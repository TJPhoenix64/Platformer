import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    Thread gameThread;
    static final int width = 1200;
    static final int height = 800;
    static final Dimension SCREEN_SIZE = new Dimension(width, height);
    BufferedImage background;

    public GamePanel() {
        this.setFocusable(true);
        AL listener = new AL();
        this.addKeyListener(listener);
        addMouseListener(listener);
        addMouseMotionListener(listener);
        this.setPreferredSize(SCREEN_SIZE);
        gameThread = new Thread(this);
        gameThread.start();

        /*
         * try {
         * //background = ImageIO.read(new File("Photos/chess-board.png"));
         * } catch (IOException e) {
         * }
         */

    }

    public void draw(Graphics g) {

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }
        /*
         * for (Piece elem : pieceList) {
         * elem.draw(g);
         * }
         * for (MoveOption elem : selectedPieceMovesList) {
         * elem.draw(g);
         * }
         * if (showPsuedoMoves) {
         * for (MoveOption elem : psuedoLegalMovesList) {
         * elem.draw(g);
         * }
         * }
         */

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
                delta--;
            }
        }
    }

    public class AL implements KeyListener, MouseListener, MouseMotionListener {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
        }

        @Override
        public void keyReleased(KeyEvent e) {
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
