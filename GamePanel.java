import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    Thread gameThread;
    static final int width = 1200;
    static final int height = 800;
    static final Dimension SCREEN_SIZE = new Dimension(width, height);
    BufferedImage background;
    BufferedImage plainBackground;
    static Player tyler;

    // final Long startTime;

    public GamePanel() {
        makePlayer();
        this.setFocusable(true);
        AL listener = new AL();
        this.addKeyListener(listener);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        this.setPreferredSize(SCREEN_SIZE);
        gameThread = new Thread(this);
        gameThread.start();
        // startTime = System.nanoTime();

        try {
            background = ImageIO.read(new File("photos/platformerBackground.jpg"));
            plainBackground = ImageIO.read(new File("photos/blueBackground.png"));
        } catch (IOException e) {
        }

    }

    public void makePlayer() {
        tyler = new Player();
    }

    public void draw(Graphics g) {
        if (plainBackground != null) {
            g.drawImage(plainBackground, 0, 0, getWidth(), getHeight(), null);
        }
        tyler.draw(g);
        g.drawRect(0, 500 + tyler.height, width, 1);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0.0;
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                repaint();
                tyler.updatePosition();
                delta--;
            }
        }
    }

    public class AL implements KeyListener, MouseListener, MouseMotionListener {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_UP && !tyler.isJumping) {
                tyler.jump(System.nanoTime());
            }

            if (key == KeyEvent.VK_RIGHT) {
                tyler.moveRight();
            }

            if (key == KeyEvent.VK_LEFT) {
                tyler.moveLeft();
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_UP && !tyler.isJumping) {
                tyler.jumpReleased(System.nanoTime());
            }

            if (key == KeyEvent.VK_RIGHT) {
                tyler.moveRightReleased(System.nanoTime());
            }

            if (key == KeyEvent.VK_LEFT) {
                tyler.moveLeftReleased(System.nanoTime());
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        // MouseListener methods
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        // MouseMotionListener methods
        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }
    }
}
