import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player extends Rectangle {
    boolean isJumping;
    boolean jumpReleased = true;
    Image image;
    Long startJumpTime = 0L;
    Long endJumpPressedTime = 0L;
    Long endAirTime = 0L;

    int totalAirTime;

    double gravity = 0.05;
    double initialVeloY;
    boolean moveLeftPressed;
    boolean moveRightPressed;
    boolean moveLeftReleased;
    boolean moveRightReleased;
    Long endLeftTime = 0L;
    Long endRightTime = 0L;
    int initialVeloX = 10;
    int lastCheckpointX;
    int lastCheckpointY;
    boolean passedCheckpointSinceButtonPress = false;
    double xVeloAtJump = 0;
    double currentXVelo = 0;

    ArrayList<Tile> nearbyTiles = new ArrayList<>();
    ArrayList<Spike> nearbySpikes = new ArrayList<>();

    public Player() {
        this.width = GamePanel.TILE_SIZE;
        this.height = GamePanel.TILE_SIZE;
        this.x = 100;
        this.y = 100;
        this.lastCheckpointX = this.x;
        this.lastCheckpointY = this.y;

        try {
            image = ImageIO.read(new File("photos/orangeBackground.jpg"));
        } catch (IOException e) {
        }
    }

    public void updateCheckpointPos(int x, int y) {
        this.lastCheckpointX = x;
        this.lastCheckpointY = y;
    }

    public void checkCheckpoints() {
        int col = x / GamePanel.TILE_SIZE;
        int row = y / GamePanel.TILE_SIZE;
        if (GamePanel.currentLevel.getCheckpoints().contains(new Checkpoint(row, col))) {
            GamePanel.passCheckpoint(new Checkpoint(row, col));
        }

    }

    public Rectangle getPlayerRect() {
        return new Rectangle(this.x, this.y, this.width, this.height);
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
        endJumpPressedTime = 0L;
        endAirTime = 0L;
        xVeloAtJump = currentXVelo;
        totalAirTime = 0;
    }

    public void jumpReleased(Long time) {
        endJumpPressedTime = time;
        jumpReleased = true;
    }

    public double getInitialVeloY() {
        double num = 10;
        double diffMilis;
        if (jumpReleased) {
            diffMilis = getDiffMillis(startJumpTime, endAirTime);
        } else {
            Long currentTime = System.nanoTime();
            diffMilis = getDiffMillis(startJumpTime, currentTime);
        }
        num += diffMilis / 10.0;
        if (num > 30) {
            num = 30;
        }

        return -num;
    }

    public int getDiffMillis(Long startTime, Long endTime) {
        if (startTime == null || endTime == null)
            return 0;
        if (startTime == 0L || endTime == 0L)
            return 0;
        long diff = endTime - startTime;
        if (diff <= 0)
            return 0;
        return (int) (diff / 1000000);
    }

    public void updatePosition() {
        int deltaX = 0;
        int deltaY = 0;
        checkCheckpoints();
        Long currentTime = System.nanoTime();
        if (isJumping) {
            int timeSinceJumpStarted = getDiffMillis(startJumpTime, currentTime);
            initialVeloY = getInitialVeloY();
            deltaY = (int) (gravity * timeSinceJumpStarted + initialVeloY);
            // System.out.println("dy: " + dy);
            // ensures the block does not go below 500
            System.out.println(this.y + deltaY);
            if (this.y + deltaY > 500) {
                deltaY = 500 - this.y;
                if (isJumping) {
                    isJumping = false;
                    endAirTime = System.nanoTime();
                    totalAirTime = getDiffMillis(startJumpTime, endAirTime);
                }
            }
        } else {
            if (startJumpTime != null) {
                if (endAirTime == 0L && startJumpTime != 0L) {
                    endAirTime = System.nanoTime();
                }
            }
        }

        if (moveLeftPressed) {
            deltaX = -initialVeloX;
        } else if (moveRightPressed) {
            deltaX = initialVeloX;
        } else if (moveLeftReleased || moveRightReleased) {
            deltaX = handleXVelo(currentTime);
        }

        changePosition(deltaX, deltaY);

    }

    public int handleXVelo(Long currentTime) {

        if (this.isJumping) {
            return (int) xVeloAtJump;
        }

        if (moveLeftReleased && !passedCheckpointSinceButtonPress) {
            int timeSinceReleased = getDiffMillis(endLeftTime, currentTime);

            if (timeSinceReleased < 500 + totalAirTime) {
                int effectiveTime = timeSinceReleased - totalAirTime;
                if (effectiveTime < 0) {
                    effectiveTime = 0;
                }
                return -(initialVeloX - (effectiveTime / 50));
            } else {
                moveRightReleased = false;
            }
        }

        if (moveRightReleased && !passedCheckpointSinceButtonPress) {
            int timeSinceReleased = getDiffMillis(endRightTime, currentTime);

            if (timeSinceReleased < 500 + totalAirTime) {
                int effectiveTime = timeSinceReleased - totalAirTime;
                if (effectiveTime < 0) {
                    effectiveTime = 0;
                }
                return (initialVeloX - (effectiveTime / 50));
            } else {
                moveRightReleased = false;
            }
        }
        return 0;
    }

    /**
     * this handles changing the position, it also handles collisions too
     * 
     * @param dX
     * @param dY
     */
    public void changePosition(int dX, int dY) {
        int tileSize = GamePanel.TILE_SIZE;
        int leftTile = this.x / tileSize; // left edge of player
        int rightTile = (this.x + this.width) / tileSize; // right edge of player
        int topTile = this.y / tileSize; // top edge of player
        int bottomTile = (this.y + this.height) / tileSize; // bottom edge of player

        for (int yPos = topTile - 1; yPos <= bottomTile + 1; yPos++) {
            for (int xPos = leftTile - 1; xPos <= rightTile + 1; xPos++) {
                if (GamePanel.isSolidTile(xPos, yPos)) {
                    nearbySpikes.clear();
                    nearbyTiles.clear();
                    if (GamePanel.currentLevel.getBlocks()[xPos][yPos] != null) {
                        nearbyTiles.add(new Tile(yPos, xPos, false));
                    }
                    if (GamePanel.currentLevel.getSpikes()[xPos][yPos] != null) {
                        nearbySpikes.add(new Spike(yPos, xPos));
                    }
                }
            }
        }

        for (Tile tile : nearbyTiles) {
            Rectangle tileBounds = new Rectangle(tile.col * tileSize,
                    tile.row * tileSize, tileSize, tileSize);
            if (getPlayerRect().getBounds().intersects(tileBounds)) {
                if (dX > 0) {

                }
            }
        }
        /*
         * if moving right and block is to the right
         * make sure that it does not move too far
         */
        for (Spike spike : nearbySpikes) {
            Rectangle tileBounds = new Rectangle(spike.col * tileSize,
                    spike.row * tileSize, tileSize, tileSize);
            if (getPlayerRect().getBounds().intersects(tileBounds)) {
                GamePanel.playerHurt = true;
            }
        }
        currentXVelo = dX;
        this.x += dX;
        this.y += dY;
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, this.x, this.y, width, height, null);
        }
    }
}
