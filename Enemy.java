
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Enemy {

    BufferedImage image;
    int row;
    int col;
    Rectangle rect;

    public Enemy(int row, int col, int width, int height) {
        this.row = row;
        this.col = col;
        this.rect = new Rectangle(col * GameConstants.TILE_SIZE, row * GameConstants.TILE_SIZE, width, height);
        try {
            image = ImageIO.read(new File("photos/enemyImage.png"));
        } catch (IOException e) {
        }
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, rect.x, rect.y, rect.width, rect.height,
                    null);
        }
    }
}
