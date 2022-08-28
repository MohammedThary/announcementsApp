package com.example.announcementsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ActivityProfile extends AppCompatActivity {

    public EditText new_passwd, old_passwd, userName;
    Button btnEdit;
    EditText  phone , age , location;
    String userEmail;
    FirebaseAuth firebaseAuth;
    DatabaseReference db;
    private SessionManager session;
    private ProgressDialog progressDialog;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.UserName);
        new_passwd = findViewById(R.id.new_ETpassword2);
        old_passwd = findViewById(R.id.old_ETpassword);

        phone = findViewById(R.id.ETphone);
        age = findViewById(R.id.ETage);
        location = findViewById(R.id.ETlocation);

        btnEdit = findViewById(R.id.btnEdit);

        dbManager = new DBManager(this);
        dbManager.open();
        session = new SessionManager(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Profile");
        progressDialog.setMessage("Loading... Please waite");
        progressDialog.show();

        User me = getUser(session.getUserDetails().get("ID") , userName);


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Profile");
                progressDialog.setMessage("Update data... Please waite");
                progressDialog.show();

                String username = userName.getText().toString();
                String oldPass = old_passwd.getText().toString();
                String newPass = new_passwd.getText().toString();
                if (username.isEmpty()) {
                    userName.setError("Provide your name first!");
                    userName.requestFocus();
                } else if (oldPass.isEmpty()) {
                    old_passwd.setError("Provide your old password!");
                    old_passwd.requestFocus();
                } else if (newPass.isEmpty()) {
                    new_passwd.setError("Set your new password");
                    new_passwd.requestFocus();
                } else if (username.isEmpty() && oldPass.isEmpty() && newPass.isEmpty()) {
                    Toast.makeText(ActivityProfile.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(username.isEmpty() && oldPass.isEmpty() && newPass.isEmpty())) {
                    changePassword(oldPass, newPass, username);
                } else {
                    Toast.makeText(ActivityProfile.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    User user = null;
    public User getUser(String userID, final EditText txt){

        db = FirebaseDatabase.getInstance().getReference();
        Query query = db.child("users").orderByChild("userID").equalTo(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    user = postSnapshot.getValue(User.class);
                    txt.setText(user.getUserName());
                    phone.setText(user.getUserPhone());
                    age.setText(user.getUserAge());
                    location.setText(user.getUserLocation());
                    userEmail = user.getUserEmail();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user;
    }

    public void updateUserName(String username) {
        db = FirebaseDatabase.getInstance().getReference("users");
        String userID = session.getUserDetails().get("ID");
        String name = userName.getText().toString();
        String ph = phone.getText().toString();
        String loc = location.getText().toString();
        String userAge = age.getText().toString();
        User u = new User(userID, name, userEmail, ph, userAge, loc);
        db.child(userID).setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dbManager.update(u,  new_passwd.getText().toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("APP", "onFailure: " + e.toString());
            }
        });
    }

    private FirebaseUser FBuser;
    public void changePassword(String oldPass , final String newPass, final String username){
        FBuser = FirebaseAuth.getInstance().getCurrentUser();
        final String email = FBuser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

        FBuser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FBuser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                            }else {
                                updateUserName(username);
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "Field: Incorrect passowrd", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.home:
                startActivity(new Intent(getApplicationContext(), listAnnouncementActivity.class));
                return true;
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
                return true;
            case R.id.favorite:
                startActivity(new Intent(getApplicationContext(), ViewFavorite.class));
                return true;
            case R.id.addAnnouncement:
                startActivity(new Intent(getApplicationContext(), addAnnouncementActivity.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                session.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
