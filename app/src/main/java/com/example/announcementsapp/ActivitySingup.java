package com.example.announcementsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySingup extends AppCompatActivity {

    public EditText emailId, passwd;
    Button btnSignUp;
    TextView signIn;
    FirebaseAuth firebaseAuth;
    DatabaseReference db;
    private SessionManager session;
    EditText userName,phone , age , location;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.UserName);
        emailId = findViewById(R.id.ETemail);
        passwd = findViewById(R.id.old_ETpassword);
        phone = findViewById(R.id.ETphone);
        age = findViewById(R.id.ETage);
        location = findViewById(R.id.ETlocation);
        btnSignUp = findViewById(R.id.btnSignUp);
        signIn = findViewById(R.id.TVSignIn);

        dbManager = new DBManager(this);
        dbManager.open();
        session = new SessionManager(getApplicationContext());

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = userName.getText().toString();
                String emailID = emailId.getText().toString();
                String paswd = passwd.getText().toString();
                if (user_name.isEmpty()) {
                    userName.setError("Provide your name first!");
                    userName.requestFocus();
                } else if (emailID.isEmpty()) {
                    emailId.setError("Provide your Email first!");
                    emailId.requestFocus();
                } else if (paswd.isEmpty()) {
                    passwd.setError("Set your password");
                    passwd.requestFocus();
                } else if (emailID.isEmpty() && paswd.isEmpty()) {
                    Toast.makeText(ActivitySingup.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(ActivitySingup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(ActivitySingup.this.getApplicationContext(),
                                        "SignUp unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                db = FirebaseDatabase.getInstance().getReference("users");
                                String userID = firebaseAuth.getCurrentUser().getUid();
                                session.createLoginSession(emailId.getText().toString() , emailId.getText().toString() ,userID);
                                String e = emailId.getText().toString();
                                User user = new User(userID, userName.getText().toString() ,e,
                                        phone.getText().toString(),
                                        age.getText().toString(),
                                        location.getText().toString());

                                db.child(userID).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dbManager.register(user, passwd.getText().toString());
                                        Log.d("APP", "onSuccess: user Profile is created for "+ emailId.getText().toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("APP", "onFailure: " + e.toString());
                                    }
                                });

                                startActivity(new Intent(ActivitySingup.this, MainActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(ActivitySingup.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ActivitySingup.this, ActivityLogin.class);
                startActivity(I);
            }
        });
    }

}
