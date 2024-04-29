package com.example.snakegame;
import android.content.Context;
import android.content.SharedPreferences;


public class ScoreManager
{
    private int highScore = 0;
    public int getHighScore()
    {
        return highScore;
    }
    public void setHighScore(int score)
    {
        if (score > highScore) {
            highScore = score;
        }
    }
    public void saveHighScore(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyGamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("highScore", highScore);
        editor.apply();
    }
    public void loadHighScore(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyGamePrefs", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highScore", 0);
    }
}
