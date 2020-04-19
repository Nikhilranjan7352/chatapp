package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Model> contacts;
    FindFriendsAdapter(Context context, ArrayList<Model> contacts)
    {
        this.context=context;
        this.contacts=contacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_model_layout,parent,false);
        return  new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position)
    {

        holder.user.setText(contacts.get(position).getName());
        holder.status.setText(contacts.get(position).getSatus());

        Picasso.get().load(contacts.get(position).getImage()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clickedID = contacts.get(position).getUserId();
                Intent intent=new Intent(context,ProfileActivity.class);
                intent.putExtra("userid",clickedID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder

    {
        ImageView image;
        TextView user,status;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.model_profile_image);
            user=itemView.findViewById(R.id.model_user_name);
            status=itemView.findViewById(R.id.model_user_status);
        }
    }
}
