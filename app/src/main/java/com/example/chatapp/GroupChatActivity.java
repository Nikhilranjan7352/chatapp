package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity
{
    private EditText UserMessage;
    private ImageButton SendImageButton;
    private TextView DisplayGroupMessage;
    private Toolbar mToolbar;
    private ScrollView mScrollView;
    private String CurrentUserID,CurrentUserNAme;
    private FirebaseAuth mAuth;
    private DatabaseReference UserReference,GroupNameRef,Groupmessageref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        String CurrentGroupName=getIntent().getExtras().get("Group").toString();
        mAuth= FirebaseAuth.getInstance();
        UserReference=FirebaseDatabase.getInstance().getReference().child("Users");
        CurrentUserID=mAuth.getCurrentUser().getUid();
        GroupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(CurrentGroupName);

        InitialiseFields(CurrentGroupName);
        getUserInfo();

        SendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveUserMessage();
                UserMessage.setText("");


            }
        });





    }

    private void SaveUserMessage()
    {
        String UserCurrentMessage = UserMessage.getText().toString();
     //   String messageKey=
        if(TextUtils.isEmpty(UserCurrentMessage))
        {
            Toast.makeText(this, "empty mesage can't be send", Toast.LENGTH_SHORT).show();
        }
        else
            {
                String messageKey=GroupNameRef.push().getKey().toString();

                Calendar calForDate=Calendar.getInstance();
                SimpleDateFormat myDate = new SimpleDateFormat();


                HashMap<String,Object> groupMessageKey =new HashMap<>();
                groupMessageKey.put("name",CurrentUserNAme);
                groupMessageKey.put("message",UserCurrentMessage);
                Groupmessageref=GroupNameRef.child(messageKey);
                Groupmessageref.updateChildren(groupMessageKey);









            }
    }

    @Override
    protected void onStart() {
        super.onStart();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayMessages(DataSnapshot dataSnapshot)

    {
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while ((iterator.hasNext()))
        {
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            DisplayGroupMessage.append(chatName +"\n \n "+ chatMessage + "\n \n \n");
        }


    }

    private void getUserInfo()
    {
        UserReference.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {CurrentUserNAme=dataSnapshot.child("name").getValue().toString();}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void InitialiseFields(String currentGroupName) {

        mToolbar=(Toolbar) findViewById(R.id.group_chat_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        UserMessage=(EditText) findViewById(R.id.group_chat_write_message);
        SendImageButton=(ImageButton)findViewById(R.id.group_send_image_button);
        DisplayGroupMessage=(TextView)findViewById(R.id.group_chat_display_text);
        mScrollView=(ScrollView)findViewById(R.id.group_chatmy_scroll_view);


    }
}
