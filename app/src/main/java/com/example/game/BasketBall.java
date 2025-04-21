package com.example.game;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BasketBall extends AppCompatActivity {
    private View arrow;
    private ImageView ball;
    private ImageView basket;
    private float startX, startY;
    private boolean isDragging = false;
    private float velocityX, velocityY;
    private boolean isLaunched = false;
    private boolean isBouncedByGoalkeeper = false;
    private float initialBallX, initialBallY;
    private RelativeLayout rootLayout;
    private TextView scoreTextView;
    private TextView timerTextView;
    private ImageView goalkeeper;
    private int score = 0;
    private final float SPEED = 0.15f;
    private final float MAX_SWIPE_DISTANCE = 300;
    private final float MIN_SWIPE_DISTANCE = 10;
    private static final int WINNING_SCORE = 8;
    private CountDownTimer countDownTimer;
    private final long GAME_DURATION = 30000; // 30 seconds

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_ball);

        rootLayout = findViewById(R.id.rootLayout);
        ball = findViewById(R.id.ball);
        basket = findViewById(R.id.basket);
        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timeTextView);
        goalkeeper = findViewById(R.id.goalkeeper);

        // Ajouter une flèche directionnelle
        arrow = new View(this);
        arrow.setBackground(createArrowDrawable());
        RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(10, 10);
        rootLayout.addView(arrow, arrowParams);
        arrow.setVisibility(View.INVISIBLE);

        // Placer la balle à sa position initiale
        ball.post(() -> {
            initialBallX = getScreenWidth() / 2f - ball.getWidth() / 2f;
            initialBallY = getScreenHeight() - ball.getHeight() - 200;
            resetBall();
        });

        // animer le gardien
        startGoalkeeperAnimation();

        // Écouter les événements de toucher sur tout l'écran (rootLayout)
        rootLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    isDragging = true;
                    arrow.setVisibility(View.VISIBLE);
                    arrow.setLayoutParams(new RelativeLayout.LayoutParams(10, 10));
                    arrow.setX(startX - arrow.getWidth() / 2f);
                    arrow.setY(startY - arrow.getHeight() / 2f);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (isDragging) {
                        float deltaX = Math.min(startX - event.getRawX(), MAX_SWIPE_DISTANCE);
                        float deltaY = Math.min(startY - event.getRawY(), MAX_SWIPE_DISTANCE);
                        updateArrow(deltaX, deltaY);
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    isDragging = false;
                    arrow.setVisibility(View.INVISIBLE);

                    float deltaX = Math.min(Math.max(startX - event.getRawX(), -MAX_SWIPE_DISTANCE), MAX_SWIPE_DISTANCE);
                    float deltaY = Math.min(Math.max(startY - event.getRawY(), -MAX_SWIPE_DISTANCE), MAX_SWIPE_DISTANCE);

                    velocityX = deltaX * SPEED;
                    velocityY = deltaY * SPEED;
                    launchBall();

                    if (!isTimerRunning()) {
                        startTimer();
                    }

                    return true;
            }
            return false;
        });
    }

    private void launchBall() {
        isLaunched = true;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();

            float newX = ball.getX() + velocityX * (1 - progress);
            float newY = ball.getY() + velocityY * (1 - progress);

            // Check for collision with left or right boundary
            if (newX <= 0) {
                newX = 0;
                velocityX = Math.abs(velocityX) * 0.8f; // Reverse and dampen velocity
            } else if (newX + ball.getWidth() >= getScreenWidth()) {
                newX = getScreenWidth() - ball.getWidth();
                velocityX = -Math.abs(velocityX) * 0.8f; // Reverse and dampen velocity
            } else if (!isBouncedByGoalkeeper && newY >= goalkeeper.getY() && newY <= goalkeeper.getY() + goalkeeper.getHeight()) {
                if (newX >= goalkeeper.getX() && newX <= goalkeeper.getX() + goalkeeper.getWidth()) {
                    Log.d("Goalkeeper", "X: " + goalkeeper.getX() + ", Y: " + goalkeeper.getY() + ", Width: " + goalkeeper.getWidth() + ", Height: " + goalkeeper.getHeight());
                    velocityY = Math.abs(velocityY) * 0.8f; // Reverse and dampen velocity
                    isBouncedByGoalkeeper = true;
                }
            }
            ball.setX(newX);
            ball.setY(newY);
            // show console goalkeeper position and size


            if (progress >= 1 || newY <= -ball.getHeight() || (velocityX == 0 && velocityY == 0)) {
                checkScore(newX, newY);
                // reset velocity
                velocityX = 0;
                velocityY = 0;
                resetBall();
                isBouncedByGoalkeeper = false;
            }
        });

        animator.start();
    }

    private void checkScore(float ballX, float ballY) {
        // Assuming the goal is at the top center of the screen
        float goalX = getScreenWidth() / 2f;
        float goalY = basket.getY();
        float goalWidth = basket.getWidth(); // Width of the goal

        if (ballX + ball.getWidth() / 2f >= goalX - goalWidth / 2 &&
                ballX + ball.getWidth() / 2f <= goalX + goalWidth / 2 &&
                ballY + ball.getHeight() / 2f <= goalY + basket.getHeight()) {
            score++;
            Toast toast = Toast.makeText(this, "But!!", Toast.LENGTH_SHORT);
            scoreTextView.setText("Score: " + score);
            ball.setVisibility(View.INVISIBLE);
            if (score >= WINNING_SCORE) {
                endGame();
            }
        }
    }

    private void resetBall() {
        ball.setX(initialBallX);
        ball.setY(initialBallY);
        isLaunched = false;
        ball.setVisibility(View.VISIBLE);
    }

    private void updateArrow(float deltaX, float deltaY) {
        float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Mettre à jour la taille du cercle en fonction de la longueur du swipe
        float newSize = Math.max(Math.min(length, MAX_SWIPE_DISTANCE), MIN_SWIPE_DISTANCE); // Limiter la taille entre 10 et 300
        arrow.setLayoutParams(new RelativeLayout.LayoutParams((int) newSize, (int) newSize));

        // Garder le cercle centré sur la position initiale du clic
        arrow.setX(startX - newSize / 2);
        arrow.setY(startY - newSize / 2);
    }

    private GradientDrawable createArrowDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL); // Forme ovale
        drawable.setColor(Color.BLACK);
        drawable.setAlpha(128);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        drawable.setGradientRadius(50); // Rayon du dégradé initial
        drawable.setCornerRadii(new float[]{50, 50, 50, 50, 50, 50, 50, 50}); // Cercle parfait
        return drawable;
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(GAME_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                endGame();
                ball.setVisibility(View.INVISIBLE);
                // change activity
            }
        }.start();
    }

    private void endGame() {
        SharedPreferences preferences = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        int totalVictories = preferences.getInt("TOTAL_VICTORIES", 0);

        if (score >= WINNING_SCORE) {
            totalVictories++;
        }

        preferences.edit().putInt("TOTAL_VICTORIES", totalVictories).apply();

        if (getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false)) {
            Intent intent = new Intent(this, SoloResultActivity.class);
            intent.putExtra("VICTORY", score >= WINNING_SCORE);
            intent.putExtra("TOTAL_VICTORIES", getSharedPreferences("SoloChallenge", MODE_PRIVATE).getInt("TOTAL_VICTORIES", 0));
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(BasketBall.this, EndActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("VICTORY", score >= WINNING_SCORE);
            intent.putExtra("CURRENT_GAME", BasketBall.class.getName());
            startActivity(intent);
            finish();
        }
    }
    private void startGoalkeeperAnimation() {
        float startX = 0;
        float endX = getScreenWidth() - 300;

        ValueAnimator animator = ValueAnimator.ofFloat(startX, endX);
        animator.setDuration(2000); // Durée pour aller d’un côté à l’autre
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {
            float animatedX = (float) animation.getAnimatedValue();
            goalkeeper.setX(animatedX);
        });

        // Placer le gardien verticalement au centre
        goalkeeper.setY(400);

        animator.start();
    }

    private boolean isTimerRunning() {
        return countDownTimer != null;
    }

}
