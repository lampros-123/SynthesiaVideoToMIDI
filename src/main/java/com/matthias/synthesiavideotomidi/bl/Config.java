package com.matthias.synthesiavideotomidi.bl;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Matthias
 */
public class Config implements Serializable {

    private File video;
    private int bpm = 0, startFrame = 100, firstFrameOfSong = 100, ppq = 4;
    private int c1Idx = 2, c2Idx = 8, c1x = -1, c2x = -1, offLeft, offRight;
    private double scale = 1;
    private int lh_rh_balance = 60, colorTolerance = 80;
    private int blackWhiteVerticalSpacing = 40;
    private double c12y;

    public Config(File video) {
        this.video = video;
    }
    public Config() {
    }

    public File getVideo() {
        return video;
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

    public double getScale() {
        return scale;
    }

    public int getColorTolerance() {
        return colorTolerance;
    }

    public int getBlackWhiteVerticalSpacing() {
        return blackWhiteVerticalSpacing;
    }

    public int getLh_rh_balance() {
        return lh_rh_balance;
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

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setColorTolerance(int colorTolerance) {
        this.colorTolerance = colorTolerance;
    }

    public void setBlackWhiteVerticalSpacing(int blackWhiteVerticalSpacing) {
        this.blackWhiteVerticalSpacing = blackWhiteVerticalSpacing;
    }

    public void setLh_rh_balance(int lh_rh_balance) {
        this.lh_rh_balance = lh_rh_balance;
    }

    public void setFirstFrameOfSong(int firstFrameOfSong) {
        this.firstFrameOfSong = firstFrameOfSong;
    }

    public int getFirstFrameOfSong() {
        return firstFrameOfSong;
    }
}
