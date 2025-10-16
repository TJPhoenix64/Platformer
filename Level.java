import java.awt.*;
import java.util.ArrayList;

public class Level {
    private ArrayList<Tile> blocks;

    public Level() {
        blocks = new ArrayList<>();
    }

    public void add(Tile block) {
        blocks.add(block);
    }

    public ArrayList<Tile> getBlocks() {
        return blocks;
    }

    // Additional logic for your level
    public void draw(Graphics g) {
        for (Tile b : blocks) {
            b.draw(g);
        }
    }
}
