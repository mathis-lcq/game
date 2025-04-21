package com.example.game;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TextView scoreTextView = findViewById(R.id.scoreTextView);
        TextView resultTextView = findViewById(R.id.resultTextView);
        Button replayButton = findViewById(R.id.replayButton);
        Button menuButton = findViewById(R.id.menuButton);

        final String currentGameClass = getIntent().getStringExtra("CURRENT_GAME");
        int totalVictories = getIntent().getIntExtra("TOTAL_VICTORIES", 0);
        int totalGames = getIntent().getIntExtra("TOTAL_GAMES", 1);
        String mode = getIntent().getStringExtra("MODE");

        scoreTextView.setText("Your Score: " + totalVictories +" / "+ totalGames);

        if (totalVictories>=2) {
            resultTextView.setText("Victory!");
            mediaPlayer = MediaPlayer.create(this, R.raw.win);
        } else {
            resultTextView.setText("Defeat!");
            mediaPlayer = MediaPlayer.create(this, R.raw.defeat);
        }

        // Lancer la musique
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        }

        replayButton.setOnClickListener(v -> {
            getSharedPreferences("SoloChallenge", MODE_PRIVATE).edit().putInt("TOTAL_VICTORIES", 0).apply();
            // Redirigez vers le jeu approprié
            assert currentGameClass != null;
            Intent intent = null;
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
