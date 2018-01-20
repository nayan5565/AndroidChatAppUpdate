package com.example.dev.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtMessage;
    private Button btnSend;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseUsers;
    private RecyclerView recMessage;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FloatingActionButton fab;
    public static String name = "";
    public static String userName = "good";
    public static String userName2 = "";

    private ArrayList<String> friends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName2 = Utils.getPref("userName", "well");
        Log.e("oncreate", " name " + userName2);
        friends = new ArrayList<>();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("message");
        recMessage = (RecyclerView) findViewById(R.id.recMessage);
        recMessage.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setReverseLayout(true);
        recMessage.setLayoutManager(linearLayoutManager);


        firebaseAuth = FirebaseAuth.getInstance();

        Log.e("name", name);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
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
                    friends.add(mapRecord.get(key).toString());
                    Log.e("friends", " listKey  " + mapRecord.get(key).toString());
                }
                Log.e("friends", "  is " + friends.get(1) + " size " + friends.size());
                Log.e("friends", " dataSnapshot  one " + dataSnapshot.child("Name").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("message").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("FIREBASE", "added:" + dataSnapshot.child("Name").getValue());

                recMessage.scrollToPosition(recMessage.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("FIREBASE", "changed:" + s);
                recMessage.scrollToPosition(recMessage.getAdapter().getItemCount() - 1);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("FIREBASE", "remmoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e("FIREBASE", "Moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FIREBASE", "onCancelled");
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            currentUser = firebaseAuth.getCurrentUser();
            databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());


            final String messageValue = edtMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(messageValue)) {
                final DatabaseReference newPost = databaseReference.push();
                databaseUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        newPost.child("content").setValue(messageValue);
                        newPost.child("userName").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    userName = dataSnapshot.child("Name").getValue() + "";
                                    if (userName2.equals("well"))
                                        Utils.savePref("userName", userName);
                                    Log.e("userName", userName + " is " + dataSnapshot.child("Name").getValue());
                                    edtMessage.setText("");
                                    recMessage.scrollToPosition(recMessage.getAdapter().getItemCount() - 1);
                                }

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

        }


    }


    private String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy \n hh:mm:ss");
        String day = sdf.format(new Date());
        return day;
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
        final FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class, R.layout.single_message, MessageViewHolder.class, databaseReference
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                userName2 = Utils.getPref("userName", "well");
                Log.e("VH", " data " + userName2 + model.getContent());


                viewHolder.setData(model.getUserName(), getToday(), model.getContent());
            }
        };
        recMessage.setAdapter(firebaseRecyclerAdapter);

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        View view;

        private LinearLayout lnUser, lnChatWith, layout;
        private CardView cardView, cardView2;

        private TextView message, message2, tvDate, tvDate2, tvName, tvName2;


        public MessageViewHolder(View itemView) {
            super(itemView);
            view = itemView;

        }

        public void setContent(String content, String content2) {
            message = (TextView) view.findViewById(R.id.tvMessage);
            message2 = (TextView) view.findViewById(R.id.tvMessage2);
            message.setText(content);
            message2.setText(content2);
        }

        public void setData(String Name, String date, String sms) {
            cardView = (CardView) view.findViewById(R.id.card_view);
            cardView2 = (CardView) view.findViewById(R.id.card_view2);
            lnUser = (LinearLayout) view.findViewById(R.id.lnUser);
            lnChatWith = (LinearLayout) view.findViewById(R.id.lnWithChat);
            message = (TextView) view.findViewById(R.id.tvMessage);
            message2 = (TextView) view.findViewById(R.id.tvMessage2);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvDate2 = (TextView) view.findViewById(R.id.tvDate2);
            tvName = (TextView) view.findViewById(R.id.tvUserName);
            tvName2 = (TextView) view.findViewById(R.id.tvUserName2);
            userName2 = Utils.getPref("userName", "well");
            tvName.setText(Name);

            Log.e("data ", " name " + Name + " " + " " + sms + " " + userName2);
//            if (!Name.equals("null")) {
            if (tvName.getText().toString().equals(userName2)) {
                cardView.setVisibility(View.VISIBLE);
                cardView2.setVisibility(View.GONE);
                message.setText(sms);
                tvName.setText(Name);
                tvDate.setText(date);
                tvDate.setTextColor(Color.RED);
                message2.setText("");
                tvName2.setText("");
                tvDate2.setText("");
                lnChatWith.setBackgroundColor(Color.TRANSPARENT);
//                lnUser.setBackgroundResource(R.drawable.message_shape);


            } else {
                cardView.setVisibility(View.GONE);
                cardView2.setVisibility(View.VISIBLE);
                message2.setText(sms);
                tvName2.setText(Name);
                tvDate2.setText(date);
                message.setText("");
                tvName.setText("");
                tvDate.setText("");
                tvDate2.setTextColor(Color.RED);
                lnUser.setBackgroundColor(Color.TRANSPARENT);
//                lnChatWith.setBackgroundResource(R.drawable.message_shape2);
            }
//            }


        }


        public void setUserName(String userName) {
            TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);

            tvUserName.setText(userName);
            if (userName.equals("nan")) {
                tvUserName.setTextColor(Color.RED);
            } else {
                if (!tvUserName.getText().toString().isEmpty()) {
                    tvUserName.setTextColor(Color.GREEN);
                }
            }
        }

        public void setDate(String date, String date4) {
            TextView date2 = (TextView) view.findViewById(R.id.tvDate);
            TextView date3 = (TextView) view.findViewById(R.id.tvDate2);
            date2.setText(date);
            date3.setText(date4);
        }

        public void addMessageBox(String message, String user, String date, Context context) {

            TextView textView = new TextView(context);
            TextView textView2 = new TextView(context);
            TextView textView3 = new TextView(context);
            textView.setText(message);
            textView2.setText(user);
            textView3.setText(date);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 1.0f;

            if (user.equals("nan")) {
                lp2.gravity = Gravity.RIGHT;
//                textView.setBackgroundResource(R.drawable.bubble);
            } else {
                lp2.gravity = Gravity.LEFT;
//                textView.setBackgroundResource(R.drawable.bubble2);
            }
            textView.setLayoutParams(lp2);
            textView2.setLayoutParams(lp2);
            textView3.setLayoutParams(lp2);
            layout.addView(textView);
            layout.addView(textView2);
            layout.addView(textView3);
//            scrollView.fullScroll(View.FOCUS_DOWN);
        }
    }
}






