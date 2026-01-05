
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class VolumeSlider {

    private int volume;
    private int yCir;
    private int radius;
    private Color rectColor;
    private Color circleColor;
    private int widthOval;
    private int heightOval;
    private Color ovalColor;
    private int leftXCircle;
    private int upperYCircle;
    private int centerX;
    private int centerY;
    private int leftXOval;
    private int upperYOval;
    private int centerXCircle;
    private int centerYCircle;

    private Rectangle rect;

    private int lowerYOval;

    public VolumeSlider(int volume, Rectangle rect, Color rectColor, int radius, Color circleColor, int widthOval, int heightOval, Color ovalColor) {
        this.volume = volume;
        this.rect = rect;
        this.radius = radius;
        this.rectColor = rectColor;
        this.circleColor = circleColor;
        this.widthOval = widthOval;
        this.heightOval = heightOval;
        this.ovalColor = ovalColor;
        this.centerX = rect.x + (rect.width / 2);
        this.centerY = rect.y + (rect.height / 2);
        this.leftXCircle = centerX - (radius / 2);
        this.upperYCircle = centerY - (radius / 2);
        this.centerXCircle = centerX;
        this.yCir = centerY;
        this.leftXOval = centerX - (widthOval / 2);
        this.upperYOval = centerY - (heightOval / 2);
        this.lowerYOval = centerY + (heightOval / 2);
    }

    public VolumeSlider(int volume, int xRect, int yRect, int widthRect, int heightRect, Color rectColor, int radius, Color circleColor, int widthOval, int heightOval, Color ovalColor) {
        this.volume = volume;
        this.rect = new Rectangle(xRect, yRect, widthRect, heightRect);
        this.radius = radius;
        this.rectColor = rectColor;
        this.circleColor = circleColor;
        this.widthOval = widthOval;
        this.heightOval = heightOval;
        this.ovalColor = ovalColor;
        this.centerX = rect.x + (rect.width / 2);
        this.centerY = rect.y + (rect.height / 2);
        this.leftXCircle = centerX - (radius / 2);
        this.upperYCircle = centerY - (radius / 2);
        this.leftXOval = centerX - (widthOval / 2);
        this.upperYOval = centerY - (heightOval / 2);
        this.lowerYOval = centerY + (heightOval / 2);

    }

    public float getVolume() {
        return (float) (volume / 100.0);
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void draw(Graphics g) {
        g.setColor(this.rectColor);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(this.ovalColor);
        drawOval(g);
        g.setColor(this.circleColor);
        drawCircle(g);
    }

    public void moveCircle(int y) {
        double heightDouble = heightOval * 1.0;
        int percentage = (int) (((lowerYOval - y) / heightDouble) * 100);

        if (percentage > 100) {
            percentage = 100;
            this.yCir = upperYOval;
        } else if (percentage < 0) {
            percentage = 0;
            this.yCir = lowerYOval;
        } else {
            this.yCir = y;
        }
        this.volume = percentage;
        this.upperYCircle = yCir - (radius / 2);
    }

    public boolean circleClicked(int x, int y) {
        double dx = x - centerXCircle;
        double dy = y - yCir;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= radius;
    }

    private void drawOval(Graphics g) {
        g.fillRoundRect(leftXOval, upperYOval, widthOval, heightOval, 10, 10);
    }

    private void drawCircle(Graphics g) {
        g.fillOval(leftXCircle, upperYCircle, this.radius, this.radius);
    }

}
