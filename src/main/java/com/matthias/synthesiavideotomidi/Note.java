package com.matthias.synthesiavideotomidi;

public class Note {
    private static double fps = 24;
    private static double bpm = 120;

    private static double firstNoteFrame = -1;
    
    private double startFrame;
    private double durationFrames;
    private double noteNumber;
    
    public Note(double startFrame, double durationFrames, double idx) {
        if(firstNoteFrame < 0 || startFrame < firstNoteFrame) firstNoteFrame = startFrame;
        startFrame -= firstNoteFrame;
        this.startFrame = startFrame;
        this.durationFrames = durationFrames;
//        startBeat = (startFrame / fps) * (bpm / 60.0);
//        this.duration = (durationFrames / fps) * (bpm / 60.0);
        noteNumber = idx;
    }
    
    public static void setFpsBpm(double _fps, double _bpm){
        fps = _fps;
        bpm = _bpm;
    }
    
    public int getNoteNumber(){
        return (int) noteNumber;
    }

    public double getDuration() {
        return (durationFrames / fps) * (bpm / 60.0);
    }
    
    public int getDurationTicks(int ppq){
        // for some reason, longer notes are often one 16th note too short, if
        // they are longer or equal to  a half note, so to fix that, if the note is
        // longer or equal to a half note and ends 1/16 before a down beat, lengthen the
        // duration by 1/16
        int durationTicks = (int) Math.round(getDuration() * ppq);
        
        int ticksOff = (getStartTick(ppq) + durationTicks) % ppq;
        
//        if(durationTicks > ppq){
//            if(ticksOff > ppq / 2.0)
//                return durationTicks + ppq - ticksOff;
//            if(ticksOff > 0 && ticksOff < ppq / 2.0){
//                return durationTicks + (ppq / 2) - ticksOff;
//            }
//        }

        return durationTicks;
    }

    public double getStartBeat() {
        return (startFrame / fps) * (bpm / 60.0);
    }
    public int getStartTick(int ppq){
        return (int) Math.round(getStartBeat() * ppq);
    }

    public void setDuration(double durationFrames) {
        this.durationFrames = durationFrames;
    }

    @Override
    public String toString() {
        return String.format("%.0f %.0f %.0f\n", noteNumber, getStartBeat(), getDuration());
    }

    public double getStartFrame() {
        return startFrame;
    }

    public double getDurationFrames() {
        return durationFrames;
    }
}
