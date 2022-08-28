package com.example.announcementsapp;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ActivityLogin extends AppCompatActivity {
    public EditText loginEmailId, logInpasswd;
    Button btnLogIn;
    TextView signup;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    SessionManager session;
    ProgressDialog progressDialog;
    DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        loginEmailId = findViewById(R.id.loginEmail);
        logInpasswd = findViewById(R.id.loginpaswd);
        btnLogIn = findViewById(R.id.btnLogIn);
        signup = findViewById(R.id.TVSignIn);
        progressDialog = new ProgressDialog(this);

        dbManager = new DBManager(this);
        dbManager.open();

        session = new SessionManager(getApplicationContext());

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(ActivityLogin.this, "User logged in ", Toast.LENGTH_SHORT).show();
                    Intent I = new Intent(ActivityLogin.this, MainActivity.class);
                    startActivity(I);
                } else {
                    Toast.makeText(ActivityLogin.this, "Login to continue", Toast.LENGTH_SHORT).show();
                }
            }
        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ActivityLogin.this, ActivitySingup.class);
                startActivity(I);
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Login");
                progressDialog.setMessage("Loading... Please waite");
                progressDialog.show();

                String userEmail = loginEmailId.getText().toString();
                String userPaswd = logInpasswd.getText().toString();
                if (userEmail.isEmpty()) {
                    loginEmailId.setError("Provide your Email first!");
                    loginEmailId.requestFocus();
                } else if (userPaswd.isEmpty()) {
                    logInpasswd.setError("Enter Password!");
                    logInpasswd.requestFocus();
                } else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
                    Toast.makeText(ActivityLogin.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
                    if(Utilis.isInternetConnected(getApplicationContext())) {
                        fireBaseLogin();
                    }else{
                        dbLogin();
                    }
                } else {
                    Toast.makeText(ActivityLogin.this, "Error", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    private void fireBaseLogin() {
        String userEmail = loginEmailId.getText().toString();
        String userPaswd = logInpasswd.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(ActivityLogin.this, "Invalid User", Toast.LENGTH_SHORT).show();
                } else {
                    String userID = firebaseAuth.getCurrentUser().getUid();
                    session.createLoginSession(loginEmailId.getText().toString() , loginEmailId.getText().toString() ,userID);
                    startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                }
            }
        });
    }

    private void dbLogin(){
        String userEmail = loginEmailId.getText().toString();
        String userPaswd = logInpasswd.getText().toString();
        User user = dbManager.login(userEmail, userPaswd);
        if(user != null){

            session.createLoginSession(user.getUserName() , user.getUserEmail() , user.getUserID());

            Intent i =  new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(ActivityLogin.this, "Invalid User", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
