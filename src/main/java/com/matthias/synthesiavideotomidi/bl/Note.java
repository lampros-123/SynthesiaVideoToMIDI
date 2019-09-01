package com.matthias.synthesiavideotomidi.bl;

import java.awt.Color;

public class Note {
    private double startFrame;
    private double durationFrames;
    private int noteNumber;
    private Color color;
    private double frameCorrection = 0;
    
    public Note(double startFrame, double durationFrames, int idx, Color color) {
        this.startFrame = startFrame;
        this.durationFrames = durationFrames;
        noteNumber = idx;
        this.color = color;
    }

    public int getNoteNumber(){
        return noteNumber;
    }

    public double getDuration(Config config) {
        return (durationFrames / config.getFPS()) * (config.getBpm() / 60.0);
    }
    
    public int getDurationTicks(Config config){
        return (int) Math.round(getDuration(config) * config.getPpq());
    }

    public double getStartBeat(Config config) {
        return (getStartFrame() / config.getFPS()) * (config.getBpm() / 60.0);
    }
    public double getEndBeat(Config config) {
        return (getEndFrame() / config.getFPS()) * (config.getBpm() / 60.0);
    }
    public int getStartTick(Config config){
        return (int) Math.round(getStartTickExact(config));
    }
    /**
     * non rounded start tick
     * @param config
     * @return 
     */
    public double getStartTickExact(Config config){
        return getStartBeat(config) * config.getPpq();
    }

    public int getEndTick(Config config){
        return (int) Math.round(getEndBeat(config) * config.getPpq());
    }

    public void setDuration(double durationFrames) {
        this.durationFrames = durationFrames;
    }
    
    public double getStartFrame() {
        return startFrame - frameCorrection;
    }

    public double getEndFrame() {
        return getStartFrame() + durationFrames;
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
    
    public void setFrameCorrection(double frameCorrection) {
        this.frameCorrection = frameCorrection;
    }

    public double getFrameCorrection() {
        return frameCorrection;
    }
    
    public void setFrameCorrectionFromTicks(double tick, Config config) {
         frameCorrection = startFrame - (tick * config.getFPS() * 60.0) / (config.getBpm() * config.getPpq()); // don't question it
    }
}
