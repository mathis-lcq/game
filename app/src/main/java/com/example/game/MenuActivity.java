package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button playGyroscopeGameButton = findViewById(R.id.playGyroscopeGameButton);
        Button playShakeGameButton = findViewById(R.id.playShakeGameButton);
        Button playPongGameButton = findViewById(R.id.playPongGameButton);
        Button playQuizzButton = findViewById(R.id.playQuizzButton);
        Button playNumericQuizButton = findViewById(R.id.playNumericQuizButton);
        Button playSoccerButton = findViewById(R.id.playSoccerButton);

        playGyroscopeGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, GyroscopeGame.class);
            startActivity(intent);
        });

        playShakeGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ShakeGame.class);
            startActivity(intent);
        });

        playPongGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, PongGame.class);
            startActivity(intent);
        });

        playQuizzButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, Quizz.class);
            startActivity(intent);
        });

        playNumericQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, NumericQuiz.class);
            startActivity(intent);
        });

        playSoccerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, BasketBall.class);
            startActivity(intent);
        });
    }
}
