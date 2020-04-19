package com.example.chatapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contactsfragment extends Fragment {
    private ListView myview;


    private View contactview;
    private RecyclerView contactRview;

    private String currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference contactref, userref, currentfriendref;


    public Contactsfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactview = inflater.inflate(R.layout.fragment_contactsfragment, container, false);
        contactRview = (RecyclerView) contactview.findViewById(R.id.list_contact);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contactref = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        contactRview.setLayoutManager(new LinearLayoutManager(getContext()));


        return contactview;


    }
    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<ContactsRequest> options=
                new FirebaseRecyclerOptions.Builder<ContactsRequest>()
                        .setQuery(contactref.child(currentUser),ContactsRequest.class)
                        .build();
        FirebaseRecyclerAdapter<ContactsRequest, ContactViewHolder> adapter =new FirebaseRecyclerAdapter<ContactsRequest, ContactViewHolder>(options){


            @NonNull
            @Override
            public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_model_layout,parent,false);
                ContactViewHolder holder=new ContactViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final ContactViewHolder holder, int position, @NonNull ContactsRequest model) {

                final String contactuserid=getRef(position).getKey();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext(),OnetoOneChatActivity.class);
                        intent.putExtra("id",contactuserid);
                        startActivity(intent);
                    }
                });
                userref.child(contactuserid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("images"))

                        {
                            Toast.makeText(getContext(), "hereit comes1", Toast.LENGTH_SHORT).show();
                            final String username=dataSnapshot.child("name").getValue().toString();
                            final String userimage=dataSnapshot.child("images").getValue().toString();
                            final String userstus=dataSnapshot.child("status").getValue().toString();
                            holder.userStatus.setText(userstus);
                            holder.userName.setText(username);
                            Picasso.get().load(userimage).into(holder.Userimage);

                        }
                        else
                        {
                            final String username=dataSnapshot.child("name").getValue().toString();

                            final String userstus=dataSnapshot.child("status").getValue().toString();

                            holder.userStatus.setText(userstus);
                            holder.userName.setText(username);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        };

        contactRview.setAdapter(adapter);
        adapter.startListening();





    }
    public static class ContactViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userStatus;
        CircleImageView Userimage;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.model_user_name);
            userStatus=itemView.findViewById(R.id.model_user_status);
            Userimage=itemView.findViewById(R.id.model_profile_image);

        }
    }
}

