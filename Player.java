
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends Rectangle {
    int width;
    int height;
    int x;
    int y;
    boolean isJumping;
    boolean jumpReleased = true;
    Image image;
    Long startJumpTime;
    Long endJumpTime;
    double gravity = -0.05;
    double initialVeloY;
    boolean moveLeftPressed;
    boolean moveRightPressed;
    boolean moveLeftReleased;
    boolean moveRightReleased;
    Long endLeftTime;
    Long endRightTime;
    int initialVeloX = 10;
    int lastCheckpointX;
    int lastCheckpointY;
    boolean passedCheckpointSinceButtonPress = false;

    public Player() {
        this.width = GamePanel.tileSize;
        this.height = GamePanel.tileSize;
        this.x = 100;
        this.y = 100;
        this.lastCheckpointX = this.x;
        this.lastCheckpointY = this.y;

        try {
            image = ImageIO.read(new File("photos/redImage.jpg"));
        } catch (IOException e) {
        }
    }

    public void updateCheckpointPos(int x, int y) {
        this.lastCheckpointX = x;
        this.lastCheckpointY = y;
    }

    public void checkCheckpoints() {
        int col = x / GamePanel.tileSize;
        int row = y / GamePanel.tileSize;
        if (GamePanel.currentLevel.getCheckpoints().contains(new Checkpoint(row, col))) {
            GamePanel.passCheckpoint(new Checkpoint(row, col));
        }

    }

    /**
     * this teleports the player, it should only be used for when they take damage
     * and reset back to the last checkpoint
     * 
     * @param x the x-pos
     * @param y the y-pos
     */
    public void teleport(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveLeft() {
        this.moveLeftPressed = true;
        this.moveLeftReleased = false;
        this.moveRightPressed = false;
        this.moveRightReleased = false;
    }

    public void moveRight() {
        this.moveRightPressed = true;
        this.moveRightReleased = false;
        this.moveLeftReleased = false;
        this.moveLeftPressed = false;
    }

    public void moveLeftReleased(Long timeReleased) {
        this.endLeftTime = timeReleased;
        this.moveLeftPressed = false;
        this.moveLeftReleased = true;
        this.endRightTime = 0L;
    }

    public void moveRightReleased(Long timeReleased) {
        this.endRightTime = timeReleased;
        this.moveRightPressed = false;
        this.moveRightReleased = true;
        this.endLeftTime = 0L;
    }

    public void jump(Long startTime) {
        startJumpTime = startTime;
        isJumping = true;
        jumpReleased = false;
        endJumpTime = 0L;
    }

    public void jumpReleased(Long time) {
        endJumpTime = time;
        jumpReleased = true;
    }

    public double getInitialVeloY() {
        double num = 10;
        double diffMilis;
        if (jumpReleased) {
            diffMilis = getDiffMillis(startJumpTime, endJumpTime);
        } else {
            Long currentTime = System.nanoTime();
            diffMilis = getDiffMillis(startJumpTime, currentTime);
        }
        num += diffMilis / 10.0;
        if (num > 30) {
            num = 30;
        }

        return num;
    }

    public int getDiffMillis(Long startTime, Long endTime) {
        Long diff = endTime - startTime;
        int diffMilis = (int) (diff / 1000000);
        return diffMilis;
    }

    public void updatePosition() {
        checkCheckpoints();
        Long currentTime = System.nanoTime();
        if (isJumping) {
            int timeSinceJumpStarted = getDiffMillis(startJumpTime, currentTime);
            initialVeloY = getInitialVeloY();
            int dy = (int) (gravity * timeSinceJumpStarted + initialVeloY);
            // System.out.println("dy: " + dy);
            // ensures the block does not go below 500
            if (this.y - dy > 500) {
                this.y = 500;
            } else {
                this.y -= dy;
            }
        }
        // change this to the y of the proper block, not 500
        if (this.y >= 500) {
            isJumping = false;
        }

        if (moveLeftPressed) {
            this.x -= initialVeloX;
        }

        // add it so that if moveLeftReleased, check how long its been since released,
        // decrease velo by set amount

        if (moveRightPressed) {
            this.x += initialVeloX;
        }

        if (moveLeftReleased && !passedCheckpointSinceButtonPress) {
            int timeSinceReleased = getDiffMillis(endLeftTime, currentTime);
            if (timeSinceReleased < 500) {
                if (this.isJumping) {
                    this.x -= initialVeloX;
                    endLeftTime = System.nanoTime();
                } else {
                    this.x -= (initialVeloX - (timeSinceReleased / 50));
                }
            } else {
                moveLeftReleased = false;
            }
        }

        if (moveRightReleased && !passedCheckpointSinceButtonPress) {
            int timeSinceReleased = getDiffMillis(endRightTime, currentTime);
            if (timeSinceReleased < 500) {
                if (this.isJumping) {
                    this.x += initialVeloX;
                    endRightTime = System.nanoTime();
                } else {
                    this.x += (initialVeloX - (timeSinceReleased / 50));
                }
            } else {
                moveRightReleased = false;
            }
        }

    }

    /**
     * this method will handle changing the players position, it will handle
     * collisions
     * 
     * @param delta number of pixels to be changed
     * @param isX   true if changing x, false if changing y
     */
    public void changePosition(int delta, boolean isX) {
        int leftTile = this.x / GamePanel.tileSize; // left edge of player
        int rightTile = (this.x + this.width) / GamePanel.tileSize; // right edge of player
        int topTile = this.y / GamePanel.tileSize; // top edge of player
        int bottomTile = (this.y + this.height) / GamePanel.tileSize; // bottom edge of player

    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, this.x, this.y, width, height, null);
        }
    }
}
