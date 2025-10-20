import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Checkpoint {
    int x;
    int y;
    Image image;

    public Checkpoint(int row, int col) {
        x = col * GamePanel.tileSize;
        y = row * GamePanel.tileSize;
        try {
            image = ImageIO.read(new File("photos/flag.png"));
        } catch (IOException e) {
        }

    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, this.x, this.y, GamePanel.tileSize, GamePanel.tileSize, null);
        }
    }
}
