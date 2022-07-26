package util;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
  
public class Audio {
  
    private static final byte STOP              = 0;
    private static final byte PAUSE             = 1;
    private static final byte PLAY              = 100;
    private Clip clip                           = null;
    private byte status                         = STOP;
    private AudioInputStream audioInputStream   = null;
    private boolean ready                       = false;
    private long microsecondPosition            = 0;
    public static final byte MUSIC              = 10;
    public static final byte SFX                = 20;
    public static final byte INCREASE           = 1;
    public static final byte DECREASE           = -1;
    private byte type                           = 0;

    public byte getType() {
        return (this.type);
    }

    /**
     * New constructor
     * @param filePath
     * @param loop
     * @param type
     */
    public Audio(String filePath, int loop, byte type) {
        this(filePath, loop);
        this.type = type;
    }

    /**
     * Audio class
     * @param filePath
     * @param loop
     */
    private Audio(String filePath, int loop) {
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (audioInputStream != null) {
            try {
                clip = AudioSystem.getClip();    
            } catch (Exception e) {}

            try {
                clip.open(audioInputStream);
                this.ready = true;
            } catch (Exception e) {
                this.ready = false;
            }
        }
    }

    public void playContinuously() {
        play(-1);
    }

    public void play() {
        play(0);
    }

    public void addVolume(float volume) {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        float value = control.getValue() + volume;
        if (value < control.getMaximum()) {
            control.setValue(value);
        }
    }

    public void decVolume(float volume) {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        float value = control.getValue() - volume;
        control.setValue(value);
    }

    public void mute() {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(control.getMinimum());
    }

    public void unmute() {
        FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(0f);
    }

    public void play(int loop) {
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setMicrosecondPosition(this.microsecondPosition);
        try {
            Thread.sleep(1);    
        } catch (Exception e) {
        }
        clip.loop(loop);
        clip.start();
        status = PLAY;
    }

    public void pause() {
        this.microsecondPosition = clip.getMicrosecondPosition();
        clip.stop();
        status = PAUSE;
    }

    public void stop() {
        clip.stop();
        this.microsecondPosition = 0;
        clip.setMicrosecondPosition(this.microsecondPosition);
        clip.setFramePosition(0);
        status = STOP;
    }

    public boolean isReady() {
        return (this.ready);
    }

    public byte getStatus() {
        return (this.status);
    }
}