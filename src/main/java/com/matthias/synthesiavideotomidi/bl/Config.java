package com.matthias.synthesiavideotomidi.bl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Matthias
 */
public class Config {

    private final File video;
    private int bpm = 0;
    private int startFrame = 100;
    private int firstFrameOfSong = 100;
    private int ppq = 4;
    private int c1Idx = 2;
    private int c2Idx = 8;
    private int c1x = -1;
    private int c2x = -1;
    private int offLeft;
    private int offRight;
    private double scale = 1;
    private int lh_rh_balance = 60;
    private int colorTolerance = 80;
    private int blackWhiteVerticalSpacing = 40;
    private double c12y;
    private String[] noteListenersCSV = {};
    private int endFrame = 0;
    
    // non csv values
    private double fps = -1;

    public Config(String[] data) {
        video = new File(data[0]);
        try {
            bpm = Integer.parseInt(data[1]);
            startFrame = Integer.parseInt(data[2]);
            firstFrameOfSong = Integer.parseInt(data[3]);
            ppq = Integer.parseInt(data[4]);
            c1Idx = Integer.parseInt(data[5]);
            c2Idx = Integer.parseInt(data[6]);
            c1x = Integer.parseInt(data[7]);
            c2x = Integer.parseInt(data[8]);
            offLeft = Integer.parseInt(data[9]);
            offRight = Integer.parseInt(data[10]);
            scale = Double.parseDouble(data[11]);
            lh_rh_balance = Integer.parseInt(data[12]);
            colorTolerance = Integer.parseInt(data[13]);
            blackWhiteVerticalSpacing = Integer.parseInt(data[14]);
            c12y = Double.parseDouble(data[15]);
            noteListenersCSV = data[16].split("_");
            endFrame = Integer.parseInt(data[17]);
        } catch (Exception e) {
            System.out.println("Parsing old savedata");
        }
    }
    
    public List<NoteListener> parseNoteListeners() throws Exception {
        List<NoteListener> notelisteners = new ArrayList<>();
        for (String listenerCSV : noteListenersCSV) {
            notelisteners.add(NoteListener.fromCSV(listenerCSV, this));
        }
        return notelisteners;
    }
    
    public void setNoteListeners(List<NoteListener> notelisteners) {
        noteListenersCSV = new String[notelisteners.size()];
        for (int i = 0; i < noteListenersCSV.length; i++) {
            noteListenersCSV[i] = NoteListener.toCSV(notelisteners.get(i));
        }
    }
    
    public String toCSV() {
        String[] data = {
            video == null ? "" : video.getAbsolutePath(),
            bpm+"",
            startFrame+"",
            firstFrameOfSong+"",
            ppq+"",
            c1Idx+"",
            c2Idx+"",
            c1x+"",
            c2x+"",
            offLeft+"",
            offRight+"",
            scale+"",
            lh_rh_balance+"",
            colorTolerance+"",
            blackWhiteVerticalSpacing+"",
            c12y+"",
            Stream.of(noteListenersCSV).collect(Collectors.joining("_")),
            endFrame+""
        };
        return Stream.of(data)
            .map(this::escapeSpecialCharacters)
            .collect(Collectors.joining(","));
    }
    
    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
    
    public Config(File video) {
        this.video = video;
    }
    public Config() {
        this.video = null;
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

    public void setEndFrame(int endFrame) {
        this.endFrame = endFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public double getFPS() {
        if (video == null) {
            return 1;
        }
        if(fps < 0) {
            FramePlayer player = new FramePlayer(video);
            fps = player.getFps();
        }
        return fps;
    }
}
