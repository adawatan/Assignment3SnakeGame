package com.example.snakegame;
import android.content.Context;
import android.content.SharedPreferences;




public class HighScoreManager {
        private SharedPreferences prefs;
        private static final String HIGH_SCORE_KEY = "high_score_key";

        public HighScoreManager(Context context) {
            prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        }

        public int getHighScore() {
            return prefs.getInt(HIGH_SCORE_KEY, 0); // 0 is the default value if no high score is found
        }

        public void saveHighScore(int score) {
            int currentHighScore = getHighScore();
            if (score > currentHighScore) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(HIGH_SCORE_KEY, score);
                editor.apply();
            }
        }




}
