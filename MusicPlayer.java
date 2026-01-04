
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class MusicPlayer {

    private Clip clip;

    public void playMusic(String filePath, boolean loop, float volume) {
        close(); // close previous clip if any

        try (AudioInputStream audioStream
                = AudioSystem.getAudioInputStream(new File(filePath))) {

            clip = AudioSystem.getClip();
            clip.open(audioStream);

            setVolume(volume);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void setVolume(float volume) {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum(); // map 0.0–1.0 to min–max range
            gainControl.setValue(gain);
        }
    }

    public void close() {
        if (clip != null) {
            clip.close();
        }
    }

    public static void playSound(String filePath) {
        try (AudioInputStream audioStream
                = AudioSystem.getAudioInputStream(new File(filePath))) {

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            clip.start();

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}
