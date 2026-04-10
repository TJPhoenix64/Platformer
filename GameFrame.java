
import java.awt.Color;
import javax.swing.*;

public class GameFrame extends JFrame {

    GamePanel panel;
    public static boolean isLoading;

    public GameFrame() {
        isLoading = true;

        new Thread(() -> {
            try {
                while (true) {
                    if (isLoading) {
                        System.out.println("Game loading");
                    } else {
                        System.out.println("Loading finished");
                    }
                    if (!isLoading) {
                        break;
                    }
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
            }
        }).start();
        panel = new GamePanel();
        this.add(panel);
        this.setTitle("Platformer");
        this.setResizable(false);
        this.setBackground(Color.black);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        new Thread(() -> {
            panel.secondaryInitialization();
            isLoading = false;
        }).start();

    }

}
