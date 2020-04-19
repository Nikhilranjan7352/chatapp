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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.EventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chatsfragment extends Fragment {

    private RecyclerView chatrview;
    private View myView;
    private String currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference messageref, userref, currentfriendref;


    public Chatsfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_chatsfragment, container, false);
        chatrview = (RecyclerView) myView.findViewById(R.id.chatrecyclerview);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messageref = FirebaseDatabase.getInstance().getReference().child("Messages");
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        chatrview.setLayoutManager(new LinearLayoutManager(getContext()));




        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<ContactsRequest>options=
                new FirebaseRecyclerOptions.Builder<ContactsRequest>()
                .setQuery(messageref.child(currentUser),ContactsRequest.class)
                        .build();
        FirebaseRecyclerAdapter<ContactsRequest,ChatsViewHolder> adapter =new FirebaseRecyclerAdapter<ContactsRequest,ChatsViewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull ContactsRequest model) {
                final String chatfriendid=getRef(position).getKey();

                userref.child(chatfriendid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("images"))

                        {
                            Toast.makeText(getContext(), "hereit comes1", Toast.LENGTH_SHORT).show();
                            final String username=dataSnapshot.child("name").getValue().toString();
                            final String userimage=dataSnapshot.child("images").getValue().toString();
                            final String userstus=dataSnapshot.child("status").getValue().toString();
                            holder.userName.setText(username);
                            Picasso.get().load(userimage).into(holder.userprofileImage);

                        }
                        else
                        {
                            final String username=dataSnapshot.child("name").getValue().toString();
                            holder.userName.setText(username);
                        }

                        if(dataSnapshot.child("userstate").hasChild("state"))
                        {
                            String date=dataSnapshot.child("userstate").child("date").getValue().toString();
                            String state=dataSnapshot.child("userstate").child("state").getValue().toString();
                            String time=dataSnapshot.child("userstate").child("time").getValue().toString();
                            if(state.equals("online"))
                            {
                                holder.userstatus.setVisibility(View.VISIBLE);
                            }
                            holder.userlasteseen.setText(time);


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext(),OnetoOneChatActivity.class);
                        intent.putExtra("id",chatfriendid);
                        startActivity(intent);
                    }
                });

                currentfriendref=FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUser).child(chatfriendid);
                currentfriendref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            for (DataSnapshot ds:dataSnapshot.getChildren())
                            {
                                Toast.makeText(getContext(), ds.child("message").getValue().toString(), Toast.LENGTH_SHORT).show();
                                holder.userlastmessage.setText(ds.child("message").getValue().toString());

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                Query lastQuery = currentfriendref.orderByKey().limitToLast(1);
//                Toast.makeText(getContext(), chatfriendid, Toast.LENGTH_SHORT).show();
//               lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                   @Override
//                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                       //String messgaelast=dataSnapshot.child("message").getValue().toString();
//                       //Toast.makeText(getContext(), messgaelast, Toast.LENGTH_SHORT).show();
//                   }
//
//                   @Override
//                   public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                   }
//               });



            }
            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_model_layout,parent,false);
                ChatsViewHolder chatsViewHolder=new ChatsViewHolder(view);

                return chatsViewHolder;
            }


        };

        chatrview.setAdapter(adapter);
        adapter.startListening();





    }
    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userlasteseen,userlastmessage;
        CircleImageView userprofileImage,userstatus;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.chatmodel_user_name);
            //userlasteseen=itemView.findViewById(R.id.chatmodel_lastseen);
            userlastmessage=itemView.findViewById(R.id.chatmodel_recentmessage);
            userprofileImage=itemView.findViewById(R.id.chatmodel_profile_image);
            userstatus=itemView.findViewById(R.id.chatmodel_user_onlinestatus);
        }
    }

}
