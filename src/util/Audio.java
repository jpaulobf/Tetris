package util;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
  
public class Audio {
  
    private static final byte STOP              = 0;
    private static final byte PAUSE             = 1;
    private static final byte PLAY              = 100;
    private Clip clip                           = null;
    private byte status                         = STOP;
    private AudioInputStream audioInputStream   = null;
    private boolean ready                       = false;
    private long microsecondPosition            = 0;
  
    /**
     * Audio class
     * @param filePath
     * @param loop
     */
    public Audio(String filePath, int loop) {
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