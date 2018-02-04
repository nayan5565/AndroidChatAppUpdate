package com.example.dev.chatapplication.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.example.dev.chatapplication.R;
import com.example.dev.chatapplication.model.Friends;
import com.example.dev.chatapplication.tools.StaticConfig;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Dev on 2/4/2018.
 */

public class FriendRequiestActivity extends AppCompatActivity {
    private DatabaseReference mFriendReqDatabase;
    private FirebaseUser mCurrent_user;
    public Bitmap bitmapAvataUser;
    private RecyclerView mFriendsList;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_req);
        mFriendsList = (RecyclerView) findViewById(R.id.recFriend);
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(FriendRequiestActivity.this));
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("user");
        mUsersDatabase.keepSynced(true);
        mFriendReqDatabase.keepSynced(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendRequiestActivity.FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendRequiestActivity.FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendRequiestActivity.FriendsViewHolder.class,
                mFriendReqDatabase


        ) {
            @Override
            protected void populateViewHolder(final FriendRequiestActivity.FriendsViewHolder friendsViewHolder, Friends friends, int i) {

                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(i).getKey();

                mFriendReqDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(list_user_id)) {

                            String req_type = dataSnapshot.child(list_user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userThumb = dataSnapshot.child("avata").getValue().toString();


                                        if (!userThumb.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                                            byte[] decodedString = Base64.decode(userThumb, Base64.DEFAULT);
                                            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        } else {
                                            bitmapAvataUser = null;
                                        }

                                        if (bitmapAvataUser != null) {
                                            friendsViewHolder.setUserImage(bitmapAvataUser, FriendRequiestActivity.this);
                                        }

                                        friendsViewHolder.setName(userName);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);


    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(Bitmap bitmapAvataUser, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            userImageView.setImageBitmap(bitmapAvataUser);
//            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avata).into(userImageView);

        }
    }

}
