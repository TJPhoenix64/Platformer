import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LevelGenerator {
    static Level level = new Level();

    public Level getContentsOfFile(String fileName) {
        Scanner sc;
        try {
            sc = new Scanner(new File(fileName));
            String line = sc.nextLine();
            String[] sets = line.split("_");
            int x;
            int y;
            for (String elem : sets) {
                elem = elem.substring(1, elem.length() - 1);
                String[] items = elem.split(", ");
                x = Integer.parseInt(items[0]);
                y = Integer.parseInt(items[1]);
                Tile tile = new Tile(x, y, false);
                level.addTile(tile);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Oh No! something went wrong with the file");
        }
        return level;
    }

}
