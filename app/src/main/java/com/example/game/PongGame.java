package com.example.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PongGame extends AppCompatActivity {

    private static final String TAG = "PongGame";

    private FrameLayout gameFrame;
    private View playerPaddle;
    private View botPaddle;
    private View ball;
    private TextView playerScoreTextView;
    private TextView botScoreTextView;

    private int playerScore = 0;
    private int botScore = 0;
    private int screenWidth, screenHeight;

    private float ballX, ballY;
    private float ballSpeedX = 10, ballSpeedY = 10;
    private float playerPaddleY, botPaddleY;

    private static final int WINNING_SCORE = 3;
    private boolean isGameOver = false; // Drapeau pour contrôler la fin du jeu

    private Handler handler = new Handler();
    private Runnable gameUpdater = new Runnable() {
        @Override
        public void run() {
            if (!isGameOver) {
                updateGame();
                handler.postDelayed(this, 30); // Mettre à jour toutes les 30ms
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong_game);

        gameFrame = findViewById(R.id.gameFrame);
        playerPaddle = findViewById(R.id.playerPaddle);
        botPaddle = findViewById(R.id.botPaddle);
        ball = findViewById(R.id.ball);
        playerScoreTextView = findViewById(R.id.playerScoreTextView);
        botScoreTextView = findViewById(R.id.botScoreTextView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        resetGame();

        gameFrame.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                playerPaddleY = event.getY() - playerPaddle.getHeight() / 2;
                updatePaddlePosition(playerPaddle, playerPaddleY);
            }
            return true;
        });
    }

    private void resetGame() {
        ballX = screenWidth / 2;
        ballY = screenHeight / 2;
        playerPaddleY = screenHeight / 2;
        botPaddleY = screenHeight / 2;

        playerScore = 0;
        botScore = 0;
        isGameOver = false; // Réinitialiser le drapeau

        updatePaddlePosition(playerPaddle, playerPaddleY);
        updatePaddlePosition(botPaddle, botPaddleY);
        updateBallPosition();

        playerScoreTextView.setText("Player: " + playerScore);
        botScoreTextView.setText("Bot: " + botScore);

        handler.post(gameUpdater);
    }

    private void updateGame() {
        moveBall();
        moveBotPaddle();

        if (ballY < 0 || ballY > screenHeight) {
            ballSpeedY = -ballSpeedY;
        }

        if (ballX < 0) {
            botScore++;
            botScoreTextView.setText("Bot: " + botScore);
            resetBall();
        }

        if (ballX > screenWidth) {
            playerScore++;
            playerScoreTextView.setText("Player: " + playerScore);
            resetBall();
        }

        if (playerScore >= WINNING_SCORE || botScore >= WINNING_SCORE) {
            endGame();
        }
    }

    private void moveBall() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballX <= playerPaddle.getWidth() && ballY >= playerPaddleY && ballY <= playerPaddleY + playerPaddle.getHeight()) {
            ballSpeedX = -ballSpeedX;
        }

        if (ballX >= screenWidth - botPaddle.getWidth() - ball.getWidth() && ballY >= botPaddleY && ballY <= botPaddleY + botPaddle.getHeight()) {
            ballSpeedX = -ballSpeedX;
        }

        updateBallPosition();
    }

    private void moveBotPaddle() {
        if (ballY > botPaddleY + botPaddle.getHeight() / 2) {
            botPaddleY += 5; // Déplacer vers le bas
        } else {
            botPaddleY -= 5; // Déplacer vers le haut
        }

        updatePaddlePosition(botPaddle, botPaddleY);
    }

    private void updateBallPosition() {
        ball.setX(ballX);
        ball.setY(ballY);
    }

    private void updatePaddlePosition(View paddle, float y) {
        paddle.setY(Math.max(0, Math.min(y, screenHeight - paddle.getHeight())));
    }

    private void resetBall() {
        ballX = screenWidth / 2;
        ballY = screenHeight / 2;
        ballSpeedX = -ballSpeedX;
        updateBallPosition();
    }

    private void endGame() {
        if (!isGameOver) {
            isGameOver = true; // Définir le drapeau pour éviter les appels multiples

            SharedPreferences preferences = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
            int totalVictories = preferences.getInt("TOTAL_VICTORIES", 0);

            if (playerScore >= WINNING_SCORE) {
                totalVictories++;
            }

            preferences.edit().putInt("TOTAL_VICTORIES", totalVictories).apply();

            if (getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false)) {
                finish();
            } else {
                handler.removeCallbacks(gameUpdater);
                Intent intent = new Intent(PongGame.this, EndActivity.class);
                intent.putExtra("SCORE", playerScore);
                intent.putExtra("BOT_SCORE", botScore);
                intent.putExtra("CURRENT_GAME", PongGame.class.getName());
                startActivity(intent);
                finish();
            }
        }
    }
}
