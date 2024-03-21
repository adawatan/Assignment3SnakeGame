package com.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;
abstract class Consumable {
    protected Point location = new Point();
    protected Bitmap mBitmap;
    protected Point mSpawnRange;
    protected int mSize;
    protected Context context;
    protected int value;

    public Consumable(Context context, Point spawnRange, int size, int value) {
        this.context = context;
        this.mSpawnRange = spawnRange;
        this.mSize = size;
        this.location.x = -10;
        this.value = value;
        loadBitmap();
        resizeBitmap();
    }

    abstract void loadBitmap();

    void resizeBitmap() {
        mBitmap = Bitmap.createScaledBitmap(mBitmap, mSize, mSize, false);
    }

    void spawn() {
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }
    Point getLocation() {
        return location;
    }
    void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmap, location.x * mSize, location.y * mSize, paint);
    }

}
