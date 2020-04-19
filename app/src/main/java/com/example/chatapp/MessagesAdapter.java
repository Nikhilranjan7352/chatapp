package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>
{
    private List<Messages> userMeassageList;
    public MessagesAdapter(List<Messages> userMeassageList)
    {
        this.userMeassageList=userMeassageList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onetoonelayout,parent,false);
        return new MessagesViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position)
    {

        String messagesenderid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Messages messages=userMeassageList.get(position);
        String messagetext=messages.getMessage();
        String fromuserid=messages.getFrom();
        String typeofmessage=messages.getType();
        if(typeofmessage.equals("text"))
        {
            holder.receivermessagetext.setVisibility(View.INVISIBLE);
            holder.sendermessagetext.setVisibility(View.INVISIBLE);
            if(fromuserid.equals(messagesenderid))
            {
                holder.receivermessagetext.setVisibility(View.INVISIBLE);
                holder.sendermessagetext.setVisibility(View.VISIBLE);
                holder.sendermessagetext.setText(messagetext);
            }
            else
            {
                holder.receivermessagetext.setVisibility(View.VISIBLE);
                holder.receivermessagetext.setText(messagetext);
            }
        }


    }

    @Override
    public int getItemCount() {
        return userMeassageList.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        public TextView sendermessagetext,receivermessagetext;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            sendermessagetext=itemView.findViewById(R.id.user_messagetext);
            receivermessagetext=itemView.findViewById(R.id.receiver_messagetext);
        }
    }
}
