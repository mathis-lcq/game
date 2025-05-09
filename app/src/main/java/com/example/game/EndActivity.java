package com.example.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.media.MediaPlayer;


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

        int score = getIntent().getIntExtra("SCORE", 0);
        boolean isVictory = getIntent().getBooleanExtra("VICTORY", false);
        final String currentGameClass = getIntent().getStringExtra("CURRENT_GAME");
        int totalVictories = getIntent().getIntExtra("TOTAL_VICTORIES", 0);
        int totalGames = getIntent().getIntExtra("TOTAL_GAMES", 1);
        String mode = getIntent().getStringExtra("MODE");


        boolean isSoloChallenge = getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false);

        boolean isDuoChallenge = getIntent().getBooleanExtra("IS_DUO_CHALLENGE", false);

        if (isSoloChallenge) {
            if (totalVictories>=2) {
                resultTextView.setText("Victory!");
                mediaPlayer = MediaPlayer.create(this, R.raw.win);
            } else {
                resultTextView.setText("Defeat!");
                mediaPlayer = MediaPlayer.create(this, R.raw.defeat);
            }
            scoreTextView.setText("Your Score: " + totalVictories +" / "+ totalGames);
        } else {
            if (isVictory) {
                resultTextView.setText("Victory!");
                mediaPlayer = MediaPlayer.create(this, R.raw.win);
                scoreTextView.setText("Your Score: "+score);
            } else {
                resultTextView.setText("Defeat!");
                mediaPlayer = MediaPlayer.create(this, R.raw.defeat);
                scoreTextView.setText("Your Score: "+score);
            }
        }
        mediaPlayer.start();




        // if (isDuoChallenge) {
        //    SharedPreferences preferences = getSharedPreferences("Challenge", MODE_PRIVATE);
         // 
           // int totalVictories = preferences.getInt("TOTAL_VICTORIES", 0);
            //int totalGames = getIntent().getIntExtra("TOTAL_GAMES", 3);





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


        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(EndActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
