package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private RecyclerView myRecyclerview;
    private ArrayList<Model> myusers;
    FirebaseDatabase rootref;
    private FindFriendsAdapter myadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        myusers=new ArrayList<>();




        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren())
                {
                    Model temp=new Model();
                    temp.setName(ds.child("name").getValue(String.class));
                    temp.setImage(ds.child("images").getValue(String.class));
                    temp.setSatus(ds.child("status").getValue(String.class));
                    temp.setUserId(ds.child("UID").getValue(String.class));
                    myusers.add(temp);
                }
                myadapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRecyclerview=(RecyclerView)findViewById(R.id.findfrieds_recyclerview);
        //mRecyclerView.setAdapter(postAdapter);
        //        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));



        myadapter= new FindFriendsAdapter(FindFriendsActivity.this,myusers);
        myRecyclerview.setAdapter(myadapter);
        myRecyclerview.setHasFixedSize(true);
        myRecyclerview.setLayoutManager(new LinearLayoutManager(FindFriendsActivity.this));
    }
}
