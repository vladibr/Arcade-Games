package com.example.arcade_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NumberMemory extends AppCompatActivity implements View.OnClickListener {

    Timer timer;
    TimerTask timerTask;
    Integer time;
    TextView level, number, countdown;
    Button submitBtn, exitBtn;
    EditText inputNumber;
    Integer digits = 10, currentLevel = 1;
    Integer currentNumber = randomNumber(digits/10, digits);

    DatabaseReference scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_memory);

        initVarbs();

        initViews();

        initButtons();

        startGame();

        timer = new Timer();
        startTimer();
    }

    private void initVarbs() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        scoresRef = database.getReference("Scores");
    }

    private void initButtons() {
        exitBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    private void initViews() {
        level = findViewById(R.id.level);
        number = findViewById(R.id.number);
        countdown = findViewById(R.id.timer);

        submitBtn = findViewById(R.id.submit_btn);
        exitBtn = findViewById(R.id.exit_btn);

        inputNumber = findViewById(R.id.input_number);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit_btn:
                checkNumber();
                break;
            case R.id.exit_btn:
                exit();
                break;
        }
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
                    Integer score = snapshot.child(currentUser).child("numberMemory").getValue(Integer.class);
                    if(score < currentLevel){
                        scoresRef.child(currentUser).child("numberMemory").setValue(currentLevel);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startTimer(){
        /**
         Make a timer (counter)
         Period is every 1 sec
         */

        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        level.setText("Level: " + currentLevel);
                        countdown.setText("Timer: " + getTimerText());
                        if(time > 0) {
                            time--;
                        } else{
                            startUserGuess();
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private String getTimerText(){
        /**
         Returns the current time in a string format to the SetText
         **/

        return String.format(String.format("%02d", time));
    }

    private void startGame(){
        /**
         Number and countdown will be Visible
         User input and submit will be Invisible
         reset timer
         **/

        number.setVisibility(View.VISIBLE);
        countdown.setVisibility(View.VISIBLE);
        inputNumber.setVisibility(View.GONE);
        submitBtn.setVisibility(View.GONE);

        number.setText("Number: " + currentNumber);
        inputNumber.setText(null);

        time = 3;
    }

    private void startUserGuess() {
        /**
         Number and countdown will be Invisible
         User input and submit will be visible
         */

        number.setVisibility(View.GONE);
        countdown.setVisibility(View.GONE);
        inputNumber.setVisibility(View.VISIBLE);
        submitBtn.setVisibility(View.VISIBLE);

        number.setText("Number: " + currentNumber);
    }

    private void checkNumber() {
        /**
         Check if user input number is equal to the current level number
         */

        if(Integer.parseInt(inputNumber.getText().toString()) == currentNumber) {
            goToNextStage();
        } else{
            checkScore();
            Toast.makeText(NumberMemory.this, "You Lose", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NumberMemory.this, NumberMemory.class));
        }
    }

    public void goToNextStage(){
        /**
         Increase values for next stage
         startGame again (with increased Values)
         */

        currentLevel++;
        level.setText("Level: " + currentLevel);
        digits *= 10;
        currentNumber = randomNumber(digits/10, digits);
        startGame();
    }

    public int randomNumber(int min, int max) {
        /**
         Returns random number between 2 values (min, max-1)
         */

        Random random = new Random();
        return random.nextInt((max-1) - min) + min;
    }

    private void exit() {

        startActivity(new Intent(NumberMemory.this, ArcadeGames.class));
    }
}