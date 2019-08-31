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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private ArrayList<NoteListener> noteListeners = new ArrayList<>();
    private BufferedImage currentFrame;
    private int currentFrameNumber;
    private int state = WAITING_TO_START;

    private ArrayList<Config> defaults = new ArrayList<>();
    private Config config;

    public ConverterBL() {
        loadDefaults();
        config = loadOrCreateConfig(null);
    }

    public void setFile(File file) {
        config = loadOrCreateConfig(file);
        updateCurrentFrame();
    }

    private void updateCurrentFrame() {
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

    private void calculateNoteListeners(int offsetNotesLeft, int offsetNotesRight) {
        if (config.getC1x() < 0 || config.getC2x() < 0 || currentFrame == null) {
            return;
        }

        double dist = (config.getC2x() - config.getC1x()) / (7.0 * (config.getC2Idx() - config.getC1Idx()));
        if (dist <= 0) {
            return;
        }

        double x = config.getC1x() - offsetNotesLeft * dist;
        int[] offsetDictionary = {0, 1, 3, 5, 7, 8, 10, 12};
        int noteNumber = config.getC1Idx() * 12 - offsetDictionary[offsetNotesLeft];

        noteListeners = new ArrayList<>();
        while (x <= config.getC2x() + offsetNotesRight * dist) {
            // add white key
            NoteListener nl = new NoteListener(noteNumber, (int) x, (int) config.getC12y(), currentFrame);
            noteListeners.add(nl);
            noteNumber++;
            // if the key isn't e or h add a black key to the right of it
            if (noteNumber % 12 != 5 && noteNumber % 12 != 0) {
                double bx = x + dist / 2.0;
                double by = config.getC12y() - config.getBlackWhiteVerticalSpacing();
                nl = new NoteListener(noteNumber, (int) bx, (int) by, currentFrame);
                noteListeners.add(nl);
                noteNumber++;
            }
            x += dist;
        }
    }

    public void NoteListenerYPositionUpdated() {
        for (NoteListener noteListener : noteListeners) {
            if (noteListener.getPosY() < (int) config.getC12y()) {
                noteListener.setPosY((int) config.getC12y() - config.getBlackWhiteVerticalSpacing());
            }
        }
    }

    /**
     * runs through the entire video and writes it to a midi file
     *
     * @param bpm how fast the video is
     * @throws Exception if the file is null or there aren't any noteListeners
     */
    public void convert(boolean maxOneParallelVoice, double staccatopadding) throws Exception {
        if (config.getVideo() == null || noteListeners.size() < 1) {
            throw new Exception("Not Initialised");
        }

        FramePlayer player = new FramePlayer(config.getVideo());
        player.setStartFrame(config.getStartFrame());

        Note.setFps(player.getFps());
        Voice.setTolerance(config.getColorTolerance());
        state = RUNNING;
        Thread t = new Thread(() -> {
            // go through all frames and save what notes are played
            try {
                currentFrameNumber = 0;
                while (player.hasNext() && state == RUNNING && (currentFrame = player.next()) != null) {
//                    if(paused) continue;
                    for (NoteListener noteListener : noteListeners) {
                        noteListener.listen(currentFrame, currentFrameNumber);
                    }
                    currentFrameNumber++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                player.stop();
            }

            state = WAITING_FOR_SETTINGS;
            while (state == WAITING_FOR_SETTINGS) {
                waitForSettings();
                Voice.setTolerance(config.getColorTolerance());

                // merge notes into voices based on the color tolerance
                ArrayList<Note> allNotes = new ArrayList<>();
                for (NoteListener noteListener : noteListeners) {
                    allNotes.addAll(noteListener.getNotes());
                }
                ArrayList<Voice> voices = new ArrayList<>();
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
                        Voice v = new Voice(note.getColor());
                        v.addNote(note);
                        voices.add(v);
                    }
                }

                LeftRightSelectionGUI gui = new LeftRightSelectionGUI(null, true, voices);
                gui.setVisible(true);
                config.setLh_rh_balance(gui.balance);

                saveDefaults();
                // merge all lh voices and all rh voices
                Voice lh = new Voice(Color.white);
                Voice rh = new Voice(Color.black);
                for (Voice voice : voices) {
                    if (voice.getAverageNote() > config.getLh_rh_balance()) {
                        rh.merge(voice);
                    } else {
                        lh.merge(voice);
                    }
                }
                Note.setBpm(config.getBpm());

                if (maxOneParallelVoice) {
                    lh = limitToOneVoice(lh);
                    rh = limitToOneVoice(rh);
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
                        MidiWriter.write(lh, rh, destination, config.getPpq());
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
        ArrayList<Note> notes = voice.getNotes();
        for (int i = 0; i < voice.getNotes().size(); i++) {
            Note curNote = voice.getNotes().get(i);
            ArrayList<Note> notesAtBeat = voice.getNotesAtBeat(curNote.getStartBeat());
            for (int j = 0; j < notesAtBeat.size(); j++) {
                Note note = notesAtBeat.get(j);
                if (note.getDuration() < curNote.getDuration() - .25) {
                    Note newNote = new Note(curNote.getStartFrame() + note.getDurationFrames(),
                            curNote.getDurationFrames() - note.getDurationFrames(),
                            curNote.getNoteNumber(), curNote.getColor());
                    curNote.setDuration(note.getDuration());
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
     * @return
     */
    public void fillStaccato(Voice voice, double maxfillbeats) {
        for (Note note : voice.getNotes()) {
            Note next = voice.getNextNote(note);
            System.out.println(note.getEndBeat() + " " + note.getStartBeat());
            double padding = Math.min(next.getStartBeat() - note.getEndBeat(), maxfillbeats);
            note.setDuration(note.getDuration() + Note.beatToFrames(padding));
        }
    }

    private void loadDefaults() {
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

    public Config getConfig() {
        return config;
    }

    private Config loadOrCreateConfig(File file) {
        if (file == null) {
            return new Config();
        }

        for (Config config : defaults) {
            if (config.getVideo().getName().equals(file.getName())) {
                return config;
            }
        }

        Config d = new Config(file);
        defaults.add(d);
        return d;
    }

    public void saveDefaults() {
        File csvOutputFile = new File(defaultsFile);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for (Config def : defaults) {
                String csv = def.toCSV();
                pw.println(csv);
            }
        } catch (FileNotFoundException ex) {}
    }

    public ArrayList<NoteListener> getNoteListeners() {
        return noteListeners;
    }

    public void setC1(double x, double y, int idx, int offLeft, int offRight) {
        config.setC1x((int) x);
        config.setC1Idx(idx);
        config.setC12y((int) y);
        config.setOffLeft(offLeft);
        config.setOffRight(offRight);

        if (config.getC2x() >= 0) {
            calculateNoteListeners(offLeft, offRight);
        }
    }

    public void setC2(double x, double y, int idx, int offLeft, int offRight) {
        config.setC2x((int) x);
        config.setC2Idx(idx);
        config.setC12y((int) y);
        config.setOffLeft(offLeft);
        config.setOffRight(offRight);

        if (config.getC1x() >= 0) {
            calculateNoteListeners(offLeft, offRight);
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

    public void setStartFrame(int startFrame) {
        config.setStartFrame(startFrame);
        updateCurrentFrame();
    }

    public int getCurrentFrameNumber() {
        return currentFrameNumber;
    }

    public double getFPS() {
        if (config.getVideo() == null) {
            return 1;
        }
        FramePlayer player = new FramePlayer(config.getVideo());
        return player.getFps();
    }
}
