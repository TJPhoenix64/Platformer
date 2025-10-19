
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Spike {
    private Polygon spike;
    private Image image;

    public Spike(int x1, int y1, int x2, int y2, int x3, int y3, String imagePath) {
        int[] xArr = new int[3];
        xArr[0] = x1;
        xArr[1] = x2;
        xArr[2] = x3;

        int[] yArr = new int[3];
        yArr[0] = y1;
        yArr[1] = y2;
        yArr[2] = y3;

        this.spike = new Polygon(xArr, yArr, 3);
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d) {
        Shape oldClip = g2d.getClip();
        g2d.setClip(spike);
        Rectangle bounds = spike.getBounds();
        g2d.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
        g2d.setClip(oldClip);
    }
}
