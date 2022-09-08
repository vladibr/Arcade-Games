package com.example.arcade_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class ColorsGame extends AppCompatActivity implements View.OnClickListener {

    Integer TIME_DEEP_COLOR = 900, TIME_LIGHT_COLOR = 1000;
    TextView score, level;
    Button startGameBtn, redBtn, yellowBtn, greenBtn, blueBtn, exitBtn, submitBtn;
    Random random = new Random();
    Handler handler = new Handler();
    Handler beforeHandler = new Handler();
    Integer i = 0, currentIndex = 0, points = 0, stage = 1;
    int[] userSeq = new int[stage + 1];
    int[] colorSeq = new int[stage + 1];

    DatabaseReference scoresRef;

    int[] COLORS_ID = new int[]{
            R.id.red_btn,
            R.id.yellow_btn,
            R.id.green_btn,
            R.id.blue_btn
    };

    int[] COLORS = new int[]{
            R.color.red,
            R.color.yellow,
            R.color.green,
            R.color.blue
    };

    int[] LIGHT_COLORS = new int[]{
            R.color.light_red,
            R.color.light_yellow,
            R.color.light_green,
            R.color.light_blue
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colors_game);

        initVarbs();

        initView();

        initButtons();
    }

    private void initVarbs() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        scoresRef = database.getReference("Scores");
    }

    private void initButtons() {

        startGameBtn.setOnClickListener(this);
        redBtn.setOnClickListener(this);
        yellowBtn.setOnClickListener(this);
        greenBtn.setOnClickListener(this);
        blueBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    private void initView() {

        score = findViewById(R.id.score);
        level = findViewById(R.id.level);


        startGameBtn = findViewById(R.id.start_game_btn);
        redBtn = findViewById(R.id.red_btn);
        blueBtn = findViewById(R.id.blue_btn);
        greenBtn = findViewById(R.id.green_btn);
        yellowBtn = findViewById(R.id.yellow_btn);
        submitBtn = findViewById(R.id.submit_btn);
        exitBtn = findViewById(R.id.exit_btn);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.start_game_btn:
                startGameBtn.setVisibility(View.GONE);
                startGame();
                break;
            case R.id.red_btn:
                onUserClick(0);
                break;
            case R.id.yellow_btn:
                onUserClick(1);
                break;
            case R.id.green_btn:
                onUserClick(2);
                break;
            case R.id.blue_btn:
                onUserClick(3);
                break;
            case R.id.submit_btn:
                checkUserSeq();
                break;
            case R.id.exit_btn:
                exit();
                break;
        }
    }

    private Runnable runnable = new Runnable() {
        /**
         * Make background color of each button deeper (Bold)
         * Does it every 0.9 sec
         * Terminate the callBack when sequence is done
         */

        @Override
        public void run() {
            if (i >= colorSeq.length){
                handler.removeCallbacks(runnable);
                return;
            }
            findViewById(COLORS_ID[colorSeq[i]]).setBackgroundResource(COLORS[colorSeq[i]]);
            i++;
            handler.postDelayed(this, TIME_DEEP_COLOR);
        }
    };

    private Runnable beforeRunnable = new Runnable() {
        /**
         * Make background color of the previous button lighter
         * Does it every 1 sec
         * Terminate the callBack when sequence is done
         * Give each button his clickable option back
         */

        @Override
        public void run() {
            findViewById(COLORS_ID[colorSeq[i - 1]]).setBackgroundResource(LIGHT_COLORS[colorSeq[i - 1]]);
            if (i >= colorSeq.length){
                changeClickAble();
                beforeHandler.removeCallbacks(beforeRunnable);
                return;
            }
            beforeHandler.postDelayed(this, TIME_LIGHT_COLOR);
        }
    };

    private void startGame() {

        createColorSeq();
        runnable.run();
        beforeRunnable.run();
    }

    private void createColorSeq() {
        /**
         * Create sequence array of integers (0-3)
         * According the stage
         */

        for (int i = 0; i < colorSeq.length; i++) {
            colorSeq[i] = random.nextInt(COLORS.length);
        }
    }

    private void changeClickAble() {
        redBtn.setClickable(true);
        yellowBtn.setClickable(true);
        greenBtn.setClickable(true);
        blueBtn.setClickable(true);
    }

    private void onUserClick(int id){
        /**
         * Make int array of user clicks on buttons
         */

        if (currentIndex < 6){
            userSeq[currentIndex++] = id;
        }
    }

    private void checkUserSeq(){
        /**
         * Check if userArray == colorSequence
         * If so go to next stage
         * else end the game
         */

        for (int i = 1; i < colorSeq.length; i++) {
            if (colorSeq[i] != userSeq[i - 1]){
                Toast.makeText(ColorsGame.this, "you lose", Toast.LENGTH_SHORT).show();
                checkScore();
                exit();
            }
        }
        goToNextLevel();
    }

    private void goToNextLevel(){
        /**
         * Reset values and update values for next stage
         * Start game again
         */

        points++;
        stage++;
        level.setText("level: " + stage );
        score.setText("Score: " + points);
        userSeq = new int[stage + 1];
        colorSeq = new int[stage + 1];
        i = 0;
        currentIndex = 0;
        startGame();
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
                    Integer score = snapshot.child(currentUser).child("colorsGame").getValue(Integer.class);
                    if(score < points){
                        scoresRef.child(currentUser).child("colorsGame").setValue(points);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void exit(){

        startActivity(new Intent(ColorsGame.this,  ArcadeGames.class));
    }
}


