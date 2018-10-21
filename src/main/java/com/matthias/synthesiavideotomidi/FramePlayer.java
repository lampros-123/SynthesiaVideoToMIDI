package com.matthias.synthesiavideotomidi;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

public class FramePlayer implements Iterator<BufferedImage> {

    FFmpegFrameGrabber frameGrabber;
    Java2DFrameConverter converter;
    int grabbedFrames = 0;

    public FramePlayer(File file) {
        try {
            frameGrabber = new FFmpegFrameGrabber(file);
            frameGrabber.start();
            converter = new Java2DFrameConverter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setStartFrame(int frame) throws Exception{
        if(frame < 0 )
            frame = 0;
        if(frame >= frameGrabber.getLengthInFrames())
            frame = frameGrabber.getLengthInFrames() - 1;

        frameGrabber.setFrameNumber(frame);
        if(hasNext())
            next();
    }

    public void stop() {
        try {
            frameGrabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public double getFps(){
        return (double) frameGrabber.getLengthInFrames() / ((double) frameGrabber.getLengthInTime() / 10e5);
    }

    @Override
    public boolean hasNext() {
        return grabbedFrames < frameGrabber.getLengthInFrames();
    }

    @Override
    public BufferedImage next() {
        try {
            grabbedFrames++;
            return converter.convert(frameGrabber.grabImage());
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.TreeMap;
//import org.jcodec.api.JCodecException;
//import org.jcodec.api.PictureWithMetadata;
//import org.jcodec.api.awt.AWTFrameGrab;
//import org.jcodec.common.Demuxer;
//import org.jcodec.common.DemuxerTrack;
//import org.jcodec.common.DemuxerTrackMeta;
//import org.jcodec.common.Format;
//import org.jcodec.common.JCodecUtil;
//import org.jcodec.common.io.NIOUtils;
//import org.jcodec.scale.AWTUtil;
//
///**
// *
// * @author Matthias
// */
//public class FramePlayer implements Iterator<BufferedImage>{
//
//    /**
//     * Some mp4 files don't send the frames in the correct order
//     * (reference frames) so I always store 10 frames in a buffer
//     * and take the most recent one
//     * https://github.com/jcodec/jcodec/issues/165
//     */
//    
//    private File file;
//    private TreeMap<Double, BufferedImage> reorderBuffer = new TreeMap<Double, BufferedImage>();
//    private AWTFrameGrab grab;
//
//    public FramePlayer(File file, double bpm) throws IOException, JCodecException{
//        this.file = file;
//        grab = AWTFrameGrab.createAWTFrameGrab(NIOUtils.readableChannel(file));
//        
//        for (int i = 0; i < 10; i++) {
//            PictureWithMetadata pmd = grab.getNativeFrameWithMetadata();
//            BufferedImage img = pmd.getPicture() == null ? null : AWTUtil.toBufferedImage(pmd.getPicture());
//            if(img != null)
//                reorderBuffer.put(pmd.getTimestamp(), img);
//        }
//
//        Format f = JCodecUtil.detectFormat(file);
//        Demuxer d = JCodecUtil.createDemuxer(f, file);
//        DemuxerTrack vt = d.getVideoTracks().get(0);
//        DemuxerTrackMeta dtm = vt.getMeta();
//
//        double fps = dtm.getTotalFrames() / dtm.getTotalDuration();
//        Note.setFpsBpm(fps, bpm);
//    }
//    
//    @Override
//    public boolean hasNext() {
//        return reorderBuffer.size() > 0;
//    }
//
//    @Override
//    public BufferedImage next() {
//        try{
//            PictureWithMetadata pmd = grab.getNativeFrameWithMetadata();
//            BufferedImage img = pmd.getPicture() == null ? null : AWTUtil.toBufferedImage(pmd.getPicture());
//            if(img != null)
//                reorderBuffer.put(pmd.getTimestamp(), img);
//            
//            return reorderBuffer.pollFirstEntry().getValue();
//        }catch(IOException e){
//            return null;
//        }
//    }
//
//}
