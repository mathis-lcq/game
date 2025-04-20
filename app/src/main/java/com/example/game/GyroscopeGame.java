package com.example.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.graphics.Rect;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class GyroscopeGame extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "GyroscopeGame";

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private FrameLayout gameFrame;
    private View player;
    private TextView scoreTextView;

    private int score = 0;
    private static int WINNING_SCORE = 50;
    private boolean isGameOver = false;

    private Handler handler = new Handler();
    private Runnable scoreUpdater = new Runnable() {
        @Override
        public void run() {
            if (!isGameOver) {
                score++;
                scoreTextView.setText("Score: " + score);
                handler.postDelayed(this, 1000);
            }
        }
    };

    private Runnable obstacleSpawner = new Runnable() {
        @Override
        public void run() {
            if (!isGameOver) {
                spawnObstacle();
                handler.postDelayed(this, 2000); // Spawn obstacle every 2 seconds
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope_game);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        gameFrame = findViewById(R.id.gameFrame);
        player = findViewById(R.id.player);
        scoreTextView = findViewById(R.id.scoreTextView);

        handler.post(scoreUpdater);
        handler.post(obstacleSpawner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(scoreUpdater);
        handler.removeCallbacks(obstacleSpawner);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[1]; // Use y-axis for left-right movement
            movePlayer(x);
        }
    }

    private void movePlayer(float x) {
        // Adjust player position based on gyroscope input
        float newX = player.getX() - x * 10;
        if (newX > 0 && newX < gameFrame.getWidth() - player.getWidth()) {
            player.setX(newX);
        }
    }

    private void spawnObstacle() {
        final View obstacle = new View(this);
        obstacle.setBackgroundColor(getResources().getColor(android.R.color.black));

        Random random = new Random();
        int obstacleSize = 50;
        int obstacleX = random.nextInt(gameFrame.getWidth() - obstacleSize);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(obstacleSize, obstacleSize);
        params.leftMargin = obstacleX;
        params.topMargin = 0;
        obstacle.setLayoutParams(params);

        gameFrame.addView(obstacle);

        // Animate obstacle falling
        obstacle.animate()
                .translationY(gameFrame.getHeight())
                .setDuration(3000)
                .withEndAction(() -> gameFrame.removeView(obstacle));

        // Check for collisions during the descent
        final Runnable collisionChecker = new Runnable() {
            @Override
            public void run() {
                if (isCollision(player, obstacle)) {
                    Log.d(TAG, "Collision detected during descent!");
                    isGameOver = true;
                    endGame();
                } else {
                    if (!isGameOver) {
                        handler.postDelayed(this, 100); // Check every 100ms
                    }
                }
            }
        };
        handler.post(collisionChecker);

        Log.d(TAG, "Obstacle spawned at X: " + obstacleX);
    }

    private void endGame() {
        SharedPreferences preferences = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        int totalVictories = preferences.getInt("TOTAL_VICTORIES", 0);

        if (score >= WINNING_SCORE) {
            totalVictories++;
        }

        preferences.edit().putInt("TOTAL_VICTORIES", totalVictories).apply();


        if (getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false)) {
            finish();
        } else {
            Intent intent = new Intent(GyroscopeGame.this, EndActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("CURRENT_GAME", GyroscopeGame.class.getName());
            intent.putExtra("VICTORY", score > WINNING_SCORE);
            startActivity(intent);
            finish();
        }
    }

    private boolean isCollision(View player, View obstacle) {
        boolean collision = Rect.intersects(getRect(player), getRect(obstacle));
        Log.d(TAG, "Collision check between player and obstacle: " + collision);
        return collision;
    }

    private Rect getRect(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
