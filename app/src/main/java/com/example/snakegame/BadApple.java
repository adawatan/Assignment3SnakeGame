package com.example.snakegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.SoundPool;

class BadApple extends Consumable {

    protected int mbadID;

    public BadApple(Context context, Point spawnRange, int size, SoundPool mSP, int mbadID) {
        super(context, spawnRange, size, -1, mSP);
        this.mbadID = mbadID;
    }

    @Override
    void loadBitmap() {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bad_apple);
    }

    @Override
    public void playSound() {
        mSP.play(mbadID, 1, 1, 0, 0, 1);
    }
}
