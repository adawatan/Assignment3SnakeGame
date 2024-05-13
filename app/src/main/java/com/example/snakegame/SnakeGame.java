package com.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Rect;
import androidx.core.content.res.ResourcesCompat;
import java.util.ArrayList;
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
    private SoundManager soundManager;
    // The size in segments of the playable area
    private int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;

    // How many points does the player have
    private int mScore;
    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Bitmap mBackground;
    private Typeface mCustomFont;
    private HighScoreManager highScoreManager;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;
    private boolean hasApple = false;
    private boolean hasGoldenApple = false;

    private List<Consumable> consumables = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private int blockSize;

    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);
        // Work out how many pixels each block is
        // How many blocks of the same size will fit into the height
        initializeScreen(size);
        //Initialize the SoundPool
        soundManager = new SoundManager(context);
        // Initialize the drawing objects
        initializeDrawObjects();
        // Call the constructors of our two game objects
        callConstructorObjects(context);
        //initialize for pause button
        initializePauseButton();
        //initialize for the background image
        initializeBackGroundImage(context,size);
        //initialize text font
        initializeTextFont(context);
        initializeObstacles(context);
        highScoreManager = new HighScoreManager(context);
    }

    //Initialize methods
    private void initializeScreen(Point size){
        // Work out how many pixels each block is
        blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;
    }

    private void initializeDrawObjects(){
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
    }

    private void initializePauseButton(){
        int pauseButtonWidth = 2170;
        int pauseButtonHeight = 900;
        int pauseButtonPadding = 30;
        pauseButton = new Rect(2140, 870, pauseButtonWidth + pauseButtonPadding, pauseButtonHeight + pauseButtonPadding);
    }

    private void initializeBackGroundImage(Context context, Point size){
        mBackground= BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
        mBackground = Bitmap.createScaledBitmap(mBackground, size.x, size.y, false);
    }

    private void initializeTextFont(Context context){
        mCustomFont = ResourcesCompat.getFont(context, R.font.cookie_crisp);
        mPaint.setTypeface(mCustomFont);
    }

    private void callConstructorObjects(Context context){
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize, soundManager);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);
    }
    private void initializeObstacles(Context context) {
        obstacles.clear();
    }
    private List<Point> getConsumableLocations() {
        List<Point> locations = new ArrayList<>();
        for (Consumable consumable : consumables) {
            locations.add(consumable.getLocation());
        }
        return locations;
    }

    // Called to start a new game
    public void newGame() {

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        obstacles.clear();
        consumables.clear();
        hasApple = false;
        // Get the apple ready for dinner
        mApple.spawn();
        consumables.add(mApple);
        scheduleGoldenAppleSpawn();
        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();

        isNewGame = true;
        obstacles.forEach(obstacle -> obstacle.spawnObstacle(new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), mSnake.getSegmentLocations(), getConsumableLocations()));
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

        List<Consumable> toRemove = new ArrayList<>();
        boolean consumedApple = false;

        for (Consumable consumable : consumables) {
            if (mSnake.checkDinner(consumable.getLocation())) {
                consumable.playSound();
                toRemove.add(consumable);

                if (consumable instanceof BadApple) {
                    if (!mSnake.isGolden()) {
                        mSnake.shrink();
                        mScore += consumable.value;
                        spawnObstacle(2);
                    }
                } else if (consumable instanceof Apple) {
                    mScore += consumable.value;
                    if(highScoreManager.getHighScore() < mScore){
                        highScoreManager.saveHighScore(mScore);
                    }
                    adjustSnakeSize(consumable.value);
                    hasApple = false;
                    consumedApple = true;
                } else if (consumable instanceof GoldenApple) {
                    ((GoldenApple) consumable).activateEffects(mSnake);
                    obstacles.clear();
                    hasGoldenApple = false;
                }
            }
        }

        consumables.removeAll(toRemove);
        if (consumedApple && !hasApple) {
            spawnNewApples();
            spawnObstacle(1);
        }

        checkSnakeDeath();
    }
    private void adjustSnakeSize(int value) {
        if (value > 0) {
            for (int i = 0; i < value; i++) {
                mSnake.grow();
            }
        } else if (value < 0) {
            for (int i = 0; i < Math.abs(value); i++) {
                mSnake.shrink();
            }
        }
    }
    private void checkSnakeDeath() {
        if (mSnake.detectDeath(obstacles)) {
            soundManager.playDeathSound();
            mPaused = true;
            isNewGame = true;
        }
    }
    private void spawnObstacle(int count) {
        for (int i = 0; i < count; i++) {
            RockObstacle rock = new RockObstacle(getContext(), blockSize);
            rock.spawnObstacle(new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), mSnake.getSegmentLocations(), getConsumableLocations());
            obstacles.add(rock);
        }
    }

    private void spawnNewApples() {
        if (!hasApple) {
            Apple newApple = new Apple(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize, soundManager);
            newApple.spawn();
            consumables.add(newApple);
            hasApple = true;
        }

        BadApple badApple = new BadApple(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize, soundManager);
        badApple.spawn();
        consumables.add(badApple);
    }

    private void scheduleGoldenAppleSpawn() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.postDelayed(() -> {
            if (mPlaying && !mPaused && !hasGoldenApple) {
                GoldenApple goldenApple = new GoldenApple(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize, soundManager);
                goldenApple.spawn();
                consumables.add(goldenApple);
                hasGoldenApple = true;
                scheduleGoldenAppleSpawn();
            }
        }, 30000); // adjust the timing as needed for gameplay balance
    }
    // Do all the drawing
    public void draw() {
        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            //Added the background image
            drawbackground(mCanvas);
            // Set the size, color, and font of the mPaint for the text
            drawSetText(mCanvas);
            //Draw the score and names of students
            drawScoreAndName(mCanvas);
            //Draw the pause button as a white square
            drawPause(mCanvas);
            //Draw the apple and snake
            drawGameObjects(mCanvas);
            // Draw some text while paused
            drawPauseMessage(mCanvas);
            // Draw the highscore
            drawHighscore(mCanvas);





            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawbackground(Canvas canvas) {
        canvas.drawBitmap(mBackground,0,0,null);
    }

    private void drawHighscore(Canvas canvas){
        // Set the color and font size for the paint
        Paint hpaint = new Paint();
        hpaint.setColor(Color.WHITE);
        hpaint.setTextSize(40);

        // Draw the current high score
        int highScore = highScoreManager.getHighScore();
        canvas.drawText("HS: " + highScore, 10, 930, hpaint);
    }
    private void drawSetText(Canvas canvas){
        mPaint.setTypeface(mCustomFont);
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(120);
    }

    private void drawScoreAndName(Canvas canvas) {
        mCanvas.drawText("" + mScore, 25, 870, mPaint);
    }

    private void drawPause(Canvas canvas){
        mPaint.setColor(Color.WHITE);
        mCanvas.drawRect(pauseButton, mPaint);
    }

    private void drawGameObjects(Canvas canvas){
        // Draw the snake
        mSnake.draw(canvas, mPaint);

        for (Consumable consumable : consumables) {
            consumable.draw(canvas, mPaint);
        }

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(canvas, mPaint);
        }
    }

    private void drawPauseMessage(Canvas canvas) {
        if (mPaused) {

            // Set the size and color of the mPaint for the text
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(250);

            // Determine the message based on if game is paused or new game is created.
            String message = isNewGame ? getResources().getString(R.string.tap_to_play) : "Game Paused";

            // Draw the message
            mCanvas.drawText(message, 500, 500, mPaint);
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