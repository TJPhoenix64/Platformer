
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class Tile {
    int x;
    int y;
    int row;
    int col;
    BufferedImage image;
    boolean isTemp;

    public Tile(int row, int col, boolean isTemp) {
        this.row = row;
        this.col = col;
        this.x = col * GamePanel.tileSize;
        this.y = row * GamePanel.tileSize;
        this.isTemp = isTemp;
        try {
            if (isTemp) {
                image = ImageIO.read(new File("photos/grass.png"));
            } else {
                image = ImageIO.read(new File("photos/dirt.png"));
            }
        } catch (IOException e) {
        }
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, this.x, this.y, GamePanel.tileSize, GamePanel.tileSize, null);
        }
    }

    @Override
    public String toString() {
        String answer = "";
        answer += this.row + ", ";
        answer += this.col;
        return answer;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same object reference
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false; // Null or different class
        }
        Tile object = (Tile) obj;

        return (object.row == this.row && object.col == this.col && object.isTemp == this.isTemp);
    }

    @Override
    public int hashCode() {
        // It's crucial to override hashCode() whenever equals() is overridden
        return Objects.hash(row, col, isTemp);
    }
}
