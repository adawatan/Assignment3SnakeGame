package com.example.snakegame;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;


public class HighScoreManager {

    private int highScore = 0;

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            // Optionally, you can save the new high score to storage here
        }
    }

    // Add methods for saving and loading the high score from storage if needed
    // Save the high score to SharedPreferences
    public void saveHighScore(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyGamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("highScore", highScore);
        editor.apply();
    }

    // Load the high score from SharedPreferences
    public void loadHighScore(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyGamePrefs", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highScore", 0);
    }

}
