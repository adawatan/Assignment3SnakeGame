package com.example.snakegame;

import android.content.Context;
import android.graphics.BitmapFactory;

public class RockObstacle extends Obstacle {
    public RockObstacle(Context context, int blockSize) {
        super(context, blockSize);
    }

    @Override
    protected void loadBitmap(Context context) {
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock);
    }
}
