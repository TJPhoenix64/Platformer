import java.awt.*;
import java.util.ArrayList;

public class Level {
    private Tile[][] blocks;
    private Spike[][] spikes;
    private ArrayList<Checkpoint> checkpoints;

    private int numTiles = 0;
    private int numCheckpoints = 0;
    private int numSpikes = 0;
    private int numObjects = 0;

    private final int cols = GamePanel.PANEL_WIDTH / GamePanel.TILE_SIZE;
    private final int rows = GamePanel.PANEL_HEIGHT / GamePanel.TILE_SIZE;

    public Level() {
        clear();
    }

    public void addObject(Object obj) {
        numObjects++;
        if (obj instanceof Tile tile) {
            blocks[tile.col][tile.row] = tile;
            numTiles++;
        } else if (obj instanceof Spike spike) {
            spikes[spike.col][spike.row] = spike;
            numSpikes++;
        } else if (obj instanceof Checkpoint checkpoint) {
            checkpoints.add(checkpoint);
            numCheckpoints++;
        } else {
            numObjects--;
        }
    }

    public Tile[][] getBlocks() {
        return blocks;
    }

    public Spike[][] getSpikes() {
        return spikes;
    }

    public ArrayList<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public int getNumObjects() {
        return numObjects;
    }

    public int getNumTiles() {
        return numTiles;
    }

    public int getNumCheckpoints() {
        return numCheckpoints;
    }

    public int getNumSpikes() {
        return numSpikes;
    }

    public final void clear() {

        blocks = new Tile[cols][rows];
        spikes = new Spike[cols][rows];
        checkpoints = new ArrayList<>();

    }

    public Thing get(int col, int row) {
        if (blocks[col][row] != null) {
            return blocks[col][row];
        }
        if (spikes[col][row] != null) {
            return spikes[col][row];
        }
        if (checkpoints.contains(new Checkpoint(col, row, false))) {
            return new Checkpoint(col, row, false);
        }
        if (checkpoints.contains(new Checkpoint(col, row, true))) {
            return new Checkpoint(col, row, true);
        }
        return null;
    }

    public boolean containsTemp(int col, int row) {
        Thing thing = get(col, row);
        // System.out.println("Thing: " + thing);
        return (thing != null && thing.isTemp);
    }

    public boolean contains(Object obj) {
        if (obj instanceof Tile tile) {
            for (Tile[] tiles : blocks) {
                for (Tile t : tiles) {
                    if (t != null) {
                        if (t.equals(tile)) {
                            return true;
                        }
                    }
                }
            }
        } else if (obj instanceof Spike spike) {
            for (Spike[] points : spikes) {
                for (Spike s : points) {
                    if (s != null) {
                        if (s.equals(spike)) {
                            return true;
                        }
                    }
                }
            }
        } else if (obj instanceof Checkpoint checkpoint) {
            for (Checkpoint c : checkpoints) {
                if (c.equals(checkpoint)) {
                    return true;
                }
            }

        }
        return false;
    }

    public void remove(Object obj) {
        if (obj instanceof Tile tile) {
            for (Tile[] tiles : blocks) {
                for (Tile t : tiles) {
                    if (t != null) {
                        if (t.equals(tile)) {
                            blocks[tile.col][tile.row] = null;
                            return;
                        }
                    }
                }
            }
        } else if (obj instanceof Spike spike) {
            for (Spike[] points : spikes) {
                for (Spike s : points) {
                    if (s != null) {
                        if (s.equals(spike)) {
                            spikes[spike.col][spike.row] = null;
                            return;
                        }
                    }
                }
            }
        } else if (obj instanceof Checkpoint checkpoint) {
            checkpoints.remove(checkpoint);
        }
    }

    // Additional logic for your level
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (Tile[] tiles : blocks) {
            for (Tile t : tiles) {
                if (t != null) {
                    t.draw(g);
                }
            }
        }
        for (Spike[] points : spikes) {
            for (Spike s : points) {
                if (s != null) {
                    s.draw(g2d);
                }
            }
        }
        for (Checkpoint c : checkpoints) {
            c.draw(g);
        }
    }
}
