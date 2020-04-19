package com.example.chatapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class Groupsfragment extends Fragment {

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList_of_groups=new ArrayList<>();
    private DatabaseReference groupRef;
    private String userc;


    public Groupsfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView= inflater.inflate(R.layout.fragment_groupsfragment, container, false);
        userc= FirebaseAuth.getInstance().getCurrentUser().getUid();

        groupRef= FirebaseDatabase.getInstance().getReference().child("Groups");

        InitialiseFields();

        RetrieveAndDisplayGRoups();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String GroupName=adapterView.getItemAtPosition(i).toString();
                Intent groupIntent= new Intent(getContext(),GroupChatActivity.class);
                groupIntent.putExtra("Group",GroupName);
                startActivity(groupIntent);

            }
        });



        return  groupFragmentView;
    }

    private void RetrieveAndDisplayGRoups()
    {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set=new  HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());

                }
                arrayList_of_groups.clear();
                arrayList_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void InitialiseFields() {


        list_view=(ListView) groupFragmentView.findViewById(R.id.groups_list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayList_of_groups);
        list_view.setAdapter(arrayAdapter);

    }

}