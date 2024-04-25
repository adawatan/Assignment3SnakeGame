package com.example.snakegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;

class GoldenApple extends Consumable {
    public GoldenApple(Context context, Point spawnRange, int size, SoundManager soundManager) {
        super(context, spawnRange, size, 2, soundManager);
    }

    @Override
    void loadBitmap() {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.golden);
    }

    @Override
    public void playSound() {
        soundManager.playEatSound();
    }

    @Override
    void spawn() {
        super.spawn();
    }
    public void activateEffects(Snake snake) {
        snake.turnGolden();
        snake.setInvulnerable(true);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            snake.revertToNormal();
            snake.setInvulnerable(false);
        }, 10000);
    }

}

