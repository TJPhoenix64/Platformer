import java.io.File;
import java.util.Scanner;

public class LevelGenerator {

    public Level getContentsOfFile(String fileName) {
        Level level = new Level();

        Scanner sc;
        try {
            sc = new Scanner(new File(fileName));
            int row;
            int col;
            int rotation;
            if (sc.hasNextLine()) {
                String firstLine = sc.nextLine();
                // System.out.println("got tiles");
                if (firstLine.length() > 7) {
                    firstLine = firstLine.substring(6);
                    // System.out.println(firstLine);
                    String[] tiles = firstLine.split("_");
                    for (String tile : tiles) {
                        String[] items = tile.split(",");
                        col = Integer.parseInt(items[0]);
                        row = Integer.parseInt(items[1]);
                        Tile t = new Tile(col, row, false);
                        level.addObject(t);
                    }
                }
            }

            if (sc.hasNextLine()) {
                String secondLine = sc.nextLine();
                // System.out.println("got spikes");
                if (secondLine.length() > 8) {
                    secondLine = secondLine.substring(7);
                    // System.out.println(secondLine);
                    String[] spikes = secondLine.split("_");

                    for (String spike : spikes) {
                        String[] items = spike.split(",");
                        col = Integer.parseInt(items[0]);
                        row = Integer.parseInt(items[1]);
                        rotation = Integer.parseInt(items[2]);
                        Spike s = new Spike(col, row, rotation);
                        level.addObject(s);
                    }
                }
            }
            if (sc.hasNextLine()) {
                String thirdLine = sc.nextLine();
                // System.out.println("Got checkpoints");
                if (thirdLine.length() > 13) {
                    thirdLine = thirdLine.substring(12);
                    // System.out.println(thirdLine);
                    String[] checkpoints = thirdLine.split("_");
                    for (String checkpoint : checkpoints) {
                        String[] items = checkpoint.split(",");
                        col = Integer.parseInt(items[0]);
                        row = Integer.parseInt(items[1]);
                        Checkpoint c = new Checkpoint(col, row);
                        level.addObject(c);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Oh No! something went wrong with the file");
        }
        // System.out.println("This is being run");
        return level;
    }

}
