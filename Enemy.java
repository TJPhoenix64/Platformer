
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Enemy {
    BufferedImage image;
    int row;
    int col;
    int width;
    int height;

    public Enemy(int row, int col, int width, int height) {
        this.row = row;
        this.col = col;
        this.width = width;
        this.height = height;
        try {
            image = ImageIO.read(new File("photos/redImage.jpg"));

        } catch (IOException e) {
        }
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, this.col * GamePanel.TILE_SIZE, this.row * GamePanel.TILE_SIZE, this.width, this.height,
                    null);
        }
    }
}
