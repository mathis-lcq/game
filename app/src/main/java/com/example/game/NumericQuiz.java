package com.example.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumericQuiz extends AppCompatActivity {

    private TextView scoreTextView;
    private TextView questionCounterTextView;
    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitButton;

    private int score = 0;
    private int questionCounter = 0;
    private int totalQuestions = 10;
    private static int WINNING_SCORE = 8;

    private List<NumericQuestion> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numeric_quiz);

        scoreTextView = findViewById(R.id.scoreTextView);
        questionCounterTextView = findViewById(R.id.questionCounterTextView);
        questionTextView = findViewById(R.id.questionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        submitButton = findViewById(R.id.submitButton);

        questionList = getQuestionList();
        Collections.shuffle(questionList);

        loadNewQuestion();

        submitButton.setOnClickListener(v -> checkAnswer());
    }

    private void loadNewQuestion() {
        if (questionCounter < totalQuestions) {
            NumericQuestion currentQuestion = questionList.get(questionCounter);
            questionTextView.setText(currentQuestion.getQuestion());
            questionCounterTextView.setText(questionCounter + "/" + totalQuestions);
            answerEditText.setText("");
        } else {
            endGame();
        }
    }

    private void checkAnswer() {
        String userAnswerStr = answerEditText.getText().toString().trim();
        if (userAnswerStr.isEmpty()) {
            Toast.makeText(NumericQuiz.this, "Please enter an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int userAnswer = Integer.parseInt(userAnswerStr);
            NumericQuestion currentQuestion = questionList.get(questionCounter);
            if (userAnswer == currentQuestion.getAnswer()) {
                score++;
                scoreTextView.setText("Score: " + score);
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wrong! The correct answer is: " + currentQuestion.getAnswer(), Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(NumericQuiz.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        questionCounter++;
        loadNewQuestion();
    }

    private void endGame() {
        SharedPreferences preferences = getSharedPreferences("SoloChallenge", MODE_PRIVATE);
        int totalVictories = preferences.getInt("TOTAL_VICTORIES", 0);

        if (score >= WINNING_SCORE) {
            totalVictories++;
        }

        preferences.edit().putInt("TOTAL_VICTORIES", totalVictories).apply();
        //boolean isSoloChallenge = getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false);
        //boolean isDuoChallenge = getIntent().getBooleanExtra("IS_DUO_CHALLENGE", false);

        //if (isSoloChallenge || isDuoChallenge) 
        if (getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false)) {
            Intent intent = new Intent(this, SoloResultActivity.class);
            intent.putExtra("VICTORY", score >= WINNING_SCORE);
            intent.putExtra("TOTAL_VICTORIES", getSharedPreferences("SoloChallenge", MODE_PRIVATE).getInt("TOTAL_VICTORIES", 0));
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(NumericQuiz.this, EndActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("VICTORY", score > WINNING_SCORE);
            intent.putExtra("CURRENT_GAME", NumericQuiz.class.getName());
            startActivity(intent);
            finish();
        }
    }

    private List<NumericQuestion> getQuestionList() {
        List<NumericQuestion> questions = new ArrayList<>();
        questions.add(new NumericQuestion("Quelle est la taille de la tour Eiffel en mètres?", 324));
        questions.add(new NumericQuestion("Combien de jours y a-t-il dans une année bissextile?", 366));
        questions.add(new NumericQuestion("Quel est le nombre d'éléments dans le tableau périodique (en 2023)?", 118));
        questions.add(new NumericQuestion("Combien de minutes y a-t-il dans une journée?", 1440));
        questions.add(new NumericQuestion("Quelle est la température d'ébullition de l'eau en degrés Celsius?", 100));
        questions.add(new NumericQuestion("Combien de continents y a-t-il sur Terre?", 7));
        questions.add(new NumericQuestion("Quel est le nombre de planètes dans notre système solaire?", 8));
        questions.add(new NumericQuestion("Combien de jours y a-t-il en février pendant une année non bissextile?", 28));
        questions.add(new NumericQuestion("Quelle est la vitesse de la lumière en m/s (arrondie)?", 300000000));
        questions.add(new NumericQuestion("Combien d'os composent le squelette humain adulte?", 206));

        return questions;
    }

    private static class NumericQuestion {
        private String question;
        private int answer;

        public NumericQuestion(String question, int answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public int getAnswer() {
            return answer;
        }
    }
}