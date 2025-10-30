import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Tile extends Thing {

    public Tile(int col, int row) {
        super(col, row);
        this.x = col * GamePanel.TILE_SIZE;
        this.y = row * GamePanel.TILE_SIZE;
        try {
            image = ImageIO.read(new File("photos/dirt.png"));
        } catch (IOException e) {
        }
    }

    public Tile(int col, int row, boolean isTemp) {
        super(col, row, isTemp);
        this.x = col * GamePanel.TILE_SIZE;
        this.y = row * GamePanel.TILE_SIZE;
        try {
            image = ImageIO.read(new File("photos/dirt.png"));
        } catch (IOException e) {
        }
    }
}
