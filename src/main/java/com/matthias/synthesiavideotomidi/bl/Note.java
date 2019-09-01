package com.matthias.synthesiavideotomidi.bl;

import java.awt.Color;

public class Note {
    private static double firstNoteFrame = -1;
    
    private double startFrame;
    private double durationFrames;
    private double noteNumber;
    private Color color;
    
    public Note(double startFrame, double durationFrames, double idx, Color color) {
        if(firstNoteFrame < 0 || startFrame < firstNoteFrame) firstNoteFrame = startFrame;
        startFrame -= firstNoteFrame;
        this.startFrame = startFrame;
        this.durationFrames = durationFrames;
        noteNumber = idx;
        this.color = color;
    }

    public int getNoteNumber(){
        return (int) noteNumber;
    }

    public double getDuration(Config config) {
        return (durationFrames / config.getFPS()) * (config.getBpm() / 60.0);
    }
    
    public int getDurationTicks(Config config){
        return (int) Math.round(getDuration(config) * config.getPpq());
    }

    public double getStartBeat(Config config) {
        return (startFrame / config.getFPS()) * (config.getBpm() / 60.0);
    }
    public double getEndBeat(Config config) {
        return (getEndFrame() / config.getFPS()) * (config.getBpm() / 60.0);
    }
    public int getStartTick(Config config){
        return (int) Math.round(getStartBeat(config) * config.getPpq());
    }
    public int getEndTick(Config config){
        return (int) Math.round(getEndBeat(config) * config.getPpq());
    }

    public void setDuration(double durationFrames) {
        this.durationFrames = durationFrames;
    }

    public double getStartFrame() {
        return startFrame;
    }

    public double getEndFrame() {
        return startFrame + durationFrames;
    }

    public double getDurationFrames() {
        return durationFrames;
    }
    
    public static double beatToFrames(double beats, Config config) {
        return beats * (60.0 / config.getBpm() ) * config.getFPS();
    }

    public Color getColor() {
        return color;
    }
}
