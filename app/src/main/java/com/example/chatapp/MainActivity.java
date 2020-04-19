package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Toolbar  mtoolbar;
    private ViewPager myviewPager;
    private TabLayout mytabLayout;
    private TabsAccesorAdapter mytabsAccessorAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private DatabaseReference RootRef;
    private String CurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();


        currentUser=mAuth.getCurrentUser();

        RootRef= FirebaseDatabase.getInstance().getReference();
       /* try{

        CurrentUserID=mAuth.getCurrentUser().getUid();
        }
        catch (Exception e){}*/
        mtoolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("WaaApp");
        myviewPager=(ViewPager) findViewById(R.id.main_tabs_pager);
        mytabsAccessorAdapter=new TabsAccesorAdapter(getSupportFragmentManager());
        myviewPager.setAdapter(mytabsAccessorAdapter);
        mytabLayout=(TabLayout) findViewById(R.id.main_tabs);
        mytabLayout.setupWithViewPager(myviewPager);


       /* String receiverName=  RootRef.child("Users").child(CurrentUserID).child("name").toString();
        Toast.makeText(this, "hryyy   "+receiverName, Toast.LENGTH_SHORT).show();*/


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser==null)
        {
            SendUserToLoginactivity();
        }
        else
        {
            updateuserstatus("online");
           VerifyExistence();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentUser!=null)
        {
            updateuserstatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(currentUser!=null)
        {
            updateuserstatus("offline");
        }
    }

    private void VerifyExistence() {
        String CurrentUserID=mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists())
                {
                    Toast.makeText(MainActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUserToSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToLoginactivity() {

        Intent loginIntent= new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
    private void SendUserToSettingsActivity() {

        Intent loginIntent= new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(loginIntent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_options,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_logout_option)
        {
            mAuth.signOut();
            SendUserToLoginactivity();
        }
        if(item.getItemId()==R.id.main_settings)
        {
            SendUserToSettingsActivity();
        }
        if(item.getItemId()==R.id.main_Create_Group)
        {
            CreateNewGroup();
        }
        if(item.getItemId()==R.id.main_find_friends)
        {
            SendUSerTOFIndFRiendsActivty();
        }
        return true;
    }

    private void SendUSerTOFIndFRiendsActivty()
    {
        Intent FriendsIntent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(FriendsIntent);
    }

    private void CreateNewGroup()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);

        builder.setTitle("Enter Group name: ");
        final EditText groupName= new EditText(MainActivity.this);
        groupName.setHint("e.g.  shchjshj");
        builder.setView(groupName);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String Groupnemetext=groupName.getText().toString();
                if(TextUtils.isEmpty(Groupnemetext))
                {
                    Toast.makeText(MainActivity.this, "fill the name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateRequestedGroup(Groupnemetext);

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });
        builder.show();

    }

    private void CreateRequestedGroup(String groupnemetext) {

        RootRef.child("Groups").child(groupnemetext).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "group created", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    private void updateuserstatus(String state)
    {
        String saveCureentTime,saveCureentdate;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCureentdate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCureentTime=currentTime.format(calendar.getTime());
        HashMap<String,Object> onlinestate=new HashMap<>();
        onlinestate.put("date",saveCureentdate);
        onlinestate.put("time",saveCureentTime);
        onlinestate.put("state",state);
        String currentuserid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        RootRef.child("Users").child(currentuserid).child("userstate")
                .updateChildren(onlinestate);

    }
}
