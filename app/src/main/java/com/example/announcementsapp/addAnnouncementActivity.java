package com.example.announcementsapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class addAnnouncementActivity extends AppCompatActivity {
    public EditText ann_Title, ann_Text, location ,  price;
    Button btnAddann_;
    TextView signup;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SessionManager session;
    String userID;
    DBManager dbManager;

    private Button storageBtn;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    private static final int STORAGE_PERMISSION_CODE = 101;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    boolean isGranted = false;

    DatabaseReference db;
    private String file_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);
        firebaseAuth = FirebaseAuth.getInstance();


        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ann_Title = findViewById(R.id.announcementTitle);
        ann_Text = findViewById(R.id.announcementText);
        price = findViewById(R.id.price);
        location = findViewById(R.id.location);
        btnAddann_ = findViewById(R.id.btnAdd);

        dbManager = new DBManager(this);
        dbManager.open();

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        userID = session.getUserDetails().get("ID");

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                /*
                if (user != null) {
                    Toast.makeText(addann_Activity.this, "User logged in ", Toast.LENGTH_SHORT).show();
                    Intent I = new Intent(addann_Activity.this, UserActivity.class);
                    startActivity(I);
                } else {
                    Toast.makeText(addann_Activity.this, "Login to continue", Toast.LENGTH_SHORT).show();
                }
                 */
            }
        };

        btnAddann_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = ann_Title.getText().toString();
                String ann_ = ann_Text.getText().toString();

                if (title.isEmpty()) {
                    ann_Title.setError("Title is empty!");
                    ann_Title.requestFocus();
                } else if (ann_.isEmpty()) {
                    ann_Text.setError("body is empty");
                    ann_Text.requestFocus();
                }else if (file_url.isEmpty()) {
                    Toast.makeText(addAnnouncementActivity.this, "Image not selected", Toast.LENGTH_LONG).show();
                } else if (!(title.isEmpty() && ann_.isEmpty() && file_url.isEmpty())) {

                    db = FirebaseDatabase.getInstance().getReference("announcements");
                    String id = db.push().getKey();

                    Announcement announcementOb =
                            new Announcement(id, userID, ann_Title.getText().toString(),
                                    ann_Text.getText().toString(),
                                    price.getText().toString(), location.getText().toString(), file_url);

                    db.child(id).setValue(announcementOb).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dbManager.addAnnouncement(announcementOb);
                            Log.d("APp", "onSuccess: announcement is published successfully ");

                            Intent I=new Intent(addAnnouncementActivity.this,MainActivity.class);
                            startActivity(I);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("App", "onFailure: " + e.toString());
                        }
                    });
                }
            }
        });



        storageBtn = findViewById(R.id.storage);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        // Set Buttons on Click Listeners
        storageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loadImagefromGallery();
            }
        });

    }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(addAnnouncementActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(addAnnouncementActivity.this, new String[] { permission }, requestCode);
        }
        else {
            //Toast.makeText(addAnnouncementActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            isGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(addAnnouncementActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                isGranted = true;
            }
            else {
                Toast.makeText(addAnnouncementActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        if(isGranted) {
            // Defining Implicit Intent to mobile gallery
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(
                            intent,
                            "Select Image from here..."),
                    PICK_IMAGE_REQUEST);
        }
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();

            uploadImage();
        }
    }

    // UploadImage method
    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            file_url = "images/"
                    + UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference
                    .child(file_url);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                  @Override
                                                                                  public void onSuccess(Uri uri) {
                                                                                      Uri downloadUrl = uri;
                                                                                      file_url = downloadUrl.toString();
                                                                                  }
                                                                              });
                                    Toast.makeText(addAnnouncementActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                    storageBtn.setText("Image Uploaded");
                                    storageBtn.setEnabled(false);

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(addAnnouncementActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
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
