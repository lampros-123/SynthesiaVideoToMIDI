package com.matthias.synthesiavideotomidi.bl;

import com.matthias.synthesiavideotomidi.gui.LeftRightSelectionGUI;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
    public static final String defaultsFile = "defaults.ser";

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

    public void reset() {
        NoteListener.resetNotes();
        state = WAITING_TO_START;
        currentFrame = null;
        currentFrameNumber = 0;
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
        if(dist <= 0) {
            return;
        }
        
        double x = config.getC1x() - offsetNotesLeft * dist;
        int[] offsetDictionary = {0, 1, 3, 5, 7, 8, 10, 12};
        int noteNumber = config.getC1Idx() * 12 - offsetDictionary[offsetNotesLeft];

        noteListeners = new ArrayList<>();
        Color whiteColor = null;
        Color blackColor = null;
        
        while (x <= config.getC2x() + offsetNotesRight * dist) {
            // add white key
            NoteListener nl = new NoteListener(noteNumber);
            if(whiteColor == null) {
                whiteColor = new Color(currentFrame.getRGB((int) x, (int) config.getC12y()));
            }
            nl.set((int) x, (int) config.getC12y(), currentFrame, whiteColor);
            noteListeners.add(nl);
            noteNumber++;
            // if the key isn't e or h add a black key to the right of it
            if (noteNumber % 12 != 5 && noteNumber % 12 != 0) {
                double bx = x + dist / 2.0;
                nl = new NoteListener(noteNumber);
                if(blackColor == null) {
                    blackColor = new Color(currentFrame.getRGB((int) x, (int) config.getC12y()));
                }
                nl.set((int) bx, (int) config.getC12y() - config.getBlackWhiteVerticalSpacing(), currentFrame, blackColor);
                noteListeners.add(nl);
                noteNumber++;
                x += dist;
            } else {
                x = nl.getPosX() + dist;
            }
        }
        for (NoteListener nl : noteListeners) {
//            nl.center(currentFrame);
        }
    }
    
    public void NoteListenerYPositionUpdated() {
        for (NoteListener noteListener : noteListeners) {
            if(noteListener.getPosY() < (int) config.getC12y()) {
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
                LeftRightSelectionGUI gui = new LeftRightSelectionGUI(null, true, NoteListener.getVoices());
                gui.setVisible(true);
                config.setLh_rh_balance(gui.balance);

                saveDefaults();
                // merge all lh voices and all rh voices
                Voice lh = new Voice(Color.white);
                Voice rh = new Voice(Color.black);
                for (Voice voice : NoteListener.getVoices()) {
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
        if(state == WAITING_FOR_SETTINGS) {
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
                            curNote.getNoteNumber());
                    curNote.setDuration(note.getDuration());
                    voice.addNote(newNote);
                }
            }
        }
        return voice;
    }

    /**
     * TODO: Fix (not working correctly)
     * if a note ends shortly before another note (up to maxfillbeats) it is 
     * lengthend to fill the gap between the two of them in order to fix staccato errors
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
        defaults = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(defaultsFile)))) {
            Object o;
            while ((o = ois.readObject()) != null) {
                defaults.add((Config) o);
            }
        } catch (EOFException e) {
        } catch (Exception e) {
            e.printStackTrace();
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(defaultsFile)))) {
            for (Config defaultData : defaults) {
                oos.writeObject(defaultData);
            }
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
