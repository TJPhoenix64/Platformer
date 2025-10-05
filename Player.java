
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class Player extends Rectangle {
    int width;
    int height;
    int x;
    int y;
    Color color = new Color(105, 68, 13);
    boolean isFalling;
    boolean isJumping;
    Image image;

    public Player() {
        this.width = 100;
        this.height = 100;
        this.x = 100;
        this.y = 100;
        try {
            image = ImageIO.read(new File("photos/redImage.jpg"));
        } catch (Exception e) {
        }
    }

    public void jump() {

    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, this.x, this.y, width, height, null);
        }
    }
}
