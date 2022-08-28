package com.example.announcementsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.squareup.picasso.Picasso;
public class announcementListAdapter extends ArrayAdapter<Announcement> {

    private Context mContext;
    private List<Announcement> an_list = new ArrayList<>();
    int pos = 0;

    public announcementListAdapter(Context context, ArrayList<Announcement> list) {
        super(context, 0 , list);
        mContext = context;
        an_list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_announcement,parent,false);

        Announcement current = an_list.get(position);

        TextView title = (TextView) listItem.findViewById(R.id.an_title);
        title.setText(current.getTitle());

        TextView by = (TextView) listItem.findViewById(R.id.an_by);
        TextView price = (TextView) listItem.findViewById(R.id.an_price);
        TextView location = (TextView) listItem.findViewById(R.id.an_location);
        getUser(current.getUserID(), by);

        price.setText(current.getPrice());
        location.setText(current.getLocation());

        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Announcement announcement = an_list.get(position);
                String id = announcement.getId();

                Intent i = new Intent(mContext , announcementDetailActivity.class);
                i.putExtra("id", id);
                mContext.startActivity(i);
            }
        });
        TextView date = (TextView) listItem.findViewById(R.id.an_date);
        date.setText(current.getAnnouncementDate());

        ImageView img = (ImageView) listItem.findViewById(R.id.imageView2);

        if(current.getImg() != null) {
            Picasso.get().load(current.getImg()).into(img);
        }
        return listItem;
    }



    User user = null;
    DatabaseReference db;
    public User getUser(String userID, final TextView ann_By){

        db = FirebaseDatabase.getInstance().getReference();
        Query query = db.child("users").orderByChild("userID").equalTo(userID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    user = postSnapshot.getValue(User.class);
                    ann_By.setText(user.getUserName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user;
    }


}