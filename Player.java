import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player extends Rectangle {
    boolean isJumping;
    boolean jumpReleased = true;
    Image image;
    Image redImage;
    Image orangeImage;
    Long startJumpTime = 0L;
    Long endJumpPressedTime = 0L;
    Long endAirTime = 0L;

    int totalAirTime;

    double mediumGravity = 0.035;
    double weakGravity = 0.02;
    double strongGravity = 0.05;
    double currentGravity;
    double initialVeloY = -15;
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
    double currentYVelo = 0;

    int numCoins = 15;

    ArrayList<Tile> nearbyTiles = new ArrayList<>();
    ArrayList<Spike> nearbySpikes = new ArrayList<>();
    ArrayList<Coin> nearbyCoins = new ArrayList<>();

    public Player() {
        width = GamePanel.TILE_SIZE;
        height = GamePanel.TILE_SIZE;
        x = 100;
        y = 500;
        this.lastCheckpointX = this.x;
        this.lastCheckpointY = this.y;

        try {
            orangeImage = ImageIO.read(new File("photos/orangeBackground.jpg"));
            redImage = ImageIO.read(new File("photos/redImage.jpg"));
            image = orangeImage;
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

    public void setImage(Image image) {
        this.image = image;
    }

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
    }

    public void moveRightReleased(Long timeReleased) {
        this.endRightTime = timeReleased;
        this.moveRightPressed = false;
        this.moveRightReleased = true;
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

    public void pickupCoin(Coin coin) {
        numCoins++;
        GamePanel.currentLevel.removeCoin(coin);
    }

    public void updatePosition() {
        int deltaX = 0;
        int deltaY = 0;
        checkCheckpoints();
        Long currentTime = System.nanoTime();

        // Vertical
        if (isJumping) {
            int timeSinceJumpStarted = getDiffMillis(startJumpTime, currentTime);
            deltaY = (int) (mediumGravity * timeSinceJumpStarted + initialVeloY);
        }

        // Horizontal
        if (moveLeftPressed) {
            deltaX = -initialVeloX;
        } else if (moveRightPressed) {
            deltaX = initialVeloX;
        } else if (moveLeftReleased || moveRightReleased) {
            deltaX = handleXVelo(currentTime);
        }

        if (x > GamePanel.PANEL_WIDTH - width) {
            GamePanel.advanceLevel();
            teleport(100, 500);
        }

        if (x < 0) {
            GamePanel.rewindLevel();
            teleport(900, 500);
        }

        if (deltaX == 0 && deltaY == 0) {
            setImage(redImage);
        } else {
            setImage(orangeImage);
        }

        changePosition(deltaX, deltaY);
    }

    public int handleXVelo(Long currentTime) {
        // In-air X-velocity remains constant
        if (isJumping)
            return (int) xVeloAtJump;

        // Ground deceleration
        if (moveLeftReleased && !passedCheckpointSinceButtonPress) {
            if (endLeftTime != 0L) {
                if (endLeftTime > endAirTime) {
                    int timeSinceReleased = getDiffMillis(endLeftTime, currentTime);
                    int num = -(int) (initialVeloX - (timeSinceReleased / 70));
                    if (num < 0) {
                        return num;
                    } else {
                        moveLeftReleased = false;
                        endLeftTime = 0L;
                    }
                } else {
                    int timeSinceReleased = getDiffMillis(endAirTime, currentTime);
                    int num = (int) ((timeSinceReleased / 70) + xVeloAtJump);
                    if (num < 0) {
                        return num;
                    } else {
                        moveLeftReleased = false;
                        endAirTime = 0L;
                    }
                }
            }
        }

        if (moveRightReleased && !passedCheckpointSinceButtonPress) {
            if (endRightTime != 0L) {
                int timeSinceReleased = getDiffMillis(endRightTime, currentTime);
                if (timeSinceReleased < 500) {
                    return (initialVeloX - (timeSinceReleased / 50));
                } else {
                    moveRightReleased = false;
                    endRightTime = 0L;
                }
            }
        }

        return 0;
    }

    public void changePosition(int dX, int dY) {
        int tileSize = GamePanel.TILE_SIZE;
        int leftTile = this.x / tileSize;
        int rightTile = (this.x + this.width) / tileSize;
        int topTile = this.y / tileSize;
        int bottomTile = (this.y + this.height) / tileSize;

        nearbyTiles.clear();
        nearbySpikes.clear();
        nearbyCoins.clear();
        for (int yPos = topTile - 1; yPos <= bottomTile + 1; yPos++) {
            for (int xPos = leftTile - 1; xPos <= rightTile + 1; xPos++) {
                if (GamePanel.isSolidTile(xPos, yPos)) {
                    if (GamePanel.currentLevel.getBlocks()[xPos][yPos] != null) {
                        nearbyTiles.add(new Tile(xPos, yPos, false));
                    } else if (GamePanel.currentLevel.getSpikes()[xPos][yPos] != null) {
                        nearbySpikes.add(new Spike(xPos, yPos));
                    } else if (GamePanel.currentLevel.getCoins().contains(new Coin(xPos, yPos))) {
                        System.out.println("numCoins: " + GamePanel.currentLevel.getCoins().size());
                        nearbyCoins.add(new Coin(xPos, yPos));
                    }
                }
            }
        }

        for (Spike spike : nearbySpikes) {
            Rectangle tileBounds = new Rectangle(spike.col * tileSize, spike.row * tileSize, tileSize, tileSize);
            if (getPlayerRect().getBounds().intersects(tileBounds)) {
                GamePanel.playerHurt = true;
            }
        }

        for (Coin coin : nearbyCoins) {
            Rectangle tileBounds = new Rectangle(coin.col * tileSize, coin.row * tileSize, tileSize, tileSize);
            if (getPlayerRect().getBounds().intersects(tileBounds)) {
                pickupCoin(coin);
            }
        }

        for (Tile tile : nearbyTiles) {
            Rectangle tileBounds = new Rectangle(tile.col * tileSize, tile.row * tileSize, tileSize, tileSize);

            if (dY > 0) {
                Rectangle playerRect = getPlayerRect();
                // Predict future horizontal position
                Rectangle futureRect = new Rectangle(playerRect);
                futureRect.y += dY;
                if (futureRect.intersects(tileBounds)) {
                    int maxDown = tileBounds.y - playerRect.height;

                    // Compute how far we *can* move without intersecting
                    dY = maxDown - playerRect.y;

                    isJumping = false;
                    endAirTime = System.nanoTime();
                    totalAirTime = getDiffMillis(startJumpTime, endAirTime);

                }

                // --- NEW: sync deceleration timers so landing resumes smoothly ---
                if (moveLeftReleased && endLeftTime < endAirTime) {
                    endLeftTime = endAirTime; // start ground decel from landing moment
                }
                if (moveRightReleased && endRightTime < endAirTime) {
                    endRightTime = endAirTime;
                }

            }

            if (dY < 0) {
                Rectangle playerRect = getPlayerRect();
                // Predict future horizontal position
                Rectangle futureRect = new Rectangle(playerRect);
                futureRect.y += dY;
                if (futureRect.intersects(tileBounds)) {
                    int maxUp = tileBounds.y + tileBounds.height;

                    // Compute how far we *can* move without intersecting
                    dY = maxUp - playerRect.y;
                }
            }

            // Only check if moving right
            if (dX > 0) {
                Rectangle playerRect = getPlayerRect();

                // Predict future horizontal position
                Rectangle futureRect = new Rectangle(playerRect);
                futureRect.x += dX;
                if (futureRect.intersects(tileBounds)) {

                    int maxRight = tileBounds.x - playerRect.width;

                    dX = maxRight - playerRect.x;

                    // Ensure dX doesn't overshoot (e.g., if already inside tile)
                    if (dX < 0) {
                        dX = 0;
                    }
                }
            }

            if (dX < 0) {
                Rectangle playerRect = getPlayerRect();

                // Predict future horizontal position
                Rectangle futureRect = new Rectangle(playerRect);
                futureRect.x += dX;
                if (futureRect.intersects(tileBounds)) {

                    int maxLeft = tileBounds.x + tileBounds.width;

                    dX = maxLeft - playerRect.x;

                    // Ensure dX doesn't overshoot (e.g., if already inside tile)
                    if (dX > 0) {
                        dX = 0;
                    }
                }
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
