package com.example.chatapp;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private View RequestFragmentView;
    private RecyclerView requestList;
    private  DatabaseReference  userref;
    private DatabaseReference requestref,contacttref;
    private String currentuserid;
    public RequestFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestFragmentView= inflater.inflate(R.layout.fragment_request, container, false);
        requestList=(RecyclerView) RequestFragmentView.findViewById(R.id.request_list_rview);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));
        requestref= FirebaseDatabase.getInstance().getReference().child("Request");
        contacttref= FirebaseDatabase.getInstance().getReference().child("Contacts");

        userref=FirebaseDatabase.getInstance().getReference().child("Users");
        currentuserid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        return RequestFragmentView;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ContactsRequest>options=
                new FirebaseRecyclerOptions.Builder<ContactsRequest>()
                        .setQuery(requestref.child(currentuserid),ContactsRequest.class)
                        .build();
        FirebaseRecyclerAdapter<ContactsRequest,RequestsViewholder> adapter=
                new FirebaseRecyclerAdapter<ContactsRequest, RequestsViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewholder holder, int position, @NonNull ContactsRequest model) {
                        final String list_userid=getRef(position).getKey();
                        DatabaseReference getTypeRef=getRef(position).child("requesttype").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists())
                                {
                                    String type=dataSnapshot.getValue().toString();
                                    if(type.equals("received"))
                                    {
                                        userref.child(list_userid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.hasChild("images"))

                                                {
                                                    Toast.makeText(getContext(), "hereit comes1", Toast.LENGTH_SHORT).show();
                                                    final String username=dataSnapshot.child("name").getValue().toString();
                                                    final String userimage=dataSnapshot.child("images").getValue().toString();
                                                    final String userstus=dataSnapshot.child("status").getValue().toString();
                                                    holder.username.setText(username);
                                                    Picasso.get().load(userimage).into(holder.userimage);
                                                    holder.accept.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Toast.makeText(getContext(), "its working bro", Toast.LENGTH_SHORT).show();
                                                            addtocontacts(list_userid);
                                                            removefromrequests(list_userid);
                                                        }
                                                    });
                                                    holder.cancel.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            removefromrequests(list_userid);
                                                        }
                                                    });
                                                }
                                                else
                                                {
                                                    final String username=dataSnapshot.child("name").getValue().toString();
                                                    holder.username.setText(username);
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                    else {
                                        holder.mylayout.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "i am here", Toast.LENGTH_SHORT).show();
                                        holder.itemView.setVisibility(View.GONE);
                                    }

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public RequestsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.requestusermodellayout,parent,false);
                        RequestsViewholder holder=new RequestsViewholder(view);
                        return holder;
                    }
                };
        requestList.setAdapter(adapter);
        adapter.startListening();
    }

    private void removefromrequests(String list_userid) {
        final String current_user=currentuserid,useridclikd=list_userid;

        requestref.child(current_user).child(useridclikd).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                requestref.child(useridclikd).child(current_user).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getContext(), "kaamhgya", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }

    private void addtocontacts(String list_userid)
    {
        String current_user=currentuserid,useridclikd=list_userid;

        contacttref.child(current_user).child(useridclikd).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getContext(), "You are freinds now", Toast.LENGTH_SHORT).show();
                }

            }
        });
        contacttref.child(useridclikd).child(current_user).child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getContext(), "You are freinds now", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public static  class RequestsViewholder extends RecyclerView.ViewHolder {
        TextView username,userstatus;
        CircleImageView userimage;
        LinearLayout mylayout;
        Button accept,cancel;
        public RequestsViewholder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.rmodel_user_name);
            userimage=itemView.findViewById(R.id.rmodel_profile_image);
            accept=itemView.findViewById(R.id.accept_button);
            cancel=itemView.findViewById(R.id.cancel_button);
            mylayout=itemView.findViewById(R.id.rusmlayoutlinear);
        }
    }
}
