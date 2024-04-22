package com.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;
import java.util.List;

abstract class Obstacle {
    protected Point location = new Point();
    protected Bitmap bitmap;
    protected int size;

    public Obstacle(Context context, int blockSize) {
        this.size = blockSize;
        loadBitmap(context);
        this.bitmap = Bitmap.createScaledBitmap(bitmap, blockSize, blockSize, false);
    }

    abstract void loadBitmap(Context context);

    // Updated spawn method to accept snake and consumables positions
    public void spawnObstacle(Point gameAreaSize, List<Point> snakeBody, List<Point> consumableLocations) {
        Random random = new Random();
        boolean valid;

        do {
            valid = true;
            location.x = random.nextInt(gameAreaSize.x);
            location.y = random.nextInt(gameAreaSize.y);

            // Check against snake body
            for (Point segment : snakeBody) {
                if (segment.equals(location)) {
                    valid = false;
                    break;
                }
            }

            // Check against consumables
            if (valid) {
                for (Point consumable : consumableLocations) {
                    if (consumable.equals(location)) {
                        valid = false;
                        break;
                    }
                }
            }
        } while (!valid); // Continue until a valid position is found
    }

    public Point getLocation() {
        return location;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, location.x * size, location.y * size, paint);
    }
}