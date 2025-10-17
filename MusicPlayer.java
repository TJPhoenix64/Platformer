import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class MusicPlayer {
    private Clip clip;

    public void playMusic(String filePath, boolean loop) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
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

    public void close() {
        if (clip != null) {
            clip.close();
        }
    }

    public static void playSound(String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}