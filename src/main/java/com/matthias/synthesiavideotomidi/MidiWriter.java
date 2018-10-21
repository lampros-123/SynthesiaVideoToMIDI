package com.matthias.synthesiavideotomidi;

import java.io.File;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 *
 * @author Matthias
 */
public class MidiWriter {

    private static int VELOCITY = 120;

    public static void write(Voice leftHand, Voice rightHand, String path, int ppq) throws Exception{
        Sequence s = new Sequence(Sequence.PPQ, ppq);
        

        ArrayList<Note> notes = null;
        for (int i = 0; i < 2; i++) {
            if(i == 0)
                notes = rightHand.getNotes();
            else
                notes = leftHand.getNotes();

            Track track = s.createTrack();

            for (Note note : notes) {
                track.add(createNoteOnEvent(note.getNoteNumber(),
                        note.getStartTick(ppq)));
                track.add(createNoteOffEvent(note.getNoteNumber(),
                        note.getStartTick(ppq) + note.getDurationTicks(ppq)));
            }
        }

        File f = new File(path);
        MidiSystem.write(s, 1, f);
    }

    private static MidiEvent createNoteOnEvent(int nKey, long lTick) {
        return createNoteEvent(ShortMessage.NOTE_ON,
                nKey,
                VELOCITY,
                lTick);
    }

    private static MidiEvent createNoteOffEvent(int nKey, long lTick) {
        return createNoteEvent(ShortMessage.NOTE_OFF,
                nKey,
                0,
                lTick);
    }

    private static MidiEvent createNoteEvent(int nCommand,
            int nKey,
            int nVelocity,
            long lTick) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(nCommand,
                    0, // always on channel 1
                    nKey,
                    nVelocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.exit(1);
        }
        MidiEvent event = new MidiEvent(message,
                lTick);
        return event;
    }
}
