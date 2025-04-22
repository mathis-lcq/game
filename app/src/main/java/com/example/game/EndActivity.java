package com.example.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TextView resultTextView = findViewById(R.id.resultTextView);
        Button replayButton = findViewById(R.id.replayButton);
        Button menuButton = findViewById(R.id.menuButton);

        boolean isDuoChallenge = getIntent().getBooleanExtra("IS_DUO_CHALLENGE", false);

        if (isDuoChallenge) {
            SharedPreferences preferences = getSharedPreferences("Challenge", MODE_PRIVATE);
            int totalVictories = preferences.getInt("TOTAL_VICTORIES", 0);
            int totalGames = getIntent().getIntExtra("TOTAL_GAMES", 3);

            if (totalVictories > totalGames / 2) {
                resultTextView.setText("Victory! You won the Duo Challenge!");
            } else {
                resultTextView.setText("Defeat! You lost the Duo Challenge.");
            }

            replayButton.setOnClickListener(v -> {
                Intent intent = new Intent(EndActivity.this, MenuActivity.class);
                intent.putExtra("RESTART_DUO_CHALLENGE", true);
                startActivity(intent);
                finish();
            });
        } else {
            int playerScore = getIntent().getIntExtra("SCORE", 0);
            boolean isVictory = getIntent().getBooleanExtra("VICTORY", false);

            if (isVictory) {
                resultTextView.setText("Victory!");
            } else {
                resultTextView.setText("Defeat!");
            }

            replayButton.setOnClickListener(v -> {
                String currentGame = getIntent().getStringExtra("CURRENT_GAME");
                try {
                    Intent intent = new Intent(EndActivity.this, Class.forName(currentGame));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                finish();
            });
        }

        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(EndActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
