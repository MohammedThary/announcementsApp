package com.example.announcementsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public EditText emailId, passwd;
    Button btnSignUp;
    TextView signIn;
    FirebaseAuth firebaseAuth;
    Button btnLogOut;

    private SessionManager session;
    private  String userID= "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        userID = session.getUserDetails().get("ID");




        btnLogOut = (Button)findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                session.logoutUser();

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
