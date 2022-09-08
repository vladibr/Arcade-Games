package com.example.arcade_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WordsGame extends AppCompatActivity implements View.OnClickListener {

    ImageView heart1, heart2, heart3;
    TextView score, word;
    Button exitBtn, seenBtn, newBtn;
    Integer points = 0, lives = 3;
    Random random = new Random();

    DatabaseReference scoresRef;

    List<String> WORDS = Arrays.asList(
        "dog", "cat", "table", "chair", "soda",
        "spider", "port", "chairman","cola", "house",
        "mouse", "phone", "laptop", "computer", "surfboard",
        "watch", "rolex", "car", "toothbrush", "tooth",
        "pen", "pencil", "door", "rabbit", "dragon",
        "country", "dark", "bright", "city", "animal",
        "girl", "boy", "homework", "music", "dirt",
        "school", "bent", "band", "spring", "word"
        ,"linear", "pier" ,"nerve", "skin" ,"sickness"
        ,"policy", "career", "drift", "justify", "count"
        ,"grandmother", "harmful", "discover", "authority", "quit"
        ,"wake", "desert", "favor", "incredible", "mutual"

    );

    List<String> seenWords = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_game);

        initVarbs();

        initViews();

        initButtons();
    }

    private void initVarbs() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        scoresRef = database.getReference("Scores");
    }

    private void initButtons() {

        seenBtn.setOnClickListener(this);
        newBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
    }

    private void initViews() {

        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);

        word = findViewById(R.id.word);
        score = findViewById(R.id.score);

        seenBtn = findViewById(R.id.seen_btn);
        newBtn = findViewById(R.id.new_btn);
        exitBtn = findViewById(R.id.exit_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.new_btn:
                checkIfNewWord(word.getText().toString());
                break;
            case R.id.seen_btn:
                checkIfSeenWord(word.getText().toString());
                break;
            case R.id.exit_btn:
                exit();
                break;
        }
    }

    private void checkIfSeenWord(String seenWord) {
        if(seenWords.contains(seenWord)) {
            points++;
            score.setText(points.toString());
        } else{
            removeHeart();
        }
        word.setText(WORDS.get(random.nextInt(WORDS.size())));
    }

    private void checkIfNewWord(String newWord) {

        if(!seenWords.contains(newWord)){
            points++;
            score.setText(points.toString());
            seenWords.add(newWord);
        } else{
            removeHeart();
        }
        word.setText(WORDS.get(random.nextInt(WORDS.size())));
    }

    private void removeHeart() {
        switch (lives)
        {
            case 3:
                heart3.setImageDrawable(null);
                break;
            case 2:
                heart2.setImageDrawable(null);
                break;
            case 1:
                heart1.setImageDrawable(null);
                Toast.makeText(WordsGame.this, "You Lose", Toast.LENGTH_SHORT).show();
                checkScore();
                startActivity(new Intent(WordsGame.this,WordsGame.class));
                break;
        }
        lives--;
    }

    private void checkScore(){
        /**
         * Work on the Scores table in firebase
         * Gets the current user ID from firebase
         * Check if there is an object with that ID
         * Get the current game score
         * Check if that score is less then the new score if so update it
         */
        scoresRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(snapshot.child(currentUser).exists()){
                    Integer score = snapshot.child(currentUser).child("wordsGame").getValue(Integer.class);
                    if(score < points){
                        scoresRef.child(currentUser).child("wordsGame").setValue(points);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void exit() {

        startActivity(new Intent(WordsGame.this, ArcadeGames.class));
    }
}