
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Spike extends Thing {

    private Polygon spike;
    private String defaultImagePath = "photos/dirt.png";
    int rotation;

    /**
     * goes counter clockwise starting from bottom left corner
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param imagePath
     */
    public Spike(int x1, int y1, int x2, int y2, int x3, int y3, String imagePath) {
        // row, col
        super(x1 / GamePanel.TILE_SIZE, y3 / GamePanel.TILE_SIZE);
        int[] xArr = new int[3];
        xArr[0] = x1;
        xArr[1] = x2;
        xArr[2] = x3;

        int[] yArr = new int[3];
        yArr[0] = y1;
        yArr[1] = y2;
        yArr[2] = y3;

        this.rotation = 0;

        this.spike = new Polygon(xArr, yArr, 3);
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * simplest constructor, assumes it is 1 tile wide and tall, uses default
     * image path, also asumes is upright
     *
     * @param col
     * @param row
     */
    public Spike(int col, int row) {
        super(col, row, false);
        this.rotation = 0;

        int[] xArr = new int[3];
        xArr[0] = col * GamePanel.TILE_SIZE;
        xArr[1] = (col + 1) * GamePanel.TILE_SIZE;
        xArr[2] = (xArr[0] + xArr[1]) / 2;

        int[] yArr = new int[3];
        yArr[0] = (row + 1) * GamePanel.TILE_SIZE;
        yArr[1] = (row + 1) * GamePanel.TILE_SIZE;
        yArr[2] = row * GamePanel.TILE_SIZE;

        this.spike = new Polygon(xArr, yArr, 3);

        try {
            image = ImageIO.read(new File(defaultImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * location of top right corner
     *
     * @param col
     * @param row
     * @param numTileWidth how many tiles it has in length and width
     * @param imagePath
     */
    public Spike(int col, int row, int rotation) {
        super(col, row, false);
        this.rotation = rotation;
        int[] xArr = new int[3];
        xArr[0] = col * GamePanel.TILE_SIZE;
        xArr[1] = (col + 1) * GamePanel.TILE_SIZE;
        xArr[2] = (xArr[0] + xArr[1]) / 2;

        int[] yArr = new int[3];
        yArr[0] = (row + 1) * GamePanel.TILE_SIZE;
        yArr[1] = (row + 1) * GamePanel.TILE_SIZE;
        yArr[2] = row * GamePanel.TILE_SIZE;

        this.spike = new Polygon(xArr, yArr, 3);
        if (rotation != 0) {

            double angleRadians = Math.toRadians(rotation);
            Rectangle bounds = this.spike.getBounds();
            double centerX = bounds.getCenterX();
            double centerY = bounds.getCenterY();

            AffineTransform transform = new AffineTransform();
            transform.rotate(angleRadians, centerX, centerY);

            Shape rotated = transform.createTransformedShape(this.spike);
            this.spike = toPolygon(rotated);
        }
        try {
            image = ImageIO.read(new File(defaultImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * position, rotation, and if its temporary
     *
     * @param col
     * @param row
     * @param rotation
     * @param isTemp
     */
    public Spike(int col, int row, int rotation, boolean isTemp) {
        super(col, row, isTemp);
        this.rotation = rotation;
        int[] xArr = new int[3];
        xArr[0] = col * GamePanel.TILE_SIZE;
        xArr[1] = (col + 1) * GamePanel.TILE_SIZE;
        xArr[2] = (xArr[0] + xArr[1]) / 2;

        int[] yArr = new int[3];
        yArr[0] = (row + 1) * GamePanel.TILE_SIZE;
        yArr[1] = (row + 1) * GamePanel.TILE_SIZE;
        yArr[2] = row * GamePanel.TILE_SIZE;

        this.spike = new Polygon(xArr, yArr, 3);
        if (rotation != 0) {

            double angleRadians = Math.toRadians(rotation);
            Rectangle bounds = this.spike.getBounds();
            double centerX = bounds.getCenterX();
            double centerY = bounds.getCenterY();

            AffineTransform transform = new AffineTransform();
            transform.rotate(angleRadians, centerX, centerY);

            Shape rotated = transform.createTransformedShape(this.spike);
            this.spike = toPolygon(rotated);
        }
        try {
            image = ImageIO.read(new File(defaultImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * location of top right corner
     *
     * @param col
     * @param row
     * @param numTileWidth how many tiles it has in length and width
     * @param imagePath
     */
    public Spike(int col, int row, int numTileWidth, String imagePath, int rotation) {
        super(col, row, false);
        this.rotation = rotation;
        int[] xArr = new int[3];
        xArr[0] = col * GamePanel.TILE_SIZE;
        xArr[1] = (col + numTileWidth) * GamePanel.TILE_SIZE;
        xArr[2] = (xArr[0] + xArr[1]) / 2;

        int[] yArr = new int[3];
        yArr[0] = (row + numTileWidth) * GamePanel.TILE_SIZE;
        yArr[1] = (row + numTileWidth) * GamePanel.TILE_SIZE;
        yArr[2] = row * GamePanel.TILE_SIZE;

        this.spike = new Polygon(xArr, yArr, 3);
        if (rotation != 0) {

            double angleRadians = Math.toRadians(rotation);
            Rectangle bounds = this.spike.getBounds();
            double centerX = bounds.getCenterX();
            double centerY = bounds.getCenterY();

            AffineTransform transform = new AffineTransform();
            transform.rotate(angleRadians, centerX, centerY);

            Shape rotated = transform.createTransformedShape(this.spike);
            this.spike = toPolygon(rotated);
        }
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2d) {
        Shape oldClip = g2d.getClip();
        Composite oldComposite = g2d.getComposite();
        g2d.setClip(spike);
        if (isTemp) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, GamePanel.tempTransparency));
        }
        Rectangle bounds = spike.getBounds();
        g2d.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null);
        g2d.setClip(oldClip);
        g2d.setComposite(oldComposite);
    }

    public static Polygon toPolygon(Shape shape) {
        Polygon polygon = new Polygon();

        PathIterator it = shape.getPathIterator(null);
        double[] coords = new double[6];

        while (!it.isDone()) {
            int type = it.currentSegment(coords);
            if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
                polygon.addPoint((int) coords[0], (int) coords[1]);
            }
            it.next();
        }

        return polygon;
    }

    @Override
    public String toString() {
        return col
                + "," + row
                + "," + rotation;
    }

}
