package com.tyler.platformer;

import java.io.IOException;
import javax.imageio.ImageIO;

public class StartTile extends Tile {

    public StartTile(int col, int row) {
        super(col, row);
        this.x = col * GameConstants.TILE_SIZE;
        this.y = row * GameConstants.TILE_SIZE;
        try {
            image = ImageIO.read(StartTile.class.getResource(GameConstants.Images.START_TILE));
        } catch (IOException e) {
        }
    }

    public StartTile(int col, int row, boolean isTemp) {
        super(col, row, isTemp);
        try {
            image = ImageIO.read(StartTile.class.getResource(GameConstants.Images.START_TILE));
        } catch (IOException e) {
        }
    }

}
