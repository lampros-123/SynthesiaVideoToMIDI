package com.matthias.synthesiavideotomidi.bl;

import com.matthias.synthesiavideotomidi.gui.LeftRightSelectionGUI;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Matthias
 */
public class ConverterBL {

    public static int WAITING_TO_START = 0;
    public static int RUNNING = 1;
    public static int WAITING_FOR_SETTINGS = 2;
    public static int SAVING = 3;
    public static int DONE = 4;
    public static final String defaultsFile = "defaults.csv";

    public boolean paused;

    private BufferedImage currentFrame;
    private int currentFrameNumber;
    private int state = WAITING_TO_START;

    private static List<Config> defaults = new LinkedList<>();
    private Config config;
    
    private List<NoteListener> noteListeners = new ArrayList<>();

    static {
        loadDefaults();
    }

    public ConverterBL() {
        config = loadOrCreateConfig(null);
    }

    public void setFile(File file) {
        config = loadOrCreateConfig(file);
        try {
            noteListeners = config.parseNoteListeners();
        } catch (Exception ex) {
        }
        updateCurrentFrame();
    }

    public void updateCurrentFrame() {
        if (config.getVideo() == null) {
            return;
        }

        FramePlayer player = new FramePlayer(config.getVideo());
        try {
            player.setStartFrame(config.getStartFrame());
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentFrame = player.next();
    }

    /**
     * runs through the entire video and writes it to a midi file
     *
     * @param bpm how fast the video is
     * @throws Exception if the file is null or there aren't any noteListeners
     */
    public void convert(boolean maxOneParallelVoice, double staccatopadding, double autoadjustBeat) throws Exception {
        if (config.getVideo() == null || noteListeners.size() < 1) {
            throw new Exception("Not Initialised");
        }

        FramePlayer player = new FramePlayer(config.getVideo());
        player.setStartFrame(config.getStartFrame());
        state = RUNNING;
        Thread t = new Thread(() -> {
            // go through all frames and save what notes are played
            try {
                currentFrameNumber = config.getStartFrame();
                while (player.hasNext() && state == RUNNING) {
//                    if(paused) continue;
                    currentFrame = player.next();
                    currentFrameNumber++;
                    for (NoteListener noteListener : noteListeners) {
                        noteListener.listen(currentFrame, currentFrameNumber);
                    }
                    if(config.getEndFrame() > 0 && currentFrameNumber > config.getEndFrame()) {
                        state = WAITING_FOR_SETTINGS;
                    }
                }
                for (NoteListener noteListener : noteListeners) {
                    noteListener.finish(currentFrameNumber);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                player.stop();
            }

            state = WAITING_FOR_SETTINGS;
            while (state == WAITING_FOR_SETTINGS) {
                waitForSettings();
                List<Note> allNotes = new ArrayList<>();
                for (NoteListener noteListener : noteListeners) {
                    allNotes.addAll(noteListener.getNotes());
                }
                double firstNoteFrame = Integer.MAX_VALUE;
                for (Note note : allNotes) {
                    note.setFrameCorrection(0);
                }
                for (Note note : allNotes) {
                    if(note.getStartFrame() < firstNoteFrame) {
                        firstNoteFrame = note.getStartFrame();
                    }
                }
                for (Note note : allNotes) {
                    note.setFrameCorrection(firstNoteFrame);
                }
                // merge notes into voices based on the color tolerance
                List<Voice> voices = new ArrayList<>();
                for (Note note : allNotes) {
                    addToVoice:
                    {
                        // check if the note fits into an already existing voice
                        for (Voice voice : voices) {
                            // if it does, add it to that voice
                            if (voice.isOfTrack(note.getColor())) {
                                voice.addNote(note);
                                break addToVoice;
                            }
                        }
                        Voice v = new Voice(note.getColor(), config);
                        v.addNote(note);
                        voices.add(v);
                    }
                }

                LeftRightSelectionGUI gui = new LeftRightSelectionGUI(null, true, voices, config);
                gui.setVisible(true);

                saveDefaults();
                // merge all lh voices and all rh voices
                Voice lh = new Voice(Color.white, config);
                Voice rh = new Voice(Color.black, config);
                for (Voice voice : voices) {
                    if (voice.getAverageNote() > config.getLh_rh_balance()) {
                        rh.merge(voice);
                    } else {
                        lh.merge(voice);
                    }
                }

                // post edit
                if (maxOneParallelVoice) {
                    lh = limitToOneVoice(lh);
                    rh = limitToOneVoice(rh);
                }
                if(staccatopadding > 0.01) {
                    fillStaccato(lh, staccatopadding);
                    fillStaccato(rh, staccatopadding);
                }
                if (autoadjustBeat > 0) {
                    List<Note> notes = new ArrayList<Note>();
                    notes.addAll(lh.getNotes());
                    notes.addAll(rh.getNotes());
                    autoadjustBeat(notes, autoadjustBeat);
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("C:\\Users\\Matthias\\OneDrive - HTBLA Kaindorf\\Klavier\\MidiResults"));
                int result = chooser.showSaveDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        String destination = chooser.getSelectedFile().toString();
                        if (!destination.endsWith(".midi")) {
                            destination += ".midi";
                        }
                        for (Note note : lh.getNotesSorted()) {
                            System.out.println(note.getFrameCorrection());
                        }
                        for (Note note : rh.getNotesSorted()) {
                            System.out.println(note.getFrameCorrection());
                        }
                        MidiWriter.write(lh, rh, destination, config);
                        JOptionPane.showMessageDialog(null, "saving done");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "saving failed");
                    }
                }
                state = WAITING_FOR_SETTINGS;
            }
        });
        t.start();
    }

    public void configCompleted() {
        if (state == WAITING_FOR_SETTINGS) {
            state = SAVING;
        }
    }

    private void waitForSettings() {
        while (state == WAITING_FOR_SETTINGS) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void calculateNoteListeners() {
        System.out.println("calculatenotelisteners");
        if (config.getC1x() < 0 || config.getC2x() < 0 || currentFrame == null) {
            return;
        }

        double dist = (config.getC2x() - config.getC1x()) / (7.0 * (config.getC2Idx() - config.getC1Idx()));
        if (dist <= 0) {
            return;
        }

        double x = config.getC1x() - config.getOffLeft() * dist;
        int[] offsetDictionary = {0, 1, 3, 5, 7, 8, 10, 12};
        int noteNumber = config.getC1Idx() * 12 - offsetDictionary[config.getOffLeft()];

        noteListeners = new ArrayList<>();
        while (x <= config.getC2x() + config.getOffRight() * dist) {
            // add white key
            NoteListener nl = new NoteListener(noteNumber, (int) x, (int) config.getC12y(), currentFrame, true, config);
            noteListeners.add(nl);
            noteNumber++;
            // if the key isn't e or h add a black key to the right of it
            if (noteNumber % 12 != 5 && noteNumber % 12 != 0) {
                double bx = x + dist / 2.0;
                double by = config.getC12y() - config.getBlackWhiteVerticalSpacing();
                nl = new NoteListener(noteNumber, (int) bx, (int) by, currentFrame, false, config);
                noteListeners.add(nl);
                noteNumber++;
            }
            x += dist;
        }
    }
    
    public void blackWhiteVerticalSpacingUpdated() {
        for (NoteListener noteListener : noteListeners) {
            if (!noteListener.isWhiteNote()) {
                noteListener.setPosY((int) config.getC12y() - config.getBlackWhiteVerticalSpacing());
            }
        }
    }

    /**
     * fixes the position of the nearest noteListener
     *
     * @param x desired x coordinate
     * @param y the y coordinate
     */
    public void fix(double x, double y) {
        double closest = Integer.MAX_VALUE;
        NoteListener closestNl = null;

        for (NoteListener nl : noteListeners) {
            double dx = Math.abs(x - nl.getPosX());
            double dy = Math.abs(y - nl.getPosY());
            if (dx + dy < closest) {
                closest = dx + dy;
                closestNl = nl;
            }
        }

        if (closestNl != null) {
            closestNl.setPosX((int) x, currentFrame);
        }
    }

    /**
     * if to two simultaneous notes are of different length, the longer one is
     * split into two
     *
     * @param voice
     * @return
     */
    public Voice limitToOneVoice(Voice voice) {
        List<Note> notes = voice.getNotes();
        for (int i = 0; i < voice.getNotes().size(); i++) {
            Note curNote = voice.getNotes().get(i);
            List<Note> notesAtBeat = voice.getNotesAtBeat(curNote.getStartBeat(config));
            for (int j = 0; j < notesAtBeat.size(); j++) {
                Note note = notesAtBeat.get(j);
                if (note.getDuration(config) < curNote.getDuration(config) - .25) {
                    Note newNote = new Note(curNote.getStartFrame() + note.getDurationFrames(),
                            curNote.getDurationFrames() - note.getDurationFrames(),
                            curNote.getNoteNumber(), curNote.getColor());
                    curNote.setDuration(note.getDuration(config));
                    voice.addNote(newNote);
                }
            }
        }
        return voice;
    }

    /**
     * TODO: Fix (not working correctly) if a note ends shortly before another
     * note (up to maxfillbeats) it is lengthend to fill the gap between the two
     * of them in order to fix staccato errors
     *
     * @param voice
     * @param maxfillbeats
     */
    public void fillStaccato(Voice voice, double maxfillbeats) {
        for (Note note : voice.getNotes()) {
            Note next = voice.getNextNote(note);
            double padding = Math.min(next.getStartBeat(config) - note.getEndBeat(config), maxfillbeats);
            note.setDuration(note.getDuration(config) + Note.beatToFrames(padding, config));
        }
    }

    /**
     * to avoid the music slowly drifting off beat, reset the beat every note
     * so that before it can drift away it gets set back on track
     * @param notes
     * @param maxOffTick How many ticks the note can be off the actual tick in 
     * order to still be regarded as perfectly on time and taken as a 
     * new reference point for future notes
     */
    public void autoadjustBeat(List<Note> notes, double maxOffTick) {
        if(notes.isEmpty()) return;
        notes.sort((Note n1, Note n2) -> {
            if(n1.getStartFrame() < n2.getStartFrame()) return -1;
            if(n1.getStartFrame() > n2.getStartFrame()) return 1;
            return 0;
        });
        Note exactNote = notes.get(0);
        for (Note note : notes) {
            note.setFrameCorrection(exactNote.getFrameCorrection());
            double tick = note.getStartTickExact(config);
            if(Math.abs(tick - Math.round(tick)) <= maxOffTick) {
                note.setFrameCorrectionFromTicks(Math.round(tick), config);
                exactNote = note;
            }
        }
    }

    private static void loadDefaults() {
        try {
            defaults = new ArrayList<>();
            RFC4180Parser rfc4180Parser = new RFC4180ParserBuilder().build();
            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(new FileReader(defaultsFile))
                    .withCSVParser(rfc4180Parser);
            try (CSVReader csvReader = csvReaderBuilder.build()) {
                String[] values = null;
                while ((values = csvReader.readNext()) != null) {
                    defaults.add(new Config(values));
                }
            } catch (IOException ex) {
            }
        } catch (FileNotFoundException ex) {
        }
    }
    
    public void saveDataToConfig() {
        config.setNoteListeners(noteListeners);
    }

    public Config getConfig() {
        return config;
    }

    private Config loadOrCreateConfig(File file) {
        if (file == null) {
            return new Config();
        }

        for (int i = 0; i < defaults.size(); i++) {
            Config c = defaults.get(i);
            if (c.getVideo().getName().equals(file.getName())) {
                defaults.remove(c);
                defaults.add(0, c);
                return c;
            }
        }

        Config d = new Config(file);
        defaults.add(0, d); // first default is opened on startup
        return d;
    }

    public void saveDefaults() {
        // save as csv
        File csvOutputFile = new File(defaultsFile);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for (Config def : defaults) {
                String csv = def.toCSV();
                pw.println(csv);
            }
        } catch (FileNotFoundException ex) {}
    }

    public List<NoteListener> getNoteListeners() {
        return noteListeners;
    }

    /**
     * calculate the notelisteners if all the necessary values have been set
     */
    public void calculateNoteListenersIfSet() {
        if (config.getC1x() >= 0 && config.getC2x() >= 0) {
            calculateNoteListeners();
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public BufferedImage getCurrentFrame() {
        return currentFrame;
    }

    public int getCurrentFrameNumber() {
        return currentFrameNumber;
    }

    public static Config getFirstConfig() {
        if(defaults.isEmpty()) return null;
        return defaults.get(0);
    }
}
