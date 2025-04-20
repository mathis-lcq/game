package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TextView scoreTextView = findViewById(R.id.scoreTextView);
        TextView resultTextView = findViewById(R.id.resultTextView);
        Button replayButton = findViewById(R.id.replayButton);
        Button menuButton = findViewById(R.id.menuButton);

        int score = getIntent().getIntExtra("SCORE", 0);
        boolean isVictory = getIntent().getBooleanExtra("VICTORY", false);
        final String currentGameClass = getIntent().getStringExtra("CURRENT_GAME");

        scoreTextView.setText("Your Score: " + score);

        if (isVictory) {
            resultTextView.setText("Victory!");
        } else {
            resultTextView.setText("Defeat!");
        }

        replayButton.setOnClickListener(v -> {
            // Redirigez vers le jeu approprié
            assert currentGameClass != null;
            Intent intent = null; // ou GyroscopeGame.class
            try {
                intent = new Intent(EndActivity.this, Class.forName(currentGameClass));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            startActivity(intent);
            finish();
        });

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(EndActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
