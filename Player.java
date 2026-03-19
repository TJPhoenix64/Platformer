
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player extends Rectangle {

    boolean isJumping;
    boolean jumpReleased = true;
    Image image;

    Image playerImageRight;
    Image playerImageLeft;
    Image redImage;
    Image orangeImage;
    Image swordImageLeft;
    Image swordImageRight;
    Image swordImage;
    long startJumpTime = 0L;
    long endJumpPressedTime = 0L;
    long endAirTime = 0L;

    int totalAirTime;

    double mediumGravity = 0.4;
    double initialVeloY = -12;
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

    int displayX;

    int numCoins = 15;

    ArrayList<Tile> nearbyTiles = new ArrayList<>();
    ArrayList<Spike> nearbySpikes = new ArrayList<>();
    ArrayList<Coin> nearbyCoins = new ArrayList<>();
    ArrayList<Enemy> nearbyEnemies = new ArrayList<>();
    Rectangle tileBounds = new Rectangle(0, 0, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);

    boolean isAttacking = false;
    long attackStartTime = 0l;
    long expectedAttackEndTime = 0l;

    boolean facingRight = true;

    long startHeadHitTime = 0l;

    public static Rectangle playerRect = null;

    public Player() {
        width = GameConstants.TILE_SIZE;
        height = GameConstants.TILE_SIZE * 2;
        x = 100;
        displayX = x;
        y = 100;
        this.lastCheckpointX = this.x;
        this.lastCheckpointY = this.y;
        this.lastCheckpointLevel = GamePanel.currentLevelNum;
        playerRect = new Rectangle(x, y, width, height);

        try {
            orangeImage = ImageIO.read(new File("photos/orangeBackground.jpg"));
            redImage = ImageIO.read(new File("photos/redImage.jpg"));
            swordImageRight = ImageIO.read(new File("photos/pixelSwordRight.png"));
            swordImageLeft = ImageIO.read(new File("photos/pixelSwordLeft.png"));
            playerImageRight = ImageIO.read(new File("photos/playerImageRight.png"));
            playerImageLeft = ImageIO.read(new File("photos/playerImageLeft.png"));

            image = playerImageRight;
            swordImage = swordImageRight;

        } catch (IOException e) {
        }
    }

    public void updateCheckpointPos(int x, int y) {
        this.lastCheckpointX = x;
        this.lastCheckpointY = y;
    }

    public void checkCheckpoints(long currentTime) {
        int col = x / GameConstants.TILE_SIZE;
        int row = y / GameConstants.TILE_SIZE;
        int row2 = (y / GameConstants.TILE_SIZE) + 1;

        Thing thing;
        if (GamePanel.currentLevel.contains(col, row)) {
            thing = GamePanel.currentLevel.getThing(col, row);
        } else if (GamePanel.currentLevel.contains(col, row2)) {
            thing = GamePanel.currentLevel.getThing(col, row2);
        } else {
            return;
        }

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

    public void jump(long startTime) {
        if (!isJumping) {
            isJumping = true;
            currentYVelo = initialVeloY; // negative
            startJumpTime = startTime;

        }
    }

    public void jumpReleased(long time) {
        endJumpPressedTime = time;
        jumpReleased = true;
    }

    /**
     * attempt at making variable jump heights, not being used right now
     *
     * @return
     */
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

    public void attack() {
        if (!isAttacking) {
            isAttacking = true;
            attackStartTime = System.currentTimeMillis();
            expectedAttackEndTime = attackStartTime + 500;
        }
    }

    public void updateAttack() {
        if (System.currentTimeMillis() > expectedAttackEndTime) {
            isAttacking = false;
        }
    }

    public void handleOffScreenMovement() {
        if (x > GameConstants.PANEL_WIDTH - width || x < 0) {

            if (x > GameConstants.PANEL_WIDTH - width) {
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
                teleport(startX, startY);
            }
        }
    }

    public void updatePosition() {
        double deltaX = 0;
        double deltaY;
        long currentTime = System.nanoTime();

        checkCheckpoints(currentTime);

        // Vertical
        currentYVelo += mediumGravity;
        deltaY = currentYVelo;

        // Horizontal
        if (moveLeftPressed) {
            deltaX = -initialVeloX;
        } else if (moveRightPressed) {
            deltaX = initialVeloX;
        } else if (moveLeftReleased || moveRightReleased) {
            deltaX = handleXVelo(currentTime);
        }

        handleOffScreenMovement();
        if (deltaX > 1) {
            facingRight = true;
        } else if (deltaX < -1) {
            facingRight = false;
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

    /**
     * clears the nearby arrays of tiles, spikes, coins, and enemies
     */
    public void clearNearbyStuff() {
        nearbyTiles.clear();
        nearbySpikes.clear();
        nearbyCoins.clear();
        nearbyEnemies.clear();
    }

    /**
     * checks the location and adds the thing there to its nearby array
     *
     * @param xPos column that is checked
     * @param yPos row that is checked
     */
    public void addNearbyThing(int xPos, int yPos) {
        if (GamePanel.isSolidTile(xPos, yPos)) {
            if (GamePanel.currentLevel.getBlocks()[xPos][yPos] != null) {
                nearbyTiles.add(GamePanel.currentLevel.getBlocks()[xPos][yPos]);
            } else if (GamePanel.currentLevel.getSpikes()[xPos][yPos] != null) {
                nearbySpikes.add(GamePanel.currentLevel.getSpikes()[xPos][yPos]);
            } else if (GamePanel.currentLevel.getCoins()[xPos][yPos] != null) {
                nearbyCoins.add(GamePanel.currentLevel.getCoins()[xPos][yPos]);
            } else if (GamePanel.currentLevel.getEnemies()[xPos][yPos] != null) {
                nearbyEnemies.add(GamePanel.currentLevel.getEnemies()[xPos][yPos]);
            }
        }
    }

    public void changePosition(double dX, double dY) {
        int tileSize = GameConstants.TILE_SIZE;
        int leftTile = this.x / tileSize;
        int rightTile = (this.x + this.width) / tileSize;
        int topTile = this.y / tileSize;
        int bottomTile = (this.y + this.height) / tileSize;

        clearNearbyStuff();

        for (int yPos = topTile - 1; yPos <= bottomTile + 1; yPos++) {
            for (int xPos = leftTile - 1; xPos <= rightTile + 1; xPos++) {
                addNearbyThing(xPos, yPos);
            }
        }

        // should change this in the future to just do it for basic enemies(only contact damage) 
        // and have something sepsarate fro ones with projectiles or bosses
        for (Enemy enemy : nearbyEnemies) {
            tileBounds.x = enemy.col * tileSize - GamePanel.cameraX;
            tileBounds.y = enemy.row * tileSize;
            if (playerRect.getBounds().intersects(tileBounds)) {
                GamePanel.playerHurt = true;
            }
        }

        for (Spike spike : nearbySpikes) {
            tileBounds.x = spike.col * tileSize - GamePanel.cameraX;
            tileBounds.y = spike.row * tileSize;

            if (playerRect.getBounds().intersects(tileBounds)) {
                GamePanel.playerHurt = true;
            }
        }

        for (Coin coin : nearbyCoins) {
            tileBounds.x = coin.col * tileSize - GamePanel.cameraX;
            tileBounds.y = coin.row * tileSize;

            if (playerRect.getBounds().intersects(tileBounds)) {
                pickupCoin(coin);
            }
        }

        for (Tile tile : nearbyTiles) {
            tileBounds.x = tile.col * tileSize - GamePanel.cameraX;
            tileBounds.y = tile.row * tileSize;

            //going down
            if (dY > 0) {
                // Predict future vertical position
                Rectangle futureRect = new Rectangle(playerRect);
                futureRect.y += dY;
                //TODO: fix the code in this if statement
                if (futureRect.intersects(tileBounds)) {
                    int maxDown = tileBounds.y - playerRect.height;

                    // Compute how far we *can* move without intersecting
                    dY = maxDown - playerRect.y;

                    isJumping = false;
                    currentYVelo = 0;

                    endAirTime = System.nanoTime();
                    startHeadHitTime = endAirTime;
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

            // going up
            if (dY < 0) {
                // Predict future horizontal position
                Rectangle futureRect = new Rectangle(playerRect);
                futureRect.y += dY;
                if (futureRect.intersects(tileBounds)) {
                    int maxUp = tileBounds.y + tileBounds.height;

                    // Compute how far we *can* move without intersecting
                    dY = maxUp - playerRect.y;
                    if (Math.abs(dY) < 0.01) {
                        isJumping = false;
                    }
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
        playerRect.x = (int) x;
        playerRect.y = (int) y;
    }

    public void draw(Graphics g) {
        int xPos;
        if (facingRight) {
            image = playerImageRight;
            xPos = x + width;
            swordImage = swordImageRight;
        } else {
            image = playerImageLeft;
            swordImage = swordImageLeft;
            xPos = x - swordImage.getWidth(null);
        }
        if (image != null) {
            g.drawImage(image, this.x, this.y, width, height, null);
        }
        if (isAttacking) {
            g.drawImage(swordImage, xPos, y + height / 2 - swordImage.getHeight(null) / 2, null);
        }
    }
}
