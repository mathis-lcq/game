package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SoloResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_solo_result);

        TextView scoreTextView = findViewById(R.id.scoreTextView);
        TextView resultTextView = findViewById(R.id.resultTextView);
        Button continueButton = findViewById(R.id.continueButton);

        boolean victory = getIntent().getBooleanExtra("VICTORY", false);
        int totalVictories = getIntent().getIntExtra("TOTAL_VICTORIES", 0);

        resultTextView.setText(victory ? "Victory!" : "Defeat!");
        scoreTextView.setText("Total victories: " + totalVictories);
        continueButton.setOnClickListener(v -> {
            finish();
        });
    }
}