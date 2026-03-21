
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Coin extends Thing {

    public Coin(int col, int row, boolean isTemp) {
        super(col, row, isTemp);
        try {
            image = ImageIO.read(new File(GameConstants.Images.COIN));
        } catch (IOException e) {
        }
    }

    public Coin(int col, int row) {
        this(col, row, false);
    }

}
