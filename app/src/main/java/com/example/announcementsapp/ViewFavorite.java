package com.example.announcementsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewFavorite extends AppCompatActivity {
    public EditText topicTitle, topicText;
    Button btnAddTopic;
    TextView signup;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SessionManager session;
    String userID;
    ArrayList announcement = new <Announcement>ArrayList();
    DatabaseReference db;
    ListView announcement_list ;
    private ProgressDialog progressDialog;

    DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_favorite_layout);
        firebaseAuth = FirebaseAuth.getInstance();

        topicTitle = findViewById(R.id.announcementTitle);
        topicText = findViewById(R.id.announcementText);
        btnAddTopic = findViewById(R.id.btnAdd);

        dbManager = new DBManager(this);
        dbManager.open();

        announcement_list = findViewById(R.id.topics_list);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        userID = session.getUserDetails().get("ID");


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Announcements list");
        progressDialog.setMessage("Loading... Please waite");
        progressDialog.show();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

            }
        };



        if(Utilis.isInternetConnected(getApplicationContext())) {
            db = FirebaseDatabase.getInstance().getReference();
            Query query = db.child("favorite").orderByChild("userID").equalTo(userID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    announcement.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Favorite fav = postSnapshot.getValue(Favorite.class);
                        announcement.add(fav.getAnnouncement());
                    }
                    announcementListAdapter topicAdapter = new announcementListAdapter(ViewFavorite.this, announcement);
                    announcement_list.setAdapter(topicAdapter);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            announcement = dbManager.getAnnouncements(session.getUserDetails().get("ID"));
            announcementListAdapter topicAdapter = new announcementListAdapter(ViewFavorite.this, announcement);
            announcement_list.setAdapter(topicAdapter);
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
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
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                session.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
