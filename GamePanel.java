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
    BufferedImage pausedBackground;
    BufferedImage heart;
    static Player tyler;
    static Level currentLevel = new Level();
    Level editingLevel = new Level();
    ArrayList<ImageRect> mainMenuButtons = new ArrayList<>();
    ArrayList<ImageRect> pauseMenuButtons = new ArrayList<>();
    ArrayList<Level> levels = new ArrayList<>();
    static int numHearts = 3;
    boolean playMusic;
    static float musicVolume = 0.5f;

    private static GameState state = GameState.MENU;

    static MusicPlayer bgMusic = new MusicPlayer();

    // final Long startTime;

    public GamePanel() {
        makePlayer();
        generateLevel();
        playMusic = (state == GameState.PLAYING);
        playMusic(playMusic);
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
            heart = ImageIO.read(new File("photos/heart.png"));
            pausedBackground = ImageIO.read(new File("photos/redImage.jpg"));
        } catch (IOException e) {
        }

    }

    public void makePlayer() {
        tyler = new Player();
    }

    public void generateLevel() {
        currentLevel.addTile(new Tile(5, 5, false));
        currentLevel.addSpike(new Spike(100, 400, 200, 400, 150, 300, "photos/grass.png"));
        currentLevel.addCheckpoint(new Checkpoint(10, 10));
    }

    public void generateMainMenu() {
        Image playButton = null;
        Image settingsButton = null;
        try {
            playButton = ImageIO.read(new File("photos/PlayButton.png"));
            settingsButton = ImageIO.read(new File("photos/redImage.jpg"));
        } catch (IOException e) {
        }
        if (playButton != null) {
            mainMenuButtons.add(new ImageRect(playButton, 300, 200, 342, 152));
        }
        if (settingsButton != null) {
            mainMenuButtons.add(new ImageRect(settingsButton, 300, 400, 342, 152));
        }
    }

    public void generatePauseMenu() {

    }

    public static void passCheckpoint(Checkpoint c) {
        tyler.updateCheckpointPos(c.x, c.y);
        MusicPlayer.playSound("music/ding.wav");
    }

    public void drawHearts(Graphics g) {
        int x = 50;
        int y = 100;
        for (int i = 0; i < numHearts; i++) {
            g.drawImage(heart, x + i * 50, y, this);
        }
    }

    /**
     * this should be run whenever the player gets hurt
     */
    public void playerHurt() {
        double num = Math.random();
        if (num > 0.5) {
            MusicPlayer.playSound("music/hurt.wav");
        } else {
            SayExample.sayPhrase("wow, you make this game look really hard");
        }
        numHearts--;
        tyler.teleport(tyler.lastCheckpointX, tyler.lastCheckpointY);
        tyler.passedCheckpointSinceButtonPress = true;
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
        if (pausedBackground != null && state == GameState.PAUSED) {
            g.drawImage(pausedBackground, 0, 0, getWidth(), getHeight(), null);
        }

        if (null != state)
            switch (state) {
                case PLAYING -> {
                    currentLevel.draw(g);
                    // line
                    g.drawRect(0, 500 + tyler.height, width, 1);
                    // player
                    tyler.draw(g);
                    drawHearts(g);
                }
                case EDITING -> {
                    for (Tile tile : editingLevel.getBlocks()) {
                        tile.draw(g);
                    }
                    drawGrid(width, height, g);
                }
                case MENU -> {
                    mainMenuButtons.clear();
                    generateMainMenu();
                    for (ImageRect rect : mainMenuButtons) {
                        rect.draw(g, this);
                    }
                }
                case PAUSED -> {
                    pauseMenuButtons.clear();
                    generateMainMenu();
                    for (ImageRect rect : pauseMenuButtons) {
                        rect.draw(g, this);
                    }
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
        Level level;
        if (state == GameState.EDITING) {
            level = editingLevel;
        } else {
            level = currentLevel;
        }

        try {
            FileWriter writer = new FileWriter(fileName + ".txt");
            StringBuilder s = new StringBuilder();

            for (Tile tile : level.getBlocks()) {
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

    public static void playMusic(boolean playMusic) {
        if (playMusic) {
            bgMusic.playMusic("music/background.wav", true, musicVolume);
        } else {
            bgMusic.stopMusic();
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
                    tyler.passedCheckpointSinceButtonPress = false;
                }

                if (key == KeyEvent.VK_LEFT) {
                    tyler.moveLeft();
                    tyler.passedCheckpointSinceButtonPress = false;
                }

                if (key == KeyEvent.VK_H) {
                    playerHurt();
                }

            } else if (state == GameState.EDITING) {
                if (key == KeyEvent.VK_N) {
                    System.out.println("numTiles: " + editingLevel.getBlocks().size());
                }

                if (key == KeyEvent.VK_D) {
                    editingLevel.getBlocks().clear();
                }
            }

            // change this to the filename that you want the program to make
            if (key == KeyEvent.VK_P) {
                printLevel("HELLO");
            }

            if (key == KeyEvent.VK_SPACE && state != GameState.PLAYING) {
                state = GameState.PLAYING;
                playMusic(playMusic);
            }
            if (key == KeyEvent.VK_E) {
                state = GameState.EDITING;
                bgMusic.stopMusic();
            }
            if (key == KeyEvent.VK_ESCAPE) {
                state = GameState.PAUSED;
                bgMusic.stopMusic();

            }
            if (key == KeyEvent.VK_M) {
                state = GameState.MENU;
                bgMusic.stopMusic();

            }

            if (key == KeyEvent.VK_X) {
                playMusic = !playMusic;
                if (playMusic) {
                    bgMusic.stopMusic();
                } else {
                    playMusic(playMusic);
                }
            }

        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            if (state == GameState.PLAYING) {
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
                for (ImageRect rect : mainMenuButtons) {
                    if (rect.contains(x, y)) {
                        Image playButton = null;
                        try {
                            playButton = ImageIO.read(new File("photos/PlayButton.png"));
                        } catch (IOException a) {
                        }
                        if (playButton != null) {
                            if (rect.getBounds().equals(mainMenuButtons.get(0).getBounds())) {
                                state = GameState.PLAYING;
                            }
                            if (rect.getBounds().equals(mainMenuButtons.get(1).getBounds())) {
                                state = GameState.PAUSED;
                            }
                        }
                    }
                }
            }

            if (state == GameState.PAUSED) {
                for (ImageRect rect : pauseMenuButtons) {
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
            if (editingLevel.getBlocks().contains(new Tile(row, col, true))) {
                editingLevel.getBlocks().remove(new Tile(row, col, true));
            }
            if (!editingLevel.getBlocks().contains(new Tile(row, col, false))) {
                editingLevel.addTile(new Tile(row, col, false));
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
            editingLevel.getBlocks().remove(new Tile(prevRow, prevCol, true));
            if (!editingLevel.getBlocks().contains(new Tile(row, col, false))) {
                editingLevel.addTile(new Tile(row, col, true));
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
            if (editingLevel.getBlocks().contains(new Tile(row, col, true))) {
                editingLevel.getBlocks().remove(new Tile(row, col, true));
            }
            if (!editingLevel.getBlocks().contains(new Tile(row, col, false))) {
                editingLevel.addTile(new Tile(row, col, false));
            }
        }
    }
}
