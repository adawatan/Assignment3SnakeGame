package com.example.snakegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.SoundPool;

class Apple extends Consumable {
    protected int mEat_ID;
    public Apple(Context context, Point spawnRange, int size, SoundPool mSP, int mEat_ID) {
        super(context, spawnRange, size, 1, mSP);
        this.mEat_ID = mEat_ID;
    }

    @Override
    void loadBitmap() {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
    }

    @Override
    public void playSound() {
        mSP.play(mEat_ID, 1, 1, 0, 0, 1);
    }
}
