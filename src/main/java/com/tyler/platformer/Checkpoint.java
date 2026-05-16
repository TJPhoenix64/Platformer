package com.tyler.platformer;

import java.io.IOException;
import javax.imageio.ImageIO;

public class Checkpoint extends Thing {

    public Checkpoint(int col, int row, boolean isTemp) {
        super(col, row, isTemp);
        try {
            image = ImageIO.read(Checkpoint.class.getResource(GameConstants.Images.FLAG));
        } catch (IOException e) {
        }
    }

    public Checkpoint(int col, int row) {
        this(col, row, false);
    }
}
