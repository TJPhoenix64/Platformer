package com.tyler.platformer;

import java.io.IOException;
import javax.imageio.ImageIO;

public class Tile extends Thing {

    public Tile(int col, int row, boolean isTemp) {
        super(col, row, isTemp);
        try {
            image = ImageIO.read(Tile.class.getResource(GameConstants.Images.DIRT));

        } catch (IOException e) {
        }
    }

    public Tile(int col, int row) {
        this(col, row, false);
    }

}
