package com.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.SoundPool;

import java.util.Random;
abstract class Consumable implements SoundEffect{
    protected Point location = new Point();
    protected Bitmap mBitmap;
    protected Point mSpawnRange;
    protected int mSize;
    protected Context context;
    protected int value;
    protected SoundPool mSP;

    public Consumable(Context context, Point spawnRange, int size, int value, SoundPool mSP) {
        this.context = context;
        this.mSpawnRange = spawnRange;
        this.mSize = size;
        this.location.x = -10;
        this.value = value;
        this.mSP = mSP;
        loadBitmap();
        resizeBitmap();
    }

    abstract void loadBitmap();
    public abstract void playSound();
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
