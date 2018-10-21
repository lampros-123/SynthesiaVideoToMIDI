package com.matthias.synthesiavideotomidi.bl;

import java.io.Serializable;

/**
 *
 * @author Matthias
 */
public class DefaultData implements Serializable{

    String videoName;
    int bpm, startFrame, ppq, c1, c2;

    public DefaultData(String videoName, int bpm, int startFrame, int ppq, int c1, int c2) {
        this.videoName = videoName;
        this.bpm = bpm;
        this.startFrame = startFrame;
        this.ppq = ppq;
        this.c1 = c1;
        this.c2 = c2;
    }

    public DefaultData(String videoName) {
        this.videoName = videoName;
        c1 = 2;
        c2 = 8;
        ppq = 4;
        bpm = 0;
    }

    public DefaultData() {
    }

    public String getVideoName() {
        return videoName;
    }

    public int getBpm() {
        return bpm;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public int getPpq() {
        return ppq;
    }

    public int getC1() {
        return c1;
    }

    public int getC2() {
        return c2;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public void setStartFrame(int startFrame) {
        this.startFrame = startFrame;
    }

    public void setPpq(int ppq) {
        this.ppq = ppq;
    }

    public void setC1(int c1) {
        this.c1 = c1;
    }

    public void setC2(int c2) {
        this.c2 = c2;
    }

}
