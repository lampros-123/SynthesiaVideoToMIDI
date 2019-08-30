package com.matthias.synthesiavideotomidi.bl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

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

    public NoteListener(int idx, int x, int y, BufferedImage img) {
        this.idx = idx;
        posX = x;
        posY = y;
        center(img);
        Color color =  new Color(img.getRGB((int) x, (int) y));
        defCol = color;
        prevCol = color;
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
        
        while(leftPx >= 0 && Voice.isEqual(col, new Color(img.getRGB(leftPx, posY)))){
            leftPx--;
        }
        while(rightPx < img.getWidth() && Voice.isEqual(col, new Color(img.getRGB(rightPx, posY)))){
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
        if (Voice.isEqual(curCol, prevCol)) {
            return;
        }

        // unless the previous color was the default color, a note must have ended
        if (!Voice.isEqual(prevCol, defCol)) {
            if(firstNoteFrame < 0) firstNoteFrame = frameCount;
            Note note = new Note(noteStartFrame, frameCount - noteStartFrame, idx, prevCol);
            notes.add(note);
        }

        
        // if the current color isn't the default color a new note has started
        if (!Voice.isEqual(curCol, defCol)) {
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
    }
}
