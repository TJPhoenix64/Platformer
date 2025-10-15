import java.awt.*;

class ImageRect {
    Rectangle bounds;
    Image image;

    public ImageRect(Image image, int x, int y, int w, int h) {
        this.image = image;
        this.bounds = new Rectangle(x, y, w, h);
    }

    public void draw(Graphics g, Component c) {
        g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, c);
    }

    public boolean contains(int mouseX, int mouseY) {
        return bounds.contains(mouseX, mouseY);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}