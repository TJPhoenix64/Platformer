
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
    long startJumpTime = 0L;
    long endJumpPressedTime = 0L;
    long endAirTime = 0L;

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
    long endLeftTime = 0L;
    long endRightTime = 0L;
    int initialVeloX = 10;
    int lastCheckpointX;
    int lastCheckpointY;
    int lastCheckpointLevel;
    boolean passedCheckpointSinceButtonPress = false;
    double xVeloAtJump = 0;
    double currentXVelo = 0;
    double currentYVelo = 0;

    int groundFriction = 30;

    int defaultForwardX = 100;
    int defaultBackwardX = 500;
    int defaultY = 300;

    int numCoins = 15;

    ArrayList<Tile> nearbyTiles = new ArrayList<>();
    ArrayList<Spike> nearbySpikes = new ArrayList<>();
    ArrayList<Coin> nearbyCoins = new ArrayList<>();
    Rectangle tileBounds = new Rectangle(0, 0, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);

    public static Rectangle playerRect = null;

    public Player() {
        width = GamePanel.TILE_SIZE;
        height = GamePanel.TILE_SIZE;
        x = 100;
        y = 100;
        this.lastCheckpointX = this.x;
        this.lastCheckpointY = this.y;
        this.lastCheckpointLevel = GamePanel.currentLevelNum;
        playerRect = new Rectangle(x, y, width, height);

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

    public void checkCheckpoints(long currentTime) {
        int col = x / GamePanel.TILE_SIZE;
        int row = y / GamePanel.TILE_SIZE;

        if (!GamePanel.currentLevel.contains(col, row)) {
            return;
        }

        Thing thing = GamePanel.currentLevel.get(col, row);
        if (thing instanceof Checkpoint checkpoint) {
            long diffMs = getDiffMillis(GamePanel.lastCheckpointTime, currentTime);

            if (diffMs > 200) {
                if (!(checkpoint.x == GamePanel.tyler.lastCheckpointX && checkpoint.y == GamePanel.tyler.lastCheckpointY && GamePanel.currentLevelNum == GamePanel.tyler.lastCheckpointLevel)) {
                    GamePanel.passCheckpoint(checkpoint);
                    GamePanel.lastCheckpointTime = currentTime;
                }

            }

        }
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

    public void moveLeftReleased(long timeReleased) {
        this.endLeftTime = timeReleased;
        this.moveLeftPressed = false;
        this.moveLeftReleased = true;
    }

    public void moveRightReleased(long timeReleased) {
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

    public void jumpReleased(long time) {
        endJumpPressedTime = time;
        jumpReleased = true;
    }

    public double getInitialVeloY() {
        double num = 10;
        double diffMilis;
        if (jumpReleased) {
            diffMilis = getDiffMillis(startJumpTime, endAirTime);
        } else {
            long currentTime = System.nanoTime();
            diffMilis = getDiffMillis(startJumpTime, currentTime);
        }
        num += diffMilis / 10.0;
        if (num > 30) {
            num = 30;
        }

        return -num;
    }

    public static int getDiffMillis(long startNs, long endNs) {
        return (int) Math.max(0, (endNs - startNs) / 1_000_000);
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

        if (x > GamePanel.PANEL_WIDTH - width || x < 0) {

            if (x > GamePanel.PANEL_WIDTH - width) {
                GamePanel.advanceLevel();
                int startX;
                int startY;
                if (GamePanel.currentLevel.getStartTile() != null) {
                    startX = GamePanel.currentLevel.getStartTile().x;
                    startY = GamePanel.currentLevel.getStartTile().y;
                } else {
                    startY = defaultY;
                    startX = defaultForwardX;
                }
                System.out.println("advance startX: " + startX + " startY: " + startY);
                teleport(startX, startY);
            }

            if (x < 0) {
                GamePanel.rewindLevel();
                int startX;
                int startY;
                if (GamePanel.currentLevel.getStartTile() != null) {
                    startX = GamePanel.currentLevel.getStartTile().x;
                    startY = GamePanel.currentLevel.getStartTile().y;
                } else {
                    startY = defaultY;
                    startX = defaultBackwardX;
                }
                System.out.println("rewind startX: " + startX + " startY: " + startY);
                teleport(startX, startY);
            }
        }

        if (deltaX == 0 && deltaY == 0) {
            setImage(redImage);
        } else {
            setImage(orangeImage);
        }

        changePosition(deltaX, deltaY);
    }

    public int handleXVelo(long currentTime) {
        // In-air X-velocity remains constant
        if (isJumping) {
            return (int) xVeloAtJump;
        }

        // Ground deceleration
        if (moveLeftReleased && !passedCheckpointSinceButtonPress) {
            if (endLeftTime != 0L) {
                if (endLeftTime > endAirTime) {
                    int timeSinceReleased = getDiffMillis(endLeftTime, currentTime);
                    int num = -(int) (initialVeloX - (timeSinceReleased / groundFriction));
                    if (num < 0) {
                        return num;
                    } else {
                        moveLeftReleased = false;
                        endLeftTime = 0L;
                    }
                } else {
                    int timeSinceReleased = getDiffMillis(endAirTime, currentTime);
                    int num = (int) ((timeSinceReleased / groundFriction) + xVeloAtJump);
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
                if (endRightTime > endAirTime) {
                    int timeSinceReleased = getDiffMillis(endRightTime, currentTime);
                    int num = (int) (initialVeloX - (timeSinceReleased / groundFriction));
                    if (num > 0) {
                        return num;
                    } else {
                        moveRightReleased = false;
                        endRightTime = 0L;
                    }
                } else {
                    int timeSinceReleased = getDiffMillis(endAirTime, currentTime);
                    int num = (int) (xVeloAtJump - (timeSinceReleased / 30));
                    if (num > 0) {
                        return num;
                    } else {
                        moveRightReleased = false;
                        endAirTime = 0L;
                    }
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
                        nearbyTiles.add(GamePanel.currentLevel.getBlocks()[xPos][yPos]);
                    } else if (GamePanel.currentLevel.getSpikes()[xPos][yPos] != null) {
                        nearbySpikes.add(GamePanel.currentLevel.getSpikes()[xPos][yPos]);
                    } else if (GamePanel.currentLevel.getCoins()[xPos][yPos] != null) {
                        nearbyCoins.add(GamePanel.currentLevel.getCoins()[xPos][yPos]);
                    }
                }
            }
        }

        for (Spike spike : nearbySpikes) {
            tileBounds.x = spike.col * tileSize;
            tileBounds.y = spike.row * tileSize;

            if (playerRect.getBounds().intersects(tileBounds)) {
                GamePanel.playerHurt = true;
            }
        }

        for (Coin coin : nearbyCoins) {
            tileBounds.x = coin.col * tileSize;
            tileBounds.y = coin.row * tileSize;

            if (playerRect.getBounds().intersects(tileBounds)) {
                pickupCoin(coin);
            }
        }

        for (Tile tile : nearbyTiles) {
            tileBounds.x = tile.col * tileSize;
            tileBounds.y = tile.row * tileSize;

            if (dY > 0) {
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
        playerRect.x = this.x;
        playerRect.y = this.y;
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, this.x, this.y, width, height, null);
        }
    }
}
