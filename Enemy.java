
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Enemy {

    static BufferedImage defaultImage = loadDefaultImage();
    BufferedImage image;
    int row;
    int col;
    Rectangle rect;
    int id;
    boolean isTemp;

    public Enemy(int col, int row, int width, int height, boolean isTemp, int id, BufferedImage image) {
        this.row = row;
        this.col = col;
        this.id = id;
        this.isTemp = isTemp;
        this.rect = new Rectangle(col * GameConstants.TILE_SIZE, row * GameConstants.TILE_SIZE, width, height);
        this.image = image;
    }

    public Enemy(int col, int row, int width, int height, boolean isTemp, int id) {
        this(col, row, width, height, isTemp, id, defaultImage);
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, rect.x - GamePanel.cameraX, rect.y, rect.width, rect.height,
                    null);
        }
    }

    private static BufferedImage loadDefaultImage() {
        try {
            return ImageIO.read(new File("photos/platformerBackground.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        String answer = "";
        answer += this.col + ",";
        answer += this.row;
        return answer;
    }
}
