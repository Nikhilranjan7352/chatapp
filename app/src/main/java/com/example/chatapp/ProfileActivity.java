package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private  String useridclikd,current_state,current_user;
    private CircleImageView friendimage;
    private EditText friendname,friendstatus;
    private FirebaseAuth mAuth;
    private Button requestButton,DeclineButton;


    private DatabaseReference dref,requestref,contactsref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        useridclikd= getIntent().getExtras().get("userid").toString();
        Toast.makeText(this, "welcome to profile activity"+useridclikd, Toast.LENGTH_SHORT).show();
        dref=FirebaseDatabase.getInstance().getReference().child("Users");
        requestref=FirebaseDatabase.getInstance().getReference().child("Request");
        contactsref=FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth=FirebaseAuth.getInstance();
        current_user=mAuth.getCurrentUser().getUid();
        initialise();
        DeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelChatRequest();
            }
        });

        RetrieveInformation();
    }

    private void RetrieveInformation()
    {
        dref.child(useridclikd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists() )
                {
                    String userName= dataSnapshot.child("name").getValue().toString();
                    String userStatus= dataSnapshot.child("status").getValue().toString();
                    if(dataSnapshot.child("images").exists())
                    {
                        String imagepath=dataSnapshot.child("images").getValue().toString();
                        friendname.setText(userName);
                        friendstatus.setText(userStatus);
                        Picasso.get().load(imagepath).into(friendimage);
                        ManageFriendRequest();
                    }
                    else {
                        friendname.setText(userName);
                        friendstatus.setText(userStatus);
                        ManageFriendRequest();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void ManageFriendRequest()

    {
        requestref.child(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(useridclikd))
                {
                    String requesttype=dataSnapshot.child(useridclikd).child("requesttype").getValue().toString();
                    if(requesttype.equals("sent"))
                    {
                        current_state="request sent";
                        requestButton.setEnabled(true);
                        requestButton.setText("Cancel chat rquest");


                    }

                    if(requesttype.equals("received"))
                    {
                        current_state="requestrecieved";
                        requestButton.setEnabled(true);
                        requestButton.setText("Accept Request");
                        DeclineButton.setVisibility(View.VISIBLE);


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(!current_user.equals(useridclikd))
        {

            requestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    requestButton.setEnabled(false);
                    if(current_state.equals("new"))
                    {
                        SendChatRequest();
                    }
                    if(current_state.equals("request sent"))
                    {
                        CancelChatRequest();
                    }
                    if(current_state.equals("requestrecieved"))
                    {
                        Addtocontacts();
                        CancelChatRequest();

                    }
                     

                }
            });
        }
        else
        {
            requestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void Addtocontacts()
    {
        contactsref.child(current_user).child(useridclikd).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ProfileActivity.this, "You are freinds now", Toast.LENGTH_SHORT).show();
                }

            }
        });
        contactsref.child(useridclikd).child(current_user).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ProfileActivity.this, "You are freinds now", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void CancelChatRequest()
    {
        requestref.child(current_user).child(useridclikd).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                requestref.child(useridclikd).child(current_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ProfileActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                            requestButton.setEnabled(true);
                            current_state="new";
                            requestButton.setText("send request");
                        }

                    }
                });

            }
        });
    }

    private void SendChatRequest()
    {
        requestref.child(current_user).child(useridclikd).child("requesttype").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    requestref.child(useridclikd).child(current_user).child("requesttype").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                requestButton.setEnabled(true);
                                requestButton.setText("Cancel chat rquest");
                                current_state="request sent";
                                DeclineButton.setEnabled(false);
                            }
                        }
                    });

                }

            }
        });

    }

    private void initialise()
    {
        friendimage=(CircleImageView) findViewById(R.id.friend_profileimage);
        friendname=(EditText) findViewById(R.id.friend_name) ;
        friendstatus=(EditText) findViewById(R.id.friend_status) ;
        requestButton=(Button) findViewById(R.id.friend_sendmessage);
        DeclineButton=(Button) findViewById(R.id.friend_declinerequest);
        current_state="new";

    }
}
