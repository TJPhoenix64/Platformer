import java.awt.*;
import java.util.ArrayList;

public class Level {
    private ArrayList<Tile> blocks;
    private ArrayList<Spike> spikes;
    private ArrayList<Checkpoint> checkpoints;

    public Level() {
        blocks = new ArrayList<>();
        spikes = new ArrayList<>();
        checkpoints = new ArrayList<>();
    }

    public void addTile(Tile tile) {
        blocks.add(tile);
    }

    public void addSpike(Spike spike) {
        spikes.add(spike);
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
    }

    public ArrayList<Tile> getBlocks() {
        return blocks;
    }

    public ArrayList<Spike> getSpikes() {
        return spikes;
    }

    public ArrayList<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    // Additional logic for your level
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (Tile b : blocks) {
            b.draw(g);
        }
        for (Spike s : spikes) {
            s.draw(g2d);
        }
        for (Checkpoint c : checkpoints) {
            c.draw(g);
        }
    }
}
