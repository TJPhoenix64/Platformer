
import java.awt.*;
import java.util.ArrayList;

public class Level {

    private Tile[][] blocks;
    private Spike[][] spikes;
    private ArrayList<Checkpoint> checkpoints;
    private Coin[][] coins;

    private int numTiles = 0;
    private int numCheckpoints = 0;
    private int numSpikes = 0;
    private int numObjects = 0;
    private int numCoins = 0;

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
        } else if (obj instanceof Coin coin) {
            coins[coin.col][coin.row] = coin;
            numCoins++;
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

    public Coin[][] getCoins() {
        return coins;
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

    public int getNumCoins() {
        return numCoins;
    }

    public void removeCoin(Coin coin) {
        if (coins[coin.col][coin.row] != null) {
            coins[coin.col][coin.row] = null;
        }
    }

    public final void clear() {
        blocks = new Tile[cols][rows];
        spikes = new Spike[cols][rows];
        checkpoints = new ArrayList<>();
        coins = new Coin[cols][rows];
    }

    public Thing get(int col, int row) {
        if (blocks[col][row] != null) {
            return blocks[col][row];
        }
        if (spikes[col][row] != null) {
            return spikes[col][row];
        }

        for (Checkpoint cp : checkpoints) {
            if (cp.col == col && cp.row == row) {
                return cp;
            }
        }
        if (coins[col][row] != null) {
            return coins[col][row];
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
            return (blocks[tile.col][tile.row] == tile);
        } else if (obj instanceof Spike spike) {
            return (spikes[spike.col][spike.row] == spike);
        } else if (obj instanceof Checkpoint checkpoint) {
            return checkpoints.contains(checkpoint);
        } else if (obj instanceof Coin coin) {
            return coins[coin.col][coin.row] == coin;
        }
        return false;
    }

    public boolean contains(int col, int row) {
        if (col < 0 || col > blocks.length || row < 0 || row > blocks[0].length) {
            return false;
        }
        if (blocks[col][row] != null) {
            return true;
        }
        if (spikes[col][row] != null) {
            return true;
        }

        if (coins[col][row] != null) {
            return true;
        }
        return checkpoints.contains(new Checkpoint(col, row, false))
                || checkpoints.contains(new Checkpoint(col, row, true));
    }

    /**
     * removes any objects in that grid tile
     *
     * @param col
     * @param row
     */
    public void remove(int col, int row) {
        blocks[col][row] = null;
        spikes[col][row] = null;
        checkpoints.remove(new Checkpoint(col, row, false));
        checkpoints.remove(new Checkpoint(col, row, true));
        coins[col][row] = null;

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

        for (Coin[] cs : coins) {
            for (Coin c : cs) {
                if (c != null) {
                    c.draw(g2d);
                }
            }
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("Tiles:");
        for (Tile[] tiles : this.getBlocks()) {
            for (Tile t : tiles) {
                if (t != null && !t.isTemp) {
                    s.append(t).append("_");
                }
            }
        }
        if (s.toString().charAt(s.length() - 1) != ':') {
            s.deleteCharAt(s.length() - 1);
        }
        s.append("\nSpikes:");
        for (Spike[] points : this.getSpikes()) {
            for (Spike spike : points) {
                if (spike != null && !spike.isTemp) {
                    s.append(spike).append("_");
                }
            }
        }
        if (s.toString().charAt(s.length() - 1) != ':') {
            s.deleteCharAt(s.length() - 1);
        }
        s.append("\nCheckpoints:");
        for (Checkpoint checkpoint : this.getCheckpoints()) {
            if (!checkpoint.isTemp) {
                s.append(checkpoint).append("_");
            }
        }
        if (s.toString().charAt(s.length() - 1) != ':') {
            s.deleteCharAt(s.length() - 1);
        }

        s.append("\nCoins:");
        for (Coin[] cs : this.getCoins()) {
            for (Coin c : cs) {
                if (c != null && !c.isTemp) {
                    s.append(c).append("_");
                }
            }
        }
        if (s.toString().charAt(s.length() - 1) != ':') {
            s.deleteCharAt(s.length() - 1);
        }

        return s.toString();

    }
}
