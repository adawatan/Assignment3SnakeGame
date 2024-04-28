package com.example.snakegame;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class GameOverActivity {
    private TextView highScoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        highScoreTextView = findViewById(R.id.high_score_text_view);

        // Display the current high score
        highScoreTextView.setText("High Score: " + highScoreManager.getHighScore());
    }
}
