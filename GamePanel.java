import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import javax.imageio.*;
import javax.swing.*;

enum GameState {
    MENU, PLAYING, PAUSED, EDITING
}

enum DrawingType {
    TILES, SPIKES, CHECKPOINTS;

    public DrawingType next() {
        return values()[(ordinal() + 1) % values().length];
    }
}

public final class GamePanel extends JPanel implements Runnable {

    Thread gameThread;
    static final int PANEL_WIDTH = 1200;
    static final int PANEL_HEIGHT = 800;
    static final Dimension SCREEN_SIZE = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
    static final int TILE_SIZE = 50;
    BufferedImage platformerBackground;
    BufferedImage plainBackground;
    BufferedImage greyBackground;
    BufferedImage pausedBackground;
    BufferedImage heart;
    static Player tyler;
    static Level currentLevel = new Level();
    static HashSet<Point> solidTiles = new HashSet<>();
    Level editingLevel = new Level();
    ArrayList<ImageRect> mainMenuButtons = new ArrayList<>();
    ArrayList<ImageRect> pauseMenuButtons = new ArrayList<>();
    ArrayList<Level> levels = new ArrayList<>();
    static int numHearts = 3;
    boolean playMusic;
    static float musicVolume = 0.5f;
    Long lastTimeEffectStarted;
    double invincibilitySeconds = 1.5;

    static boolean playerHurt = false;

    private static GameState state = GameState.MENU;

    private static DrawingType type = DrawingType.TILES;

    static MusicPlayer bgMusic = new MusicPlayer();

    // final Long startTime;

    public GamePanel() {
        makePlayer();
        generateLevel();
        lastTimeEffectStarted = System.currentTimeMillis();
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

    public void generateLevel(String fileName) {
        currentLevel = generator.getContentsOfFile(fileName);
        for (Tile[] tiles : currentLevel.getBlocks()) {
            for (Tile t : tiles) {
                if (t != null) {
                    solidTiles.add(new Point(t.col, t.row));
                }
            }
        }
        for (Spike[] spikes : currentLevel.getSpikes()) {
            for (Spike s : spikes) {
                if (s != null) {
                    if (!solidTiles.contains(new Point(s.col, s.row))) {
                        solidTiles.add(new Point(s.col, s.row));
                    }
                }
            }

        }
    }

    public void generateLevels() {
        levels.add(generator.getContentsOfFile("LEVEL1.txt"));
        levels.add(generator.getContentsOfFile("LEVEL2.txt"));

        // next do pattern of levels.add(generator.getContentsOfFile("LEVEL#.txt"));
        // needs to be in order that you want to see them
        currentLevel = levels.get(0);
        updateSolidTiles(currentLevel);
    }

    /**
     * updates the Hashset of solid tiles so that the isSolidTile method works
     * 
     * @param level the level that the hashset is based off of
     */
    public static void updateSolidTiles(Level level) {
        for (Tile[] tiles : level.getBlocks()) {
            for (Tile t : tiles) {
                if (t != null) {
                    solidTiles.add(new Point(t.col, t.row));
                }
            }
        }
        for (Spike[] spikes : level.getSpikes()) {
            for (Spike s : spikes) {
                if (s != null) {
                    if (!solidTiles.contains(new Point(s.col, s.row))) {
                        solidTiles.add(new Point(s.col, s.row));
                    }
                }
            }
        }
        for (Checkpoint checkpoint : level.getCheckpoints()) {
            if (!checkpoint.isTemp) {
                if (!solidTiles.contains(new Point(checkpoint.col, checkpoint.row))) {
                    solidTiles.add(new Point(checkpoint.col, checkpoint.row));
                }
            }
        }
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
        Long currentTime = System.currentTimeMillis();

        if (currentTime - lastTimeEffectStarted > invincibilitySeconds * 1000) {
            lastTimeEffectStarted = currentTime;
            if (num > 0.5) {
                MusicPlayer.playSound("music/hurt.wav");
            } else {
                SayExample.sayPhrase("wow, you make this game look really hard");
            }
            numHearts--;
            tyler.teleport(tyler.lastCheckpointX, tyler.lastCheckpointY);
            tyler.passedCheckpointSinceButtonPress = true;
            System.out.println(tyler.getPlayerRect());
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
        if (pausedBackground != null && state == GameState.PAUSED) {
            g.drawImage(pausedBackground, 0, 0, getWidth(), getHeight(), null);
        }

        if (null != state)
            switch (state) {
                case PLAYING -> {
                    currentLevel.draw(g);
                    // line
                    g.drawRect(0, 500 + tyler.height, PANEL_WIDTH, 1);
                    // player
                    tyler.draw(g);
                    drawHearts(g);
                }
                case EDITING -> {
                    editingLevel.draw(g);
                    drawGrid(PANEL_WIDTH, PANEL_HEIGHT, g);
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
        for (int i = 0; i < PANEL_WIDTH; i += TILE_SIZE) {
            g.drawLine(i, 0, i, height);
        }

        for (int i = 0; i < height; i += TILE_SIZE) {
            g.drawLine(0, i, width, i);
        }
    }

    @SuppressWarnings("ConvertToTryWithResources")
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

            for (Tile[] tiles : level.getBlocks()) {
                for (Tile t : tiles)
                    s.append("(").append(t).append(")_");
            }
            s.deleteCharAt(s.length() - 1);
            writer.write(s.toString());
            writer.close();
        } catch (IOException e) {
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
                if (playerHurt) {
                    playerHurt = false;
                    playerHurt();
                }
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
                    playerHurt = true;
                }

            } else if (state == GameState.EDITING) {
                if (key == KeyEvent.VK_N) {
                    System.out.println("numTiles: " + editingLevel.getNumTiles());
                }

                if (key == KeyEvent.VK_D) {
                    editingLevel.clear();
                }
            }

            // change this to the filename that you want the program to make
            if (key == KeyEvent.VK_P) {
                printLevel("HELLO");
            }

            if (key == KeyEvent.VK_4) {
                for (Spike[] spikes : currentLevel.getSpikes()) {
                    for (Spike spike : spikes) {
                        if (spike != null) {
                            System.out.println(spike);
                        }
                    }
                }
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
            int row = y / TILE_SIZE;
            int col = x / TILE_SIZE;
            if (editingLevel.contains(new Tile(row, col, true))) {
                editingLevel.remove(new Tile(row, col, true));
            }
            if (!editingLevel.contains(new Tile(row, col, false))) {
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
            int row = y / TILE_SIZE;
            int col = x / TILE_SIZE;
            editingLevel.remove(new Tile(prevRow, prevCol, true));
            if (!editingLevel.contains(new Tile(row, col, false))) {
                editingLevel.addTile(new Tile(row, col, true));
            }

            prevRow = row;
            prevCol = col;

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getPoint().x;
            int y = e.getPoint().y;
            int row = y / TILE_SIZE;
            int col = x / TILE_SIZE;
            if (editingLevel.contains(new Tile(row, col, true))) {
                editingLevel.remove(new Tile(row, col, true));
            }
            if (!editingLevel.contains(new Tile(row, col, false))) {
                editingLevel.addTile(new Tile(row, col, false));
            }
        }
    }
}
