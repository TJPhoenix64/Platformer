
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public class FancyText {

    Rectangle bounds;
    double mid;
    double amplitude;
    double speed;
    Image image;
    boolean isTemp;

    public FancyText(int x, int y, int width, int height, double speed, double max, double min, Image image, boolean isTemp) {
        this.bounds = new Rectangle(x, y, width, height);
        this.speed = speed;
        this.mid = (max + min) / 2.0;
        this.amplitude = (max - min) / 2.0;
        this.image = image;
        this.isTemp = isTemp;
    }

    public double getDegree(double time) {
        return mid + amplitude * Math.sin(time * speed);
    }

    public void draw(Graphics2D g2d, double timeSeconds) {

        double radians = Math.toRadians(getDegree(timeSeconds));

        AffineTransform oldTransform = g2d.getTransform();
        Composite oldComposite = g2d.getComposite();

        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();

        g2d.rotate(radians, cx, cy);

        if (isTemp) {
            g2d.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            GamePanel.tempTransparency
                    )
            );
        }

        g2d.drawImage(
                image,
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height,
                null
        );

        g2d.setTransform(oldTransform);
        g2d.setComposite(oldComposite);
    }
}
