package com.example.arcade_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;



import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class LeaderBoard extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    Button fingerMe, sevenBoom, colorsGame, numberMemory, wordsGame, exit;
    ArrayList<String> scoreBoardList = new ArrayList<>();
    Integer index = 0;
    String currentGame = "colorsGame";

    int[] BUTTONS = new int[]{
            R.id.finger_me_btn,
            R.id.seven_boom_btn,
            R.id.colors_game_btn,
            R.id.number_memory_btn,
            R.id.word_game_btn
    };

    DatabaseReference usersRef, scoresRef, myRef;
    ValueEventListener eventListenerUsernames, eventListenerScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        initViews();

        initButtons();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        usersRef = database.getReference("Users");
        scoresRef = database.getReference("Scores");

        eventListenerUsernames = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String username = ds.child("username").getValue(String.class);

                    scoreBoardList.add(username);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };

        eventListenerScores = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Integer score = ds.child(currentGame).getValue(Integer.class);

                    scoreBoardList.set(index, scoreBoardList.get(index) + "   " + score.toString());
                    index++;

                }
                scoreBoardList.sort(Comparator.comparing(a -> a.substring(a.length() - 4)));
                Collections.reverse(scoreBoardList);

                ArrayAdapter<String> adapter = new ArrayAdapter(LeaderBoard.this, android.R.layout.simple_list_item_1, scoreBoardList);

                listView.setAdapter(adapter);
                index = 0;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };

        usersRef.addListenerForSingleValueEvent(eventListenerUsernames);
        scoresRef.addListenerForSingleValueEvent(eventListenerScores);
    }

    private void initButtons() {
        fingerMe.setOnClickListener(this);
        sevenBoom.setOnClickListener(this);
        colorsGame.setOnClickListener(this);
        numberMemory.setOnClickListener(this);
        wordsGame.setOnClickListener(this);
        exit.setOnClickListener(this);

    }

    private void initViews() {

        listView = findViewById(R.id.list_view);

        fingerMe = findViewById(R.id.finger_me_btn);
        sevenBoom = findViewById(R.id.seven_boom_btn);
        colorsGame = findViewById(R.id.colors_game_btn);
        numberMemory = findViewById(R.id.number_memory_btn);
        wordsGame = findViewById(R.id.word_game_btn);
        exit = findViewById(R.id.exit_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.colors_game_btn:
                showLeaderBoard("colorsGame", R.id.colors_game_btn);
                break;
            case R.id.finger_me_btn:
                showLeaderBoard("fingerMeGame", R.id.finger_me_btn);
                break;
            case R.id.seven_boom_btn:
                showLeaderBoard("sevenBoomGame", R.id.seven_boom_btn);
                break;
            case R.id.number_memory_btn:
                showLeaderBoard("numberMemory", R.id.number_memory_btn);
                break;
            case R.id.word_game_btn:
                showLeaderBoard("wordsGame", R.id.word_game_btn);
                break;
            case R.id.exit_btn:
                exit();
                break;
        }
    }

    private void showLeaderBoard(String game, int id){
        if (currentGame != game){
            findViewById(id).setBackgroundColor(Color.parseColor("#FF9E38"));
            for(int i = 0; i<BUTTONS.length; i++){
                if(BUTTONS[i] != id){
                    findViewById(BUTTONS[i]).setBackgroundResource(R.color.light_blue);
                }
            }
            scoreBoardList = new ArrayList<>();
            currentGame = game;
            usersRef.addListenerForSingleValueEvent(eventListenerUsernames);
            scoresRef.addListenerForSingleValueEvent(eventListenerScores);
        }
    }

    private void exit(){
        startActivity(new Intent(LeaderBoard.this, ArcadeGames.class));
    }

}