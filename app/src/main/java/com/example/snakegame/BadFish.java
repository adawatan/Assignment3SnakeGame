package com.example.snakegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;

class BadFish extends Consumable {

    public BadFish(Context context, Point spawnRange, int size, SoundManager soundManager) {
        super(context, spawnRange, size, -1,soundManager);

    }

    @Override
    void loadBitmap() {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bad_apple);
    }

    @Override
    public void playSound() {
        soundManager.playBadSound();
    }
}
