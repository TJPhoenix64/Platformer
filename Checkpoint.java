import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class Checkpoint {
    int x;
    int y;
    Image image;

    public Checkpoint(int row, int col) {
        x = col * GamePanel.TILE_SIZE;
        y = row * GamePanel.TILE_SIZE;
        try {
            image = ImageIO.read(new File("photos/flag.png"));
        } catch (IOException e) {
        }

    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, this.x, this.y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same object reference
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false; // Null or different class
        }
        Checkpoint object = (Checkpoint) obj;

        return (object.x == this.x && object.y == this.y);
    }

    @Override
    public int hashCode() {
        // It's crucial to override hashCode() whenever equals() is overridden
        return Objects.hash(x, y);
    }
}
