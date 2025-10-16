import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import javax.swing.*;

enum GameState {
    MENU, PLAYING, PAUSED, EDITING
}

public class GamePanel extends JPanel implements Runnable {

    Thread gameThread;
    static final int width = 1200;
    static final int height = 800;
    static final Dimension SCREEN_SIZE = new Dimension(width, height);
    static final int tileSize = 50;
    BufferedImage platformerBackground;
    BufferedImage plainBackground;
    BufferedImage greyBackground;
    static Player tyler;
    ArrayList<Tile> currentLevel = new ArrayList<>();
    ArrayList<Tile> editingLevel = new ArrayList<>();
    ArrayList<ImageRect> menuButtons = new ArrayList<>();

    private static GameState state = GameState.PLAYING;

    // final Long startTime;

    public GamePanel() {
        makePlayer();
        generateLevel();
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
            platformerBackground = ImageIO.read(new File("photos/platformerBackground.jpg"));
            plainBackground = ImageIO.read(new File("photos/blueBackground.png"));
            greyBackground = ImageIO.read(new File("photos/greyBackground.jpg"));
        } catch (IOException e) {
        }

    }

    public void makePlayer() {
        tyler = new Player();
    }

    public void generateLevel() {
        currentLevel.add(new Tile(5, 5, false));
    }

    public void generateMenu() {
        Image playButton = null;
        try {
            playButton = ImageIO.read(new File("photos/PlayButton.png"));
        } catch (IOException e) {
        }
        if (playButton != null) {
            menuButtons.add(new ImageRect(playButton, 300, 200, 342, 152));
        }
    }

    public void draw(Graphics g) {
        if (plainBackground != null && state == GameState.MENU) {
            g.drawImage(plainBackground, 0, 0, getWidth(), getHeight(), null);
        }
        if (platformerBackground != null && state == GameState.PLAYING) {
            g.drawImage(platformerBackground, 0, 0, getWidth(), getHeight(), null);
        }
        if (greyBackground != null && state == GameState.EDITING) {
            g.drawImage(greyBackground, 0, 0, getWidth(), getHeight(), null);
        }
        if (null != state)
            switch (state) {
                case PLAYING -> {
                    for (Tile tile : currentLevel) {
                        tile.draw(g);
                    } // line
                    g.drawRect(0, 500 + tyler.height, width, 1);
                    // player
                    tyler.draw(g);
                }
                case EDITING -> {
                    for (Tile tile : editingLevel) {
                        tile.draw(g);
                    }
                    drawGrid(width, height, g);
                }
                case MENU -> {
                    menuButtons.clear();
                    generateMenu();
                    for (ImageRect rect : menuButtons) {
                        rect.draw(g, this);
                    }
                }
                case PAUSED -> {
                }
                default -> {
                }
            }

    }

    public void drawGrid(int width, int height, Graphics g) {
        for (int i = 0; i < width; i += tileSize) {
            g.drawLine(i, 0, i, height);
        }

        for (int i = 0; i < height; i += tileSize) {
            g.drawLine(0, i, width, i);
        }
    }

    public void printLevel(String fileName) {
        ArrayList<Tile> list;
        if (state == GameState.EDITING) {
            list = editingLevel;
        } else {
            list = currentLevel;
        }

        try {
            FileWriter writer = new FileWriter(fileName + ".txt");
            StringBuilder s = new StringBuilder();
            for (Tile tile : list) {
                s.append("(").append(tile).append(")_");
            }
            s.deleteCharAt(s.length() - 1);
            writer.write(s.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        int prevRow;
        int prevCol;

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (state == GameState.PLAYING) {

                if (key == KeyEvent.VK_UP && !tyler.isJumping) {
                    tyler.jump(System.nanoTime());
                }

                if (key == KeyEvent.VK_RIGHT) {
                    tyler.moveRight();
                }

                if (key == KeyEvent.VK_LEFT) {
                    tyler.moveLeft();
                }

            } else if (state == GameState.EDITING) {
                if (key == KeyEvent.VK_N) {
                    System.out.println("numTiles: " + editingLevel.size());
                }

                if (key == KeyEvent.VK_D) {
                    editingLevel.clear();
                }
            }

            // change this to the filename that you want the program to make
            if (key == KeyEvent.VK_P) {
                printLevel("HELLO");
            }

            if (key == KeyEvent.VK_E) {
                state = GameState.EDITING;
            }
            if (key == KeyEvent.VK_SPACE) {
                state = GameState.PLAYING;
            }
            if (key == KeyEvent.VK_ESCAPE) {
                state = GameState.PAUSED;
            }
            if (key == KeyEvent.VK_M) {
                state = GameState.MENU;
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (state == GameState.EDITING) {
                if (key == KeyEvent.VK_UP && tyler.isJumping) {
                    tyler.jumpReleased(System.nanoTime());
                }

                if (key == KeyEvent.VK_RIGHT) {
                    tyler.moveRightReleased(System.nanoTime());
                }

                if (key == KeyEvent.VK_LEFT) {
                    tyler.moveLeftReleased(System.nanoTime());
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        // MouseListener methods
        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getPoint().x;
            int y = e.getPoint().y;

            if (state == GameState.MENU) {
                for (ImageRect rect : menuButtons) {
                    if (rect.contains(x, y)) {
                        state = GameState.PLAYING;
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getPoint().x;
            int y = e.getPoint().y;
            int row = y / tileSize;
            int col = x / tileSize;
            if (editingLevel.contains(new Tile(row, col, true))) {
                editingLevel.remove(new Tile(row, col, true));
            }
            if (!editingLevel.contains(new Tile(row, col, false))) {
                editingLevel.add(new Tile(row, col, false));
            }
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
            if (state == GameState.PLAYING) {
                return;
            }

            int x = e.getPoint().x;
            int y = e.getPoint().y;
            int row = y / tileSize;
            int col = x / tileSize;
            editingLevel.remove(new Tile(prevRow, prevCol, true));
            if (!editingLevel.contains(new Tile(row, col, false))) {
                editingLevel.add(new Tile(row, col, true));
            }

            prevRow = row;
            prevCol = col;

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getPoint().x;
            int y = e.getPoint().y;
            int row = y / tileSize;
            int col = x / tileSize;
            if (editingLevel.contains(new Tile(row, col, true))) {
                editingLevel.remove(new Tile(row, col, true));
            }
            if (!editingLevel.contains(new Tile(row, col, false))) {
                editingLevel.add(new Tile(row, col, false));
            }
        }
    }
}
