package com.matthias.synthesiavideotomidi.bl;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Matthias
 */
public class DefaultData implements Serializable {

    private String videoName;
    private int bpm, startFrame, ppq;
    private int c1Idx, c2Idx, c1x, c2x, offLeft, offRight;
    double c12y;

    public DefaultData(String videoName, int bpm, int startFrame, int ppq, int c1Idx, int c2Idx, int c1, int c2, double c12y, int offLeft, int offRight) {
        this.videoName = videoName;
        this.bpm = bpm;
        this.startFrame = startFrame;
        this.ppq = ppq;
        this.c1Idx = c1Idx;
        this.c2Idx = c2Idx;
        this.c1x = c1;
        this.c2x = c2;
        this.c12y = c12y;
        this.offLeft = offLeft;
        this.offRight = offRight;
    }

    public DefaultData(String videoName) {
        this.videoName = videoName;
        c1x = 2;
        c2x = 8;
        ppq = 4;
        startFrame = 100;
        bpm = 0;
        c1Idx = -1;
        c2Idx = -1;
    }

    public DefaultData() {
        c1x = 2;
        c2x = 8;
        ppq = 4;
        startFrame = 100;
        bpm = 0;
        c1Idx = -1;
        c2Idx = -1;
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

    public int getC1x() {
        return c1x;
    }

    public int getC2x() {
        return c2x;
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

    public void setC1x(int c1) {
        this.c1x = c1;
    }

    public void setC2x(int c2) {
        this.c2x = c2;
    }

    public int getC1Idx() {
        return c1Idx;
    }

    public int getC2Idx() {
        return c2Idx;
    }

    public double getC12y() {
        return c12y;
    }

    public int getOffLeft() {
        return offLeft;
    }

    public int getOffRight() {
        return offRight;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setC1Idx(int c1Idx) {
        this.c1Idx = c1Idx;
    }

    public void setC2Idx(int c2Idx) {
        this.c2Idx = c2Idx;
    }

    public void setC12y(double c12y) {
        this.c12y = c12y;
    }

    public void setOffLeft(int offLeft) {
        this.offLeft = offLeft;
    }

    public void setOffRight(int offRight) {
        this.offRight = offRight;
    }

    
}
