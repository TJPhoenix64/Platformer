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
    TILES, SPIKES, CHECKPOINTS, COINS;

    public DrawingType next() {
        return values()[(ordinal() + 1) % values().length];
    }
}

public final class GamePanel extends JPanel implements Runnable {

    Thread gameThread;
    LevelGenerator generator = new LevelGenerator();
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
    static ArrayList<Level> levels = new ArrayList<>();
    static int currentLevelNum = 0;
    static int numHearts = 3;
    boolean playMusic;
    static float musicVolume = 0.8f;
    static float tempMusicVolume = 0.8f;
    static float tempTransparency = 0.4f;
    Long lastTimeEffectStarted;
    double invincibilitySeconds = 1.5;

    static int tempSpikeRotation = 0;

    static boolean playerHurt = false;

    private static GameState state = GameState.MENU;

    private static DrawingType type = DrawingType.TILES;

    static MusicPlayer bgMusic = new MusicPlayer();

    File folder = new File("levels");

    // final Long startTime;

    public GamePanel() {
        makePlayer();
        generateLevels();
        lastTimeEffectStarted = System.currentTimeMillis();
        playMusic = (state == GameState.PLAYING);
        playMusic();
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
        File[] files = folder.listFiles();

        for (int i = 1; i < files.length + 1; i++) {
            levels.add(generator.getContentsOfFile("levels/LEVEL" + i + ".txt"));
        }
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
        solidTiles.clear();
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

        for (Coin coin : level.getCoins()) {
            if (!solidTiles.contains(new Point(coin.col, coin.row))) {
                solidTiles.add(new Point(coin.col, coin.row));
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
        Image playButton = null;
        Image settingsButton = null;
        try {
            playButton = ImageIO.read(new File("photos/PlayButton.png"));
            settingsButton = ImageIO.read(new File("photos/orangeBackground.jpg"));
        } catch (IOException e) {
        }
        if (playButton != null) {
            pauseMenuButtons.add(new ImageRect(playButton, 300, 200, 342, 152));
        }
        if (settingsButton != null) {
            pauseMenuButtons.add(new ImageRect(settingsButton, 300, 400, 342, 152));
        }
    }

    public static void passCheckpoint(Checkpoint c) {
        tyler.updateCheckpointPos(c.x, c.y);
        MusicPlayer.playSound("music/ding.wav");
    }

    public void drawHearts(Graphics g) {
        int x = 50;
        int y = 50;
        for (int i = 0; i < numHearts; i++) {
            g.drawImage(heart, x + i * 50, y, (int) (heart.getWidth() * 2.5), (int) (heart.getHeight() * 2.5), this);
        }
    }

    public static void changeLevel(boolean forward) {
        if (forward && currentLevelNum != levels.size() - 1) {
            currentLevelNum++;
        }
        if (!forward && currentLevelNum != 0) {
            currentLevelNum--;
        }
        currentLevel = levels.get(currentLevelNum);
        updateSolidTiles(currentLevel);
    }

    public static void advanceLevel() {
        changeLevel(true);
    }

    public static void rewindLevel() {
        changeLevel(false);
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

            s.append("Tiles:");
            for (Tile[] tiles : level.getBlocks()) {
                for (Tile t : tiles) {
                    if (t != null && !t.isTemp) {
                        s.append(t).append("_");
                    }
                }
            }

            s.deleteCharAt(s.length() - 1);
            s.append("\nSpikes:");
            for (Spike[] spikes : level.getSpikes()) {
                for (Spike spike : spikes) {
                    if (spike != null && !spike.isTemp) {
                        s.append(spike).append("_");
                    }
                }
            }

            s.deleteCharAt(s.length() - 1);
            s.append("\nCheckpoints:");
            for (Checkpoint checkpoint : level.getCheckpoints()) {
                if (!checkpoint.isTemp) {
                    s.append(checkpoint).append("_");
                }
            }
            s.deleteCharAt(s.length() - 1);

            s.append("\nCoins:");
            for (Coin coin : level.getCoins()) {
                if (!coin.isTemp) {
                    s.append(coin).append("_");
                }
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
                if (state == GameState.PLAYING) {
                    tyler.updatePosition();
                    if (playerHurt) {
                        playerHurt = false;
                        playerHurt();
                    }
                }
                delta--;
            }
        }
    }

    public static void playMusic() {
        if (state == GameState.PLAYING) {
            bgMusic.playMusic("music/background.wav", true, musicVolume);
        } else {
            bgMusic.stopMusic();
        }
    }

    /**
     * checks if there is an object in that grid location: x, y
     * 
     * @param col
     * @param row
     * @return
     */
    public static boolean isSolidTile(int col, int row) {
        if (!solidTiles.isEmpty()) {
            return solidTiles.contains(new Point(col, row));
        }
        return false;
    }

    public class AL implements KeyListener, MouseListener, MouseMotionListener {

        int prevRow = -1;
        int prevCol = -1;

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

                if (key == KeyEvent.VK_A) {
                    advanceLevel();
                }

                if (key == KeyEvent.VK_T) {
                    for (Point point : solidTiles) {
                        System.out.println("Point: " + point.x + " " + point.y);
                    }
                }

                if (key == KeyEvent.VK_G) {
                    for (Tile t : tyler.nearbyTiles) {
                        System.out.println(t);
                    }
                }

            } else if (state == GameState.EDITING) {
                if (key == KeyEvent.VK_N) {
                    System.out.println("numTiles: " + editingLevel.getNumObjects());
                }

                if (key == KeyEvent.VK_D) {
                    editingLevel.clear();
                }

                if (key == KeyEvent.VK_2) {
                    type = type.next();
                }

                if (key == KeyEvent.VK_1) {
                    tempSpikeRotation += 90;
                    if (tempSpikeRotation == 360) {
                        tempSpikeRotation = 0;
                    }
                }
            }

            // change this to the filename that you want the program to make
            if (key == KeyEvent.VK_P) {
                printLevel("levels/LEVEL2");
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
                currentLevel = levels.get(currentLevelNum);
                updateSolidTiles(currentLevel);
                playMusic();
            }
            if (key == KeyEvent.VK_E) {
                state = GameState.EDITING;
                currentLevel = editingLevel;
                updateSolidTiles(currentLevel);
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
                    playMusic();
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
                                playMusic();
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
            if (state != GameState.EDITING) {
                return;
            }
            System.out.println("MousePressed");
            int x = e.getPoint().x;
            int y = e.getPoint().y;
            int row = y / TILE_SIZE;
            int col = x / TILE_SIZE;
            if (editingLevel.containsTemp(col, row)) {
                editingLevel.remove(col, row);
            }
            if (!isSolidTile(col, row)) {
                switch (type) {
                    case TILES:
                        editingLevel.addObject(new Tile(col, row, false));
                        break;
                    case SPIKES:
                        editingLevel.addObject(new Spike(col, row, tempSpikeRotation, false));
                        break;
                    case CHECKPOINTS:
                        editingLevel.addObject(new Checkpoint(col, row, false));
                        break;
                    case COINS:
                        editingLevel.addObject(new Coin(col, row, false));
                        break;
                    default:
                        break;
                }
            } else {
                editingLevel.remove(col, row);
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
            if (state != GameState.EDITING) {
                return;
            }

            int x = e.getPoint().x;
            int y = e.getPoint().y;
            int row = y / TILE_SIZE;
            int col = x / TILE_SIZE;
            if (prevRow != -1 && prevCol != -1) {
                if (editingLevel.containsTemp(prevCol, prevRow)) {
                    editingLevel.remove(prevCol, prevRow);
                }
            }
            if (!editingLevel.contains(col, row)) {
                if (null != type) {
                    switch (type) {
                        case TILES:
                            editingLevel.addObject(new Tile(col, row, true));
                            break;
                        case SPIKES:
                            editingLevel.addObject(new Spike(col, row, tempSpikeRotation, true));
                            break;
                        case CHECKPOINTS:
                            editingLevel.addObject(new Checkpoint(col, row, true));
                            break;
                        case COINS:
                            editingLevel.addObject(new Coin(col, row, true));
                            break;
                        default:
                            break;
                    }
                }
            }

            prevRow = row;
            prevCol = col;

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (state != GameState.EDITING) {
                return;
            }
            System.out.println("Dragged");
            int x = e.getPoint().x;
            int y = e.getPoint().y;
            int row = y / TILE_SIZE;
            int col = x / TILE_SIZE;

            // System.out.println("col: " + col + " row: " + row);
            System.out.println("Obj: " + editingLevel.get(col, row));
            if (editingLevel.containsTemp(col, row)) {
                editingLevel.remove(col, row);
            }
            if (!editingLevel.contains(col, row)) {
                System.out.println("new grid");
                if (null != type) {
                    switch (type) {
                        case TILES:
                            editingLevel.addObject(new Tile(col, row, false));
                            break;
                        case SPIKES:
                            editingLevel.addObject(new Spike(col, row, tempSpikeRotation, false));
                            break;
                        case CHECKPOINTS:
                            editingLevel.addObject(new Checkpoint(col, row, false));
                            break;
                        case COINS:
                            editingLevel.addObject(new Coin(col, row, false));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
