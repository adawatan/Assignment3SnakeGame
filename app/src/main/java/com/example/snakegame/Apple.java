package com.example.snakegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;

class Apple extends Consumable {

    public Apple(Context context, Point spawnRange, int size) {
        super(context, spawnRange, size, 1);
    }

    @Override
    void loadBitmap() {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);
    }
}
