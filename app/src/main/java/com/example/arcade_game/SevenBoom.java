package com.example.arcade_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.media.MediaPlayer;
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

import java.util.Timer;
import java.util.TimerTask;

public class SevenBoom extends AppCompatActivity implements View.OnClickListener {

    Timer timer;
    TimerTask timerTask;
    Integer time = 0, points = 0, lives = 3;
    TextView counter, score;
    ImageView bomb1, bomb2, bomb3;
    Button guessBtn, exitBtn;
    MediaPlayer mediaPlayer;
    DatabaseReference scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven_boom);

        initVarbs();

        initViews();

        initButtons();


        startTimer();
    }

    //TODO:
    // Ask roe how to display image on screen on btn Click
    // save that counter in local storage
    // (we can add on to firebase and save leaderboard) optional

    private void initVarbs() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        scoresRef = database.getReference("Scores");
    }

    private void initButtons() {

        guessBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
    }

    private void initViews(){
        counter = findViewById(R.id.counter);
        score = findViewById(R.id.score);

        bomb1 = findViewById(R.id.bomb1);
        bomb2 = findViewById(R.id.bomb2);
        bomb3 = findViewById(R.id.bomb3);

        guessBtn = findViewById(R.id.btnGuess);
        exitBtn = findViewById(R.id.exit_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnGuess:
                checkGuess();
                break;
            case R.id.exit_btn:
                exit();
                break;
        }
    }

    private void checkGuess() {
        /**
        Checks the user guess
        Correct: increase points
        Incorrect: reduce life
        * */

        if(time % 7 == 0 || time.toString().contains("7")) {
            score.setText("Score: " + ++points);
            if(mediaPlayer == null){
                mediaPlayer = MediaPlayer.create(this, R.raw.boom);
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSound();
                }
            });
            mediaPlayer.start();
        } else{
            reduceLife();
        }
    }

    private void reduceLife(){

        switch (lives)
        {
            case 3:
                bomb3.setImageDrawable(null);
                break;
            case 2:
                bomb2.setImageDrawable(null);
                break;
            case 1:
                bomb1.setImageDrawable(null);
                Toast.makeText(SevenBoom.this, "You Lose", Toast.LENGTH_SHORT).show();
                checkScore();
                startActivity(new Intent(SevenBoom.this, SevenBoom.class));
                break;
        }
        lives--;
    }

    public void startTimer(){
        /**
         Make a timer (counter)
         Period is every 0.5 sec
         * */

        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(() -> {
                    time++;
                    counter.setText("current Number: " + getTimerText());
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 500);
    }

    private String getTimerText(){
        /**
         Returns the current time in a string format to the SetText
         * */

        return String.format(String.format("%02d", time));
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
                    Integer score = snapshot.child(currentUser).child("sevenBoomGame").getValue(Integer.class);
                    if(score < points){
                        scoresRef.child(currentUser).child("sevenBoomGame").setValue(points);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void stopSound() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void exit() {

        startActivity(new Intent(SevenBoom.this, ArcadeGames.class));
    }
}