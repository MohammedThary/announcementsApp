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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class announcementDetailActivity extends AppCompatActivity {
    public EditText replyEditTxt;
    public TextView  announcementTitle, announcementDate, announcementBy, announcementBody;

    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SessionManager session;

    String userID;
    String ann_id = "";

    DatabaseReference db;
    ListView reply_list ;
    String annUserId = "";
    private ProgressDialog progressDialog;
    private TextView price, location, email, phone;
    private Button favBtn;
    Announcement announcement = null;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);
        firebaseAuth = FirebaseAuth.getInstance();

        announcementTitle = findViewById(R.id.announcementTitle);
        announcementDate = findViewById(R.id.postDate);
        announcementBy = findViewById(R.id.announcedBy);
        announcementBody = findViewById(R.id.announcementText);
        price = (TextView) findViewById(R.id.an_price);
        location = (TextView) findViewById(R.id.an_location);
        email = (TextView) findViewById(R.id.an_email);
        phone = (TextView) findViewById(R.id.an_phone);

        favBtn = (Button) findViewById(R.id.addFavBtn);
        img = (ImageView) findViewById(R.id.img);

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        userID = session.getUserDetails().get("ID");


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Announcement");
        progressDialog.setMessage("Loading... Please waite");
        progressDialog.show();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                /*if (user != null) {
                    Toast.makeText(announcementDetailActivity.this, "User logged in ", Toast.LENGTH_SHORT).show();
                    session.logoutUser();;
                }

                 */
            }
        };

        Intent in = getIntent();
        Bundle b = in.getExtras();

        if(b!=null && b.containsKey("id")) {
            ann_id = b.getString("id");

        }

        db = FirebaseDatabase.getInstance().getReference();

        String userName = "";
        Query query = db.child("announcements").orderByChild("id").equalTo(ann_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    announcement = postSnapshot.getValue(Announcement.class);
                    announcementTitle.setText(announcement.getTitle());
                    announcementDate.setText(announcement.getAnnouncementDate());
                    annUserId = announcement.getUserID();
                    announcementBody.setText(announcement.getDescription());
                    location.setText(announcement.getLocation());
                    price.setText(announcement.getPrice());

                    if(announcement.getImg() != null) {
                        Picasso.get().load(announcement.getImg()).into(img);
                    }
                    getUser(annUserId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        favBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                db = FirebaseDatabase.getInstance().getReference("favorite");
                String id = db.push().getKey();

                Favorite fav =
                        new Favorite(id, userID, announcement );

                db.child(id).setValue(fav).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("APp", "onSuccess: announcement is added to favorite list ");

                        Intent I=new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(I);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("App", "onFailure: " + e.toString());
                    }
                });
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    User user = null;
    public User getUser(String userID){

        Query query = db.child("users").orderByChild("userID").equalTo(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    user = postSnapshot.getValue(User.class);
                    announcementBy.setText(user.getUserName());
                    email.setText(user.getUserEmail());
                    phone.setText(user.getUserPhone());

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user;
    }


    //-------------------

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
