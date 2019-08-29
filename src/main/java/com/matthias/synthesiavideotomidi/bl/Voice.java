package com.matthias.synthesiavideotomidi.bl;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Matthias
 */
public class Voice {

    // higher = fewer colors, lower = more differntiation between colors
    private static int tolerance = 80;

    private ArrayList<Note> notes = new ArrayList<>();
    private Color color;
    private int lhRh = 0;

    public Voice(Color color) {
        this.color = color;
    }

    public void merge(Voice other) {
        notes.addAll(other.notes);
    }

    public void addNote(Note n) {
        notes.add(n);
    }

    public static boolean isEqual(Color c1, Color c2) {
        return Math.abs(c1.getRed() - c2.getRed()) < tolerance
                && Math.abs(c1.getBlue() - c2.getBlue()) < tolerance
                && Math.abs(c1.getGreen() - c2.getGreen()) < tolerance;
    }

    /**
     * checks whether a note (actually the color of a note belongs to the track)
     *
     * @param c the color to check
     * @return true if it belongs to this track, else false
     */
    public boolean isOfTrack(Color c) {
        return isEqual(color, c);
    }

    /**
     * get all notes at beat with a tolerance of 1/4th beat
     *
     * @param beat
     * @return
     */
    public ArrayList<Note> getNotesAtBeat(double beat) {
        ArrayList<Note> beatNotes = new ArrayList<>();
        for (Note note : notes) {
            if (Math.abs(note.getStartBeat() - beat) < .25) {
                beatNotes.add(note);
            }
        }
        return beatNotes;
    }

    /**
     * returns the first note that starts after the given note has ended or 0
     * duration note with startframe equal to endframe of the given note
     *
     * @param note
     * @return
     */
    public Note getNextNote(Note note) {
        Note next = null;
        for (Note n : notes) {
            if(n.getStartFrame() >= note.getEndFrame() && (next == null || n.getStartFrame() < next.getStartFrame()))
                next = n;
        }
        if (next == null) {
            return new Note(note.getEndFrame(), 0, 0);
        }
        return next;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public int getAverageNote() {
        double average = 0;
        for (Note note : notes) {
            average += note.getNoteNumber();
        }
        average /= notes.size();
        return (int) average;
    }

    public Color getColor() {
        return color;
    }

    public void setLhRh(int balance) {
        lhRh = getAverageNote() < balance ? 0 : 1;
    }

    public static void setTolerance(int tolerance) {
        Voice.tolerance = tolerance;
    }
}
