
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Coin extends Thing {

    public Coin(int col, int row) {
        super(col, row);
        try {
            image = ImageIO.read(new File("photos/coin.png"));
        } catch (IOException e) {
        }
    }

    public Coin(int col, int row, boolean isTemp) {
        super(col, row, isTemp);
        try {
            image = ImageIO.read(new File("photos/coin.png"));
        } catch (IOException e) {
        }
    }

}
