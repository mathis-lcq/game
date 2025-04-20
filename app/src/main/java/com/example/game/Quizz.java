package com.example.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Quizz extends AppCompatActivity {

    private TextView scoreTextView;
    private TextView questionCounterTextView;
    private TextView questionTextView;
    private Button answerButton1;
    private Button answerButton2;
    private Button answerButton3;

    private int score = 0;
    private int questionCounter = 0;
    private int totalQuestions = 10;

    private List<Question> questionList;
    private static int WINNING_SCORE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizz);

        scoreTextView = findViewById(R.id.scoreTextView);
        questionCounterTextView = findViewById(R.id.questionCounterTextView);
        questionTextView = findViewById(R.id.questionTextView);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);

        questionList = getQuestionList();
        Collections.shuffle(questionList);

        loadNewQuestion();
    }

    private void loadNewQuestion() {
        if (questionCounter < totalQuestions) {
            Question currentQuestion = questionList.get(questionCounter);
            questionTextView.setText(currentQuestion.getQuestion());
            answerButton1.setText(currentQuestion.getOption1());
            answerButton2.setText(currentQuestion.getOption2());
            answerButton3.setText(currentQuestion.getOption3());

            questionCounterTextView.setText(questionCounter + "/" + totalQuestions);

            answerButton1.setOnClickListener(v -> checkAnswer(currentQuestion.getOption1(), currentQuestion.getAnswer()));
            answerButton2.setOnClickListener(v -> checkAnswer(currentQuestion.getOption2(), currentQuestion.getAnswer()));
            answerButton3.setOnClickListener(v -> checkAnswer(currentQuestion.getOption3(), currentQuestion.getAnswer()));
        } else {
            endGame();
        }
    }

    private void checkAnswer(String selectedAnswer, String correctAnswer) {
        if (selectedAnswer.equals(correctAnswer)) {
            score++;
            scoreTextView.setText("Score: " + score);
            Toast.makeText(Quizz.this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Quizz.this, "Wrong!", Toast.LENGTH_SHORT).show();
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

        if (getIntent().getBooleanExtra("IS_SOLO_CHALLENGE", false)) {
            finish();
        } else {
            Intent intent = new Intent(Quizz.this, EndActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("VICTORY", score > WINNING_SCORE);
            intent.putExtra("CURRENT_GAME", Quizz.class.getName());
            startActivity(intent);
            finish();
        }
    }

    private List<Question> getQuestionList() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("Quelle est la capitale de la France?", "Paris", "Londres", "Berlin", "Paris"));
        questions.add(new Question("Quel est le plus grand océan?", "Atlantique", "Indien", "Pacifique", "Pacifique"));
        questions.add(new Question("Quel est le plus grand pays du monde?", "Russie", "Canada", "Chine", "Russie"));
        questions.add(new Question("Quel est le symbole chimique de l'eau?", "H2O", "O2", "CO2", "H2O"));
        questions.add(new Question("Qui a peint la Joconde?", "Léonard de Vinci", "Pablo Picasso", "Vincent van Gogh", "Léonard de Vinci"));
        questions.add(new Question("Quelle planète est connue comme la planète rouge?", "Mars", "Jupiter", "Vénus", "Mars"));
        questions.add(new Question("Quel est le plus grand mammifère du monde?", "Baleine bleue", "Éléphant", "Girafe", "Baleine bleue"));
        questions.add(new Question("Quel est le plus petit pays du monde?", "Vatican", "Monaco", "Saint-Marin", "Vatican"));
        questions.add(new Question("Quel est le plus long fleuve du monde?", "Nil", "Amazone", "Yangtsé", "Nil"));
        questions.add(new Question("Quel est l'élément le plus abondant dans l'univers?", "Hydrogène", "Hélium", "Oxygène", "Hydrogène"));
        questions.add(new Question("Quel est le plus haut sommet du monde?", "Everest", "K2", "Kilimandjaro", "Everest"));
        questions.add(new Question("Quel est le plus grand désert du monde?", "Sahara", "Antarctique", "Gobi", "Antarctique"));
        questions.add(new Question("Quel est le plus grand animal terrestre?", "Éléphant", "Rhinocéros", "Hippopotame", "Éléphant"));
        questions.add(new Question("Quel est le plus petit continent?", "Australie", "Europe", "Afrique", "Australie"));
        questions.add(new Question("Quel est le plus grand lac du monde?", "Mer Caspienne", "Lac Supérieur", "Lac Victoria", "Mer Caspienne"));
        questions.add(new Question("Quel est le plus grand pays d'Amérique du Sud?", "Brésil", "Argentine", "Colombie", "Brésil"));
        questions.add(new Question("Quel est le plus grand pays d'Afrique?", "Algérie", "Soudan", "RDC", "Algérie"));
        questions.add(new Question("Quel est le plus grand pays d'Europe?", "Russie", "Ukraine", "France", "Russie"));
        questions.add(new Question("Quel est le plus grand pays d'Asie?", "Russie", "Chine", "Inde", "Russie"));
        questions.add(new Question("Quel est le plus grand pays d'Océanie?", "Australie", "Papouasie-Nouvelle-Guinée", "Nouvelle-Zélande", "Australie"));

        return questions;
    }

    private static class Question {
        private String question;
        private String option1;
        private String option2;
        private String option3;
        private String answer;

        public Question(String question, String option1, String option2, String option3, String answer) {
            this.question = question;
            this.option1 = option1;
            this.option2 = option2;
            this.option3 = option3;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getOption1() {
            return option1;
        }

        public String getOption2() {
            return option2;
        }

        public String getOption3() {
            return option3;
        }

        public String getAnswer() {
            return answer;
        }
    }
}
