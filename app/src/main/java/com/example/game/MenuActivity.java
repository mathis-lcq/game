package com.example.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private static final List<Class<?>> SENSOR_GAMES = List.of(GyroscopeGame.class, ShakeGame.class);
    private static final List<Class<?>> MOVEMENT_GAMES = List.of(BasketBall.class, PongGame.class);
    private static final List<Class<?>> QUIZ_GAMES = List.of(Quizz.class, NumericQuiz.class);

    private List<Class<?>> selectedGames = new ArrayList<>();
    private int currentGameIndex = 0;
    private int totalVictories = 0;
    private final static int NB_GAMES = 3;
    private boolean isDuoChallenge = false;
    private boolean isSoloChallenge = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // init pref
        SharedPreferences prefs = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        prefs.edit().putInt("CURRENT_INDEX", 0).apply();
        prefs.edit().putInt("TOTAL_VICTORIES", 0).apply();
        prefs.edit().putString("GAMES", "").apply();

        Button playGyroscopeGameButton = findViewById(R.id.playGyroscopeGameButton);
        Button playShakeGameButton = findViewById(R.id.playShakeGameButton);
        Button playPongGameButton = findViewById(R.id.playPongGameButton);
        Button playQuizzButton = findViewById(R.id.playQuizzButton);
        Button playNumericQuizButton = findViewById(R.id.playNumericQuizButton);
        Button playSoccerButton = findViewById(R.id.playSoccerButton);

        Button playSoloChallengeButton = findViewById(R.id.playSoloChallengeButton);
        Button playDuoChallengeButton = findViewById(R.id.playDuoChallengeButton);
        Button findPlayerButton = findViewById(R.id.findPlayerButton);

        playGyroscopeGameButton.setOnClickListener(v -> startGame(GyroscopeGame.class));
        playShakeGameButton.setOnClickListener(v -> startGame(ShakeGame.class));
        playPongGameButton.setOnClickListener(v -> startGame(PongGame.class));
        playQuizzButton.setOnClickListener(v -> startGame(Quizz.class));
        playNumericQuizButton.setOnClickListener(v -> startGame(NumericQuiz.class));
        playSoccerButton.setOnClickListener(v -> startGame(BasketBall.class));

        playSoloChallengeButton.setOnClickListener(v -> startSoloChallenge());
        playDuoChallengeButton.setOnClickListener(v -> startDuoChallenge());
        findPlayerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, FindPlayerActivity.class);
            startActivity(intent);
        });
    }

    private void startSoloChallenge() {
        isSoloChallenge = true;
        isDuoChallenge = false;
        selectedGames.clear();

        // Sélectionner aléatoirement un jeu de chaque catégorie
        selectedGames.add(pickRandomGame(SENSOR_GAMES));
        selectedGames.add(pickRandomGame(MOVEMENT_GAMES));
        selectedGames.add(pickRandomGame(QUIZ_GAMES));

        // Mélanger l'ordre des jeux
        Collections.shuffle(selectedGames);

        SharedPreferences prefs = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        prefs.edit()
                .putInt("CURRENT_INDEX", 0)
                .putInt("TOTAL_VICTORIES", 0)
                .putString("GAMES", serializeGames(selectedGames))
                .apply();

        startNextGame();
    }

    private void startDuoChallenge() {
        isDuoChallenge = true;
        isSoloChallenge = false;
        selectedGames.clear();
        currentGameIndex = 0;
        totalVictories = 0;

        selectedGames.add(pickRandomGame(SENSOR_GAMES));
        selectedGames.add(pickRandomGame(MOVEMENT_GAMES));
        selectedGames.add(pickRandomGame(QUIZ_GAMES));

        Collections.shuffle(selectedGames);

        SharedPreferences prefs = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        prefs.edit()
                .putInt("CURRENT_INDEX", 0)
                .putInt("TOTAL_VICTORIES_SERVER", 0)
                .putInt("TOTAL_VICTORIES_CLIENT", 0)
                .putString("GAMES", serializeGames(selectedGames))
                .apply();

        Log.d("Bluetooth", "Duo: ");

        if (isBluetoothConnected()) {
            Log.d("Bluetooth", "StartGame: ");
            startNextGame();
        } else {
            Log.d("Bluetooth", "StartFindPlayer: ");
            Intent intent = new Intent(MenuActivity.this, FindPlayerActivity.class);
            startActivity(intent);
        }
    }

    private Class<?> pickRandomGame(List<Class<?>> games) {
        return games.get(new java.util.Random().nextInt(games.size()));
    }

    private void startNextGame() {
        if (currentGameIndex < NB_GAMES) {
            Class<?> gameClass = selectedGames.get(currentGameIndex);
            Log.d("res", "++");
            currentGameIndex++;
            SharedPreferences prefs = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
            prefs.edit().putInt("CURRENT_INDEX", currentGameIndex).apply();

            startGame(gameClass);
        }
    }

    private void startGame(Class<?> gameClass) {
        Intent intent = new Intent(this, gameClass);
        intent.putExtra("IS_SOLO_CHALLENGE", isSoloChallenge);
        intent.putExtra("IS_DUO_CHALLENGE", isDuoChallenge);
        startActivity(intent);
    }

    private boolean isBluetoothConnected() {
        // Implémentez la logique pour vérifier si un appareil Bluetooth est connecté
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        currentGameIndex = prefs.getInt("CURRENT_INDEX", 0);
        totalVictories = prefs.getInt("TOTAL_VICTORIES", 0);
        selectedGames = deserializeGames(prefs.getString("GAMES", ""));

        Log.d("res", "onResume: "+ currentGameIndex + ">="  +NB_GAMES);
        if (currentGameIndex > 0 && currentGameIndex < NB_GAMES) {
            Log.d("res", "1: ");
            startNextGame();
        } else if (currentGameIndex == NB_GAMES) {
            Log.d("res", "2: ");
            showEndActivity();
        }
    }

    private void showEndActivity() {
        SharedPreferences preferences = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        int totalVictories = preferences.getInt("TOTAL_VICTORIES", 0);

        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra("TOTAL_VICTORIES", totalVictories);
        intent.putExtra("TOTAL_GAMES", selectedGames.size());

        // Réinitialiser les préférences après utilisation
        preferences.edit().putInt("TOTAL_VICTORIES", 0).apply();

        startActivity(intent);
    }

    public void addVictory() {
        totalVictories++;
    }

    private String serializeGames(List<Class<?>> games) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> cls : games) {
            sb.append(cls.getName()).append(";");
        }
        return sb.toString();
    }

    private List<Class<?>> deserializeGames(String data) {
        List<Class<?>> list = new ArrayList<>();
        if (data == null || data.isEmpty()) return list;
        for (String name : data.split(";")) {
            try {
                list.add(Class.forName(name));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
