package com.example.game;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.BluetoothConnectionManager;

public class ShakeGame extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView counterTextView;
    private TextView statusTextView;
    private TextView timerTextView;

    private int counter = 0;
    private static final int WINNING_SCORE = 100; // Nouveau seuil pour gagner
    private static final float SHAKE_THRESHOLD = 15.0f; // Seuil de détection de secousse
    private static final long GAME_DURATION = 10000; // Durée du jeu en millisecondes (10 secondes)

    private CountDownTimer gameTimer;
    private BluetoothConnectionManager bluetoothConnectionManager;
    private boolean isServer;
    private boolean isDuoChallenge;
    private boolean isSoloChallenge;
    private boolean isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_game);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        counterTextView = findViewById(R.id.counterTextView);
        statusTextView = findViewById(R.id.statusTextView);
        timerTextView = findViewById(R.id.timerTextView);

        isSoloChallenge = getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false);
        isDuoChallenge = getIntent().getBooleanExtra("IS_DUO_CHALLENGE", false);
        isServer = getIntent().getBooleanExtra("IS_SERVER", false);

        if (isDuoChallenge) {
            bluetoothConnectionManager = new BluetoothConnectionManager(this);
            if (isServer) {
                bluetoothConnectionManager.startServer();
            } else {
                // Assume device address is passed or selected from a list
                String deviceAddress = getIntent().getStringExtra("DEVICE_ADDRESS");
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
                bluetoothConnectionManager.connectToDevice(device);
            }
        }

        startGameTimer();
    }

    private void startGameTimer() {
        gameTimer = new CountDownTimer(GAME_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time left: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                if (counter < WINNING_SCORE) {
                    endGame(false); // Défaite si le temps est écoulé et le score n'est pas atteint
                }
            }
        };
        gameTimer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            if (acceleration > SHAKE_THRESHOLD) {
                counter++;
                counterTextView.setText("Counter: " + counter);

                if (counter >= WINNING_SCORE) {
                    endGame(true); // Victoire si le score est atteint
                }
            }
        }
    }

    private void endGame(boolean isVictory) {
        gameTimer.cancel();
        sensorManager.unregisterListener(this);

        SharedPreferences preferences = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        int totalVictories = preferences.getInt("TOTAL_VICTORIES", 0);

        if (isVictory) {
            totalVictories++;
        }

        preferences.edit().putInt("TOTAL_VICTORIES", totalVictories).apply();

        boolean isSoloChallenge = getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false);
        boolean isDuoChallenge = getIntent().getBooleanExtra("IS_DUO_CHALLENGE", false);

        if (isSoloChallenge) {
            finish();
        } else if (isDuoChallenge) {
            SharedPreferences prefs = getSharedPreferences("SoloChallenge", Context.MODE_PRIVATE);
            if (isServer) {
                bluetoothConnectionManager.sendScore(counter);
                // Wait for client's score and compare
                bluetoothConnectionManager.receiveScore(clientScore -> {
                    int serverWins = prefs.getInt("TOTAL_VICTORIES_SERVER", 0);
                    int clientWins = prefs.getInt("TOTAL_VICTORIES_CLIENT", 0);
                    if (clientScore < counter) {
                        serverWins = serverWins + 1;
                        prefs.edit().putInt("TOTAL_VICTORIES_SERVER", serverWins).apply();
                    } else {
                        clientWins = clientWins + 1;
                        prefs.edit().putInt("TOTAL_VICTORIES2_CLIENT", clientWins).apply();
                    }
                });
            } else {
                bluetoothConnectionManager.receiveScore(serverScore -> {
                    int serverWins = prefs.getInt("TOTAL_VICTORIES_SERVER", 0);
                    int clientWins = prefs.getInt("TOTAL_VICTORIES_CLIENT", 0);
                    if (serverScore < counter) {
                        clientWins = clientWins + 1;
                        prefs.edit().putInt("TOTAL_VICTORIES_CLIENT", clientWins).apply();
                    } else {
                        serverWins = serverWins + 1;
                        prefs.edit().putInt("TOTAL_VICTORIES_SERVER", serverWins).apply();
                    }
                    bluetoothConnectionManager.sendScore(counter);
                });
            }
            finish();
        } else {
            Intent intent = new Intent(ShakeGame.this, EndActivity.class);
            intent.putExtra("SCORE", counter);
            intent.putExtra("VICTORY", isVictory);
            intent.putExtra("CURRENT_GAME", ShakeGame.class.getName());
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
