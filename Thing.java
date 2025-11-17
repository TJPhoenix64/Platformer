import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Objects;

public abstract class Thing {
    int row;
    int col;
    int x;
    int y;
    boolean isTemp;
    Image image;

    public Thing(int col, int row, boolean isTemp) {
        this.row = row;
        this.col = col;
        this.isTemp = isTemp;
        x = col * GamePanel.TILE_SIZE;
        y = row * GamePanel.TILE_SIZE;
    }

    public Thing(int col, int row) {
        this.row = row;
        this.col = col;
        this.isTemp = false;
        x = col * GamePanel.TILE_SIZE;
        y = row * GamePanel.TILE_SIZE;
    }

    public void draw(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();
        if (isTemp) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, GamePanel.tempTransparency));
        }
        if (image != null) {
            g2d.drawImage(image, this.x, this.y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);

        }
        g2d.dispose();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Same object reference
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false; // Null or different class
        }
        Thing object = (Thing) obj;

        return (object.row == this.row && object.col == this.col && object.isTemp == this.isTemp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, isTemp);
    }

    @Override
    public String toString() {
        String answer = "";
        answer += this.col + ",";
        answer += this.row;
        return answer;
    }
}
