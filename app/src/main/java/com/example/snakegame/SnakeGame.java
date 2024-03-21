package com.example.snakegame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import android.graphics.Rect;
import androidx.core.content.res.ResourcesCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SnakeGame extends SurfaceView implements Runnable{

    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;
    private boolean isNewGame = true;
    private Rect pauseButton;

    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Bitmap mBackground;
    private Typeface mCustomFont;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;
    private List<Consumable> consumables = new ArrayList<>();
    private int blockSize;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        // Work out how many pixels each block is
        blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        //initialize for pause button
        int pauseButtonWidth = 100;
        int pauseButtonHeight = 100;
        int pauseButtonPadding = 30;
        pauseButton = new Rect(pauseButtonPadding, pauseButtonPadding, pauseButtonWidth + pauseButtonPadding, pauseButtonHeight + pauseButtonPadding);

        //initialize for the background image
        mBackground= BitmapFactory.decodeResource(context.getResources(), R.drawable.grass);
        mBackground = Bitmap.createScaledBitmap(mBackground, size.x, size.y, false);

        //initialize text font
        mCustomFont = ResourcesCompat.getFont(context, R.font.cookie_crisp);
        mPaint.setTypeface(mCustomFont);
    }

    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        consumables.clear();
        // Get the apple ready for dinner
        mApple.spawn();
        consumables.add(mApple);

        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();

        isNewGame = true;

    }


    // Handles the game loop
    @Override
    public void run() {
        while (mPlaying) {
            if (!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }

            draw();
        }
    }

    // Check to see if it is time for an update
    public boolean updateRequired() {

        // Run at 10 frames per second
        final long TARGET_FPS = 10;
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if (mNextFrameTime <= System.currentTimeMillis()) {
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime = System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }


    // Update all the game objects
    public void update() {

        // Move the snake
        mSnake.move();

        List<Consumable> consumedItems = new ArrayList<>();
        List<Consumable> newItems = new ArrayList<>();
        for (Consumable consumable : consumables) {
            if (mSnake.checkDinner(consumable.getLocation())) {

                // adjust the score according to the value of the consumable
                mScore += consumable.value;
                mSP.play(mEat_ID, 1, 1, 0, 0, 1);
                consumedItems.add(consumable);

                if (consumable.value > 0) {
                    for (int i = 0; i < consumable.value; i++) {
                        mSnake.grow();
                    }
                } else if (consumable.value < 0) {
                    for (int i = 0; i < Math.abs(consumable.value); i++) {
                        mSnake.shrink();
                    }
                }

                if (consumable instanceof Apple) {
                    // Spawns a new apple
                    Apple newApple = new Apple(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
                    newApple.spawn();
                    newItems.add(newApple);

                    // Spawns a bad apple every time a "good" apple is consumed.
                    BadApple badApple = new BadApple(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
                    badApple.spawn();
                    newItems.add(badApple);
                }
            }
        }
        consumables.removeAll(consumedItems);
        consumables.addAll(newItems);

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            mSP.play(mCrashID, 1, 1, 0, 0, 1);

            mPaused = true;
            isNewGame = true;
        }

    }


    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();

            //Added the background image
            mCanvas.drawBitmap(mBackground, 0, 0, null);


            // Set the size, color, and font of the mPaint for the text
            mPaint.setTypeface(mCustomFont);
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(120);

            // Draw the score
            mCanvas.drawText("" + mScore, 150, 120, mPaint);

            // Draw the names of the students working on this assignment
            mCanvas.drawText("Alexis Dawatan, Wei Chong", 1750, 120, mPaint);

            //Draw the pause button as a white square
            mPaint.setColor(Color.WHITE);
            mCanvas.drawRect(pauseButton, mPaint);

            // Draw the apple and the snake
            // mApple.draw(mCanvas, mPaint);
            mSnake.draw(mCanvas, mPaint);

            // Draw all the consumables
            for (Consumable consumable : consumables) {
                consumable.draw(mCanvas, mPaint);
            }

            // Draw some text while paused
            if (mPaused) {

                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(250);

                // Determine the message based on if game is paused or new game is created.
                String message = isNewGame ? getResources().getString(R.string.tap_to_play) : "Game Paused";

                // Draw the message
                mCanvas.drawText(message, 200, 700, mPaint);
            }

            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();

                //Detect if button is clicked
                if (pauseButton.contains(x, y)) {
                   mPaused = !mPaused;
                   return true;
                }
                if (mPaused) {
                    mPaused = false;
                    if (isNewGame) {
                        newGame();
                        isNewGame = false;
                    }
                    return true;
                }

                mSnake.switchHeading(motionEvent);
                break;
        }
        return true;
    }

    // Stop the thread
    public void pause() {
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    // Start the thread
    public void resume() {
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }
}
