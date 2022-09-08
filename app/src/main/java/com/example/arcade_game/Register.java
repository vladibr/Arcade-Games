package com.example.arcade_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText username, email, pass, rePass;
    Button submitBtn, exitBtn;
    GamesScore currentScores = null;
    FirebaseAuth mAuth;
    DatabaseReference myRef, scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        initVarbs();

        initButtons();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        scoresRef = database.getReference("Scores");
    }

    private void initVarbs() {

        mAuth = FirebaseAuth.getInstance();
    }

    private void initButtons() {

        submitBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
    }

    private void initView() {

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        rePass = findViewById(R.id.re_password);

        submitBtn = findViewById(R.id.submit_btn);
        exitBtn = findViewById(R.id.exit_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_btn:
                createUser();
                break;
            case R.id.exit_btn:
                exit();
                break;
        }
    }

    private void createUser() {

        String usernameStr, emailStr, passwordStr, rePassStr;

        usernameStr = username.getText().toString();
        emailStr = email.getText().toString();
        passwordStr = pass.getText().toString();
        rePassStr = rePass.getText().toString();

        User user = new User(username.getText().toString(), email.getText().toString(), pass.getText().toString());
        GamesScore scores = new GamesScore(null, null ,null ,null ,null);

        if(usernameStr.isEmpty()
                || emailStr.isEmpty()
                || passwordStr.isEmpty()
                || rePassStr.isEmpty()){
            Toast.makeText(Register.this, "one or more fields are empty!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordStr.equals(rePassStr)){
            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
ס                                // Sign in success, update UI with the signed-in user's information
                                startActivity(new Intent(Register.this, MainActivity.class));

                                scoresRef.addValueEventListener(new ValueEventListener() {
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        scoresRef.child(currentUser).setValue(scores);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Register.this," Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(Register.this, "Password doesn't match!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void exit() {

        startActivity(new Intent(Register.this, MainActivity.class));
    }
}