import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Checkpoint extends Thing {

    public Checkpoint(int col, int row) {
        super(col, row, false);
        x = col * GameConstants.TILE_SIZE;
        y = row * GameConstants.TILE_SIZE;
        try {
            image = ImageIO.read(new File("photos/flag.png"));
        } catch (IOException e) {
        }

    }

    public Checkpoint(int col, int row, boolean isTemp) {
        super(col, row, isTemp);
        x = col * GameConstants.TILE_SIZE;
        y = row * GameConstants.TILE_SIZE;
        try {
            image = ImageIO.read(new File("photos/flag.png"));
        } catch (IOException e) {
        }

    }
}
