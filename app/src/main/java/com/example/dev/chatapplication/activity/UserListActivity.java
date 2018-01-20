package com.example.dev.chatapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.dev.chatapplication.adapter.AdUserList;
import com.example.dev.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Dev on 1/20/2018.
 */

public class UserListActivity extends AppCompatActivity {
    private ArrayList<String> userList;
    private RecyclerView recUserList;
    private AdUserList adUserList;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(UserListActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        userList = new ArrayList<>();
        recUserList = (RecyclerView) findViewById(R.id.recUserList);
        adUserList = new AdUserList(this);

        recUserList.setLayoutManager(new LinearLayoutManager(this));


        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() != null) {
//
//                    Friend user = new Friend();
//                    HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
//                    user.name = (String) mapUserInfo.get("Name");
//                    Log.e("friends", " huy  " + user.name);
//                    friends.add(user);
//                }
                HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                Iterator listKey = mapRecord.keySet().iterator();
                while (listKey.hasNext()) {
                    String key = listKey.next().toString();
                    userList.add(mapRecord.get(key).toString());
                    Log.e("friends", " listKey  " + mapRecord.get(key).toString());
                }
                if (userList.size() > 0)
                    adUserList.addData(userList);
                recUserList.setAdapter(adUserList);
//                Log.e("friends", "  is " + userList.get(1) + " size " + userList.size());
                String name = userList.get(0);
                Log.e("friends", " dataSnapshot  one " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
