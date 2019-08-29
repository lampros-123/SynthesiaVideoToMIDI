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
    public static int DONE = 3;

    public boolean paused;

    private static int LH_RH_BALANCE = 60;
    private ArrayList<NoteListener> noteListeners = new ArrayList<>();
    private BufferedImage currentFrame;
    private int currentFrameNumber;
    private File file;
    private int state = WAITING_TO_START;
    private int startFrame = 0;
    private int blackWhiteVerticalSpacing = 40;

    private int bpm, ppq = 0;

    private int c1x, c2x, c1Idx, c2Idx, y;

    private ArrayList<DefaultData> defaults = new ArrayList<>();
    private String defaultsFile = "defaults.ser";

    public ConverterBL() {
        c1Idx = 0;
        c2Idx = 12;
        y = c1x = c2x = -1;

        loadDefaults();
    }

    public DefaultData setFile(File file) {
        this.file = file;
        updateCurrentFrame();
        return getDefault();
    }

    public void reset() {
        NoteListener.resetNotes();
        state = WAITING_TO_START;
        currentFrame = null;
        currentFrameNumber = 0;
    }

    private void updateCurrentFrame() {
        if (file == null) {
            return;
        }

        FramePlayer player = new FramePlayer(file);
        try {
            player.setStartFrame(startFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentFrame = player.next();
    }

    private void calculateNoteListeners(int offsetNotesLeft, int offsetNotesRight) {
        if (c1x < 0 || c2x < 0 || currentFrame == null) {
            return;
        }

        double dist = (c2x - c1x) / (7.0 * (c2Idx - c1Idx));
        if(dist <= 0) {
            return;
        }
        
        double x = c1x - offsetNotesLeft * dist;
        int[] offsetDictionary = {0, 1, 3, 5, 7, 8, 10, 12};
        int noteNumber = c1Idx * 12 - offsetDictionary[offsetNotesLeft];

        noteListeners = new ArrayList<>();
        Color whiteColor = null;
        Color blackColor = null;
        
        while (x <= c2x + offsetNotesRight * dist) {
            // add white key
            NoteListener nl = new NoteListener(noteNumber);
            if(whiteColor == null) {
                whiteColor = new Color(currentFrame.getRGB((int) x, y));
            }
            nl.set((int) x, y, currentFrame, whiteColor);
            noteListeners.add(nl);
            noteNumber++;
            // if the key isn't e or h add a black key to the right of it
            if (noteNumber % 12 != 5 && noteNumber % 12 != 0) {
                double bx = x + dist / 2.0;
                nl = new NoteListener(noteNumber);
                if(blackColor == null) {
                    blackColor = new Color(currentFrame.getRGB((int) x, y));
                }
                nl.set((int) bx, y - blackWhiteVerticalSpacing, currentFrame, blackColor);
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

    /**
     * runs through the entire video and writes it to a midi file
     *
     * @param bpm how fast the video is
     * @throws Exception if the file is null or there aren't any noteListeners
     */
    public void convert(boolean maxOneParallelVoice, double staccatopadding) throws Exception {
        if (file == null || noteListeners.size() < 1) {
            throw new Exception("Not Initialised");
        }

        FramePlayer player = new FramePlayer(file);
        player.setStartFrame(startFrame);

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

            LeftRightSelectionGUI gui = new LeftRightSelectionGUI(null, true, NoteListener.getVoices());
            gui.setVisible(true);

            // merge all lh voices and all rh voices
            Voice lh = new Voice(Color.white);
            Voice rh = new Voice(Color.black);
            for (Voice voice : NoteListener.getVoices()) {
                if (voice.getAverageNote() > LH_RH_BALANCE) {
                    rh.merge(voice);
                } else {
                    lh.merge(voice);
                }
            }

            state = WAITING_FOR_SETTINGS;
            while (state == WAITING_FOR_SETTINGS) {
                waitForSettings();
                saveDefaults();
                if (state != WAITING_FOR_SETTINGS) {
                    break;
                }
                Note.setBpm(bpm);

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
                        MidiWriter.write(lh, rh, destination, ppq);
                        JOptionPane.showMessageDialog(null, "saving done");
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "saving failed");
                    }
                }
                state = DONE;
            }
        });
        t.start();
    }

    public boolean setBpmPpq(int bpm, int ppq) {
        if (bpm < 1 || ppq < 1) {
            return false;
        }
        this.bpm = bpm;
        this.ppq = ppq;
        getDefault().setBpm(bpm);
        getDefault().setPpq(ppq);
        return true;
    }
    
    public void setScaleColorTolerance(int scale, int colorTolerance) {
        getDefault().setScale(scale);
        getDefault().setColorTolerance(colorTolerance);
    }

    private void waitForSettings() {
        bpm = 0;
        ppq = 0;
        while (bpm == 0 || ppq == 0) {
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
                defaults.add((DefaultData) o);
            }
        } catch (EOFException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDefaults() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(defaultsFile)))) {
            for (DefaultData defaultData : defaults) {
                oos.writeObject(defaultData);
            }
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DefaultData getDefault() {
        if (file == null) {
            return new DefaultData();
        }

        for (DefaultData defaultData : defaults) {
            if (defaultData.getVideoName().equals(file.getName())) {
                return defaultData;
            }
        }

        DefaultData d = new DefaultData(file.getName());
        defaults.add(d);
        return d;
    }

    public ArrayList<NoteListener> getNoteListeners() {
        return noteListeners;
    }

    public void setC1(double x, double y, int idx, int offLeft, int offRight) {
        c1x = (int) x;
        c1Idx = (int) idx;
        this.y = (int) y;

        if (c2x >= 0) {
            calculateNoteListeners(offLeft, offRight);
        }
        
        DefaultData d = getDefault();
        d.setC1x(c1x);
        d.setC12y(y);
        d.setC1Idx(idx);
        d.setOffLeft(offLeft);
        d.setOffRight(offRight);
    }

    public void setC2(double x, double y, int idx, int offLeft, int offRight) {
        c2x = (int) x;
        c2Idx = (int) idx;
        this.y = (int) y;

        if (c1x >= 0) {
            calculateNoteListeners(offLeft, offRight);
        }
        
        DefaultData d = getDefault();
        d.setC2x(c2x);
        d.setC12y(y);
        d.setC2Idx(idx);
        d.setOffLeft(offLeft);
        d.setOffRight(offRight);
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
        this.startFrame = startFrame;
        updateCurrentFrame();
        getDefault().setStartFrame(startFrame);
    }

    public int getCurrentFrameNumber() {
        return currentFrameNumber;
    }

}
