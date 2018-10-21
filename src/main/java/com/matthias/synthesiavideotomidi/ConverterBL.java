package com.matthias.synthesiavideotomidi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Matthias
 */
public class ConverterBL {

    public boolean paused;

    private static int LH_RH_BALANCE = 60;
    private ArrayList<NoteListener> noteListeners = new ArrayList<>();
    private BufferedImage currentFrame;
    private int currentFrameNumber;
    private File file;
    private boolean running = false;
    private int startFrame = 0;

    private int c1x, c2x, c1Idx, c2Idx, y;

    {
        c1Idx = 0;
        c2Idx = 12;
        y = c1x = c2x = -1;
    }

    public void setFile(File file) {
        this.file = file;
        updateCurrentFrame();
    }

    public void reset() {
        NoteListener.resetNotes();
        running = false;
        currentFrame = null;
        currentFrameNumber = 0;
    }

    private void updateCurrentFrame() {
        if (file == null) {
            return;
        }

        FramePlayer player = new FramePlayer(file);
        System.out.println(player.getFps());
        try {
            player.setStartFrame(startFrame);
        } catch (Exception e) {
        }
        currentFrame = player.next();
    }

    private void calculateNoteListeners(int offsetNotesLeft, int offsetNotesRight) {
        if (c1x < 0 || c2x < 0 || currentFrame == null) {
            return;
        }

        double dist = (c2x - c1x) / (c2Idx - c1Idx) / 7.0;

        double x = c1x - offsetNotesLeft * dist;
        int[] offsetDictionary = {0, 1, 3, 5, 7, 8, 10, 12};
        int noteNumber = c1Idx * 12 - offsetDictionary[offsetNotesLeft];

        noteListeners = new ArrayList<>();
        while (x <= c2x + offsetNotesRight * dist) {
            // add white key
            NoteListener nl = new NoteListener(noteNumber);
            nl.set((int) x, y, currentFrame);
            noteListeners.add(nl);
            noteNumber++;
            // if the key isn't e or h add a black key to the right of it
            if (noteNumber % 12 != 5 && noteNumber % 12 != 0) {
                nl = new NoteListener(noteNumber);
                nl.set((int) (x + dist / 2.0), y - 60, currentFrame);
                noteListeners.add(nl);
                noteNumber++;
                x = nl.getPosX() + dist / 2.0;
            } else {
                x = nl.getPosX() + dist;
            }
        }
    }

    /**
     * runs through the entire video and writes it to a midi file
     *
     * @param bpm how fast the video is
     * @throws Exception if the file is null or there aren't any noteListeners
     */
    public void convert(int bpm, int ppq, boolean maxOneParallelVoice) throws Exception {
        if (file == null || noteListeners.size() < 1) {
            throw new Exception("Not Initialised");
        }

        FramePlayer player = new FramePlayer(file);
        player.setStartFrame(startFrame);

        Note.setFpsBpm(player.getFps(), bpm);
        running = true;
        Thread t = new Thread(() -> {
            // go through all frames and save what notes are played
            try {
                currentFrameNumber = 0;
                while (player.hasNext() && running) {
//                    if(paused) continue;
                    currentFrame = player.next();
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

            if (maxOneParallelVoice) {
                lh = limitToOneVoice(lh);
                rh = limitToOneVoice(rh);
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(file);
            int result = chooser.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    String destination = chooser.getSelectedFile().toString();
                    if (!destination.endsWith(".midi")) {
                        destination += ".midi";
                    }
                    MidiWriter.write(lh, rh, destination, bpm, ppq);
                    JOptionPane.showMessageDialog(null, "saving done");
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "saving failed");
                }
            }

        });
        t.start();
    }

    /**
     * fixes the position of the nearest noteListener
     *
     * @param x desired x coordinate
     * @param y the y coordinate
     */
    public void fix(int x, int y) {
        int closest = Integer.MAX_VALUE;
        NoteListener closestNl = null;

        for (NoteListener nl : noteListeners) {
            int dx = Math.abs(x - nl.getPosX());
            int dy = Math.abs(y - nl.getPosY());
            if (dx + dy < closest) {
                closest = dx + dy;
                closestNl = nl;
            }
        }

        if (closestNl != null) {
            closestNl.setPosX(x, currentFrame);
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
                    Note newNote = new Note(curNote.getStartFrame()+ note.getDurationFrames(),
                            curNote.getDurationFrames()- note.getDurationFrames(),
                            curNote.getNoteNumber());
                    curNote.setDuration(note.getDuration());
                    voice.addNote(newNote);
                }
            }
        }
        return voice;
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
    }

    public void setC2(double x, double y, int idx, int offLeft, int offRight) {
        c2x = (int) x;
        c2Idx = (int) idx;
        this.y = (int) y;

        if (c1x >= 0) {
            calculateNoteListeners(offLeft, offRight);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public BufferedImage getCurrentFrame() {
        return currentFrame;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
        updateCurrentFrame();
    }

    public int getCurrentFrameNumber() {
        return currentFrameNumber;
    }

}
