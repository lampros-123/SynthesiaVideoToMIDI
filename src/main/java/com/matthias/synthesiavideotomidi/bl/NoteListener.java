package com.matthias.synthesiavideotomidi.bl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Matthias
 */
public class NoteListener implements Serializable{
    private ArrayList<Note> notes = new ArrayList<>();

    private int posX = -1;
    private int posY = -1;
    private int idx;

    private Color defCol = null;
    private Color prevCol = null;

    private int noteStartFrame = -1;
    private static int firstNoteFrame = -1;
    
    private boolean isWhiteNote;
    
    private Config config;

    /**
     * create notelistener from assumed position and base image
     * listener will center itself on its note
     * @param idx note index
     * @param x position
     * @param y position
     * @param img frame to center itself on
     * @param isWhiteNote determines if this note is a black or a white note
     */
    public NoteListener(int idx, int x, int y, BufferedImage img, boolean isWhiteNote, Config config) {
        this.idx = idx;
        posX = x;
        posY = y;
        center(img);
        Color color =  new Color(img.getRGB((int) posX, (int) posY));
        defCol = color;
        prevCol = color;
        this.isWhiteNote = isWhiteNote;
        this.config = config;
    }
    
    /**
     * create notelistener based on known data (e.g. csv file)
     * @param idx note index
     * @param x position
     * @param y position
     * @param defCol base color of the notelistner when no note is being played
     * @param isWhiteNote determines if this note is a black or a white note
     */
    public NoteListener(int idx, int x,  int y, Color defCol, boolean isWhiteNote, Config config) {
        this.idx = idx;
        this.posX = x;
        this.posY = y;
        this.defCol = defCol;
        this.isWhiteNote = isWhiteNote;
        this.config = config;
    }

    public static String toCSV(NoteListener listener) {
        String[] data = {
            listener.idx+"",
            listener.posX+"",
            listener.posY+"",
            listener.defCol.getRGB()+"",
            listener.isWhiteNote ? "1" : "0"
        };
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }
    
    public static NoteListener fromCSV(String csv, Config config) throws Exception {
        String[] data = csv.split(",");
        int idx = Integer.parseInt(data[0]);
        int x = Integer.parseInt(data[1]);
        int y = Integer.parseInt(data[2]);
        Color color = new Color(Integer.parseInt(data[3]));
        boolean isWhiteNote = Integer.parseInt(data[4]) > 0;
        return new NoteListener(idx, x, y, color, isWhiteNote, config);
    }

    /**
     * automatically center the notelistener on the note it landed on
     * @param img the first frame of the video
     */
    public void center(BufferedImage img){
        int leftPx = posX;
        int rightPx = posX;
        if(posX >= img.getWidth()) {
            posX = img.getWidth();
            return;
        }
        Color col = new Color(img.getRGB(posX, posY));
        
        while(leftPx >= 0 && Voice.isEqual(col, new Color(img.getRGB(leftPx, posY)), config.getColorTolerance())){
            leftPx--;
        }
        while(rightPx < img.getWidth() && Voice.isEqual(col, new Color(img.getRGB(rightPx, posY)), config.getColorTolerance())){
            rightPx++;
        }
        
        posX = (leftPx + rightPx) / 2;
    }

    /**
     * for each frame, check whether a note has begun/ended and add them to
     * the correct voices
     * @param frame The current frame as a bufferedimage
     * @param frameCount the framenumber
     * @throws Exception when the passed frame is null;
     */
    public void listen(BufferedImage frame, int frameCount) throws Exception {
        if(frame == null) throw new Exception("Frame is null");

        if (defCol == null){
            defCol = new Color(frame.getRGB(posX, posY));
            prevCol = new Color(frame.getRGB(posX, posY));
        }
        
        if (posX < 0 || posY < 0 || posX > frame.getWidth() || posY > frame.getHeight()) {
        }
        Color curCol = new Color(frame.getRGB(posX, posY));
        // if the color hasn't changed, return;
        if (Voice.isEqual(curCol, prevCol, config.getColorTolerance())) {
            return;
        }

        // unless the previous color was the default color, a note must have ended
        if (!Voice.isEqual(prevCol, defCol, config.getColorTolerance())) {
            if(firstNoteFrame < 0) firstNoteFrame = frameCount;
            Note note = new Note(noteStartFrame, frameCount - noteStartFrame, idx, prevCol);
            notes.add(note);
        }

        
        // if the current color isn't the default color a new note has started
        if (!Voice.isEqual(curCol, defCol, config.getColorTolerance())) {
            noteStartFrame = frameCount;
        }

        prevCol = curCol;
    }

    public int getPosX() {
        return posX;
    }

    /**
     * sets the x position, intended for fixing the position
     * also recenters itself;
     * @param posX the desired x position
     * @param img the first frame of the video, for centering
     */
    public void setPosX(int posX, BufferedImage img) {
        this.posX = posX;
        center(img);
    }

    public int getPosY() {
        return posY;
    }

    public Color getCurCol() {
        return prevCol;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public int getIdx() {
        return idx;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public Color getDefCol() {
        return defCol;
    }

    public void setDefCol(Color defCol) {
        this.defCol = defCol;
        this.prevCol = defCol;
    }

    public boolean isWhiteNote() {
        return isWhiteNote;
    }
    
    
}
