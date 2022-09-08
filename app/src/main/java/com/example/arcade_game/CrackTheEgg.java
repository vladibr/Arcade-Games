package com.example.arcade_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class CrackTheEgg extends AppCompatActivity implements View.OnClickListener{

    Button exitBtn;
    TextView score;
    ImageView egg;
    Integer counter = 0, index = 0;
    Integer POINTS_TO_REACH = 1000;

    int[] EGG_STAGES = new int[]{

            R.drawable.egg1,
            R.drawable.egg2,
            R.drawable.egg3,
            R.drawable.egg4,
            R.drawable.cracked
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crack_the_egg);

        initViews();

        initButtons();

        egg.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    counter++;
                    score.setText(""+counter);
                    if(counter % POINTS_TO_REACH == 0 && index < EGG_STAGES.length - 1){
                        egg.setImageResource(EGG_STAGES[++index]);
                    }
                }
                return false;
            }
        });
    }

    private void initViews() {

        exitBtn = findViewById(R.id.exit_btn);

        score = findViewById(R.id.score);

        egg = findViewById(R.id.egg);
    }

    private void initButtons() {

        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.exit_btn:
                exit();
                break;
        }
    }

    private void exit() {

        startActivity(new Intent(CrackTheEgg.this, ArcadeGames.class));
    }
}