
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SmallEnemy extends Enemy {

    static BufferedImage smallImage = loadImage();

    public SmallEnemy(int col, int row, boolean isTemp, int id) {
        super(col, row, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE, isTemp, id, smallImage);
    }

    private static BufferedImage loadImage() {
        try {
            return ImageIO.read(new File("photos/PlayButton.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
