package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnetoOneChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference myDatabaseref;
    private EditText usermessageEdittext;
    private  String receiverId;
    private String currentuserid;
    private CircleImageView sendmessagebutoonimage;
    private Toolbar myToolbar;
    private TextView contactname,contactlastseen;
    private CircleImageView profileimage;
    private final List<Messages> messageList=new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private String contactid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneto_one_chat);
        contactid=getIntent().getExtras().get("id").toString();
        receiverId=contactid;
        Toast.makeText(this, contactid+"yhi hai haaaa", Toast.LENGTH_SHORT).show();





        Initialiser();
        myDatabaseref=FirebaseDatabase.getInstance().getReference();
        currentuserid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        RetrieveInformation();
        sendmessagebutoonimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmessage();
            }
        });

    }

    private void RetrieveInformation()
    {
        myDatabaseref.child("Users").child(contactid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String cname=dataSnapshot.child("name").getValue().toString();
                String cimage=dataSnapshot.child("images").getValue().toString();
                contactname.setText(cname);
                Picasso.get().load(cimage).into(profileimage);
                Toast.makeText(OnetoOneChatActivity.this, dataSnapshot.child("name").getValue().toString(), Toast.LENGTH_SHORT).show();
                //contactname.setText(dataSnapshot.child("name").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OnetoOneChatActivity.this, "somethimf ent vrirb", Toast.LENGTH_SHORT).show();

            }
        });

    }


//    @SuppressLint({"RestrictedApi", "WrongViewCast"})
    private void Initialiser()
    {
       usermessageEdittext=(EditText) findViewById(R.id.otomessage);
       sendmessagebutoonimage=(CircleImageView) findViewById(R.id.otoosendmessagebutton);


        myToolbar=(Toolbar)findViewById(R.id.otoochat_appbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar=getSupportActionBar();
       // actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarview=layoutInflater.inflate(R.layout.customtoolbar,null);
        actionBar.setCustomView(actionbarview);
        contactname=(TextView)findViewById(R.id.custom_profilename);
        //contactname.setText("vxhschs");
        contactlastseen=(TextView)findViewById(R.id.custom_lastseen);
        profileimage=(CircleImageView)findViewById(R.id.customset_profile_image);
        recyclerView=(RecyclerView) findViewById(R.id.otooRecyclerView);
        messagesAdapter=new MessagesAdapter(messageList);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messagesAdapter);
        recyclerView.smoothScrollToPosition(messagesAdapter.getItemCount());

    }

    @Override
    protected void onStart() {
        super.onStart();


        myDatabaseref.child("Messages").child(currentuserid).child(receiverId)
                .addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages=dataSnapshot.getValue(Messages.class);

                        messageList.add(messages);

                        messagesAdapter.notifyDataSetChanged();
                        recyclerView.setHasFixedSize(true);

                        recyclerView.smoothScrollToPosition(messagesAdapter.getItemCount());
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

    public void sendmessage()
    {
        String messageText=usermessageEdittext.getText().toString();
        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "Write something...", Toast.LENGTH_SHORT).show();
        }
        else

        {
            String messageaSenderRef="Messages/"+currentuserid+"/"+receiverId;
            String messageaReceiverRef="Messages/"+receiverId+"/"+currentuserid;

            DatabaseReference userMessagekeyref=myDatabaseref.child("Messages")
                    .child(currentuserid).child(receiverId).push();
            String messagePushID=userMessagekeyref.getKey();
            Map messageTextBody =new HashMap();
            messageTextBody.put("from",currentuserid);
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageaSenderRef+"/"+messagePushID,messageTextBody);
            messageBodyDetails.put(messageaReceiverRef+"/"+messagePushID,messageTextBody);
            myDatabaseref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(OnetoOneChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(OnetoOneChatActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                    usermessageEdittext.setText("");

                }
            });




        }

    }
}
