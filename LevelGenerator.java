
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class LevelGenerator {

    public Level getContentsOfFile(String fileName) {
        Level level = new Level();

        Scanner sc;
        try {
            sc = new Scanner(new File(fileName));

            while (sc.hasNextLine()) {
                String OGLine = sc.nextLine();
                String line = OGLine;
                String[] cords;
                String[] items;
                ArrayList<Integer> cols = new ArrayList<>();
                ArrayList<Integer> rows = new ArrayList<>();
                ArrayList<Integer> rotations = new ArrayList<>();
                while (line.length() > 0 && !Character.isDigit(line.charAt(0))) {
                    line = line.substring(1);
                }
                //System.out.println(line);
                cords = line.split("_");

                for (String cord : cords) {
                    if (cord.isEmpty()) {
                        continue; // skip bad
                    }
                    items = cord.split(",");
                    if (items.length < 2) {
                        continue; // skip malformed
                    }
                    try {
                        cols.add(Integer.valueOf(items[0]));
                        rows.add(Integer.valueOf(items[1]));

                        if (items.length > 2) {
                            rotations.add(Integer.valueOf(items[2]));
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping bad cord: " + cord);
                    }
                }
                if (cords[0].isEmpty()) {
                    continue;
                }
                if (OGLine.startsWith("Tiles")) {
                    for (int k = 0; k < cords.length; k++) {
                        Tile t = new Tile(cols.get(k), rows.get(k), false);
                        level.addObject(t);
                    }
                } else if (OGLine.startsWith("Spike")) {
                    for (int k = 0; k < cords.length; k++) {
                        Spike s = new Spike(cols.get(k),
                                rows.get(k),
                                rotations.get(k));
                        level.addObject(s);
                    }
                } else if (OGLine.startsWith("Check")) {
                    for (int k = 0; k < cords.length; k++) {
                        Checkpoint c = new Checkpoint(cols.get(k), rows.get(k));
                        level.addObject(c);
                    }
                } else if (OGLine.startsWith("Coins")) {
                    for (int k = 0; k < cords.length; k++) {
                        Coin c = new Coin(cols.get(k), rows.get(k));
                        level.addObject(c);
                    }
                } else if (OGLine.startsWith("start")) {

                    StartTile t = new StartTile(cols.get(0), rows.get(0), false);
                    level.addStartTile(t);
                    //System.out.println("Adding S-Tile: " + t);
                }

            }
        } catch (FileNotFoundException | NumberFormatException e) {
            System.out.println(e);
        }
        // System.out.println("This is being run");

        return level;
    }

}
