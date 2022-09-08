package com.example.arcade_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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

public class FingerMe extends AppCompatActivity implements View.OnClickListener {

    Handler handler = new Handler();
    Random random = new Random();
    ConstraintLayout myLayout;
    TextView timeElapsed, message;
    Button exitBtn;
    Timer timer;
    TimerTask timerTask;
    double time = 0;
    Integer randomTime, index = 0;
    Integer SECONDS = 60;

    DatabaseReference scoresRef;

    int[] drawableImages = new int[]{
            R.drawable.david,
            R.drawable.avshalom,
            R.drawable.chai,
            R.drawable.vladi,
            R.drawable.nvidia,
            R.drawable.doll
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_me);

        initVarbs();

        initViews();

        initButtons();

        randomTime = random.nextInt((SECONDS - 10 + 1) + 10 );

        myLayout = (ConstraintLayout) findViewById(R.id.main_layout);

        myLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Integer evenType = event.getActionMasked();

                switch (evenType){
                    case MotionEvent.ACTION_DOWN:
                        timer = new Timer();
                        startTimer();
                        break;
                    case MotionEvent.ACTION_UP:
                        Toast.makeText(FingerMe.this, "You Lose", Toast.LENGTH_SHORT).show();
                        checkScore();
                        startActivity(new Intent(FingerMe.this, FingerMe.class));
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exit_btn:
                exit();
                break;
        }
    }

    private void initVarbs() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        scoresRef = database.getReference("Scores");
    }

    private void initButtons() {

        exitBtn.setOnClickListener(this);
    }

    private void initViews() {
        timeElapsed = findViewById(R.id.time_elapsed);
        message = findViewById(R.id.message_text);

        exitBtn = findViewById(R.id.exit_btn);
    }

    private void randomEvent(){
        /**
         * Change background to image for 3 sec
         * Then change it back to default
         */

        myLayout.setBackgroundResource(drawableImages[index++]);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myLayout.setBackgroundColor(Color.parseColor("#FF2079"));
                ;
            }
        },3000);
    }

    private void startTimer() {
        /**
         * Show picture at a random time (0-60) sec
         */

        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        timeElapsed.setText(getTimerText());
                        if(time == randomTime){
                            randomEvent();
                            randomTime = random.nextInt(SECONDS);
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private String getTimerText(){
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) +
                ":" + String.format("%02d", minutes) +
                ":" + String.format("%02d", seconds) ;
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
                    Integer score = snapshot.child(currentUser).child("fingerMeGame").getValue(Integer.class);
                    if(score < time){
                        scoresRef.child(currentUser).child("fingerMeGame").setValue(time);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void exit(){

        startActivity(new Intent(FingerMe.this, ArcadeGames.class));
    }
}