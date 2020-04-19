package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button UpdateButton;
    private EditText UserName,USerStatus;
    private CircleImageView UserPic;
    private FirebaseUser CurrentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String   CurrentuserId,userpicstring="";
    private int gallerypic=1;
    private StorageReference UserProfileReference;
    private ProgressDialog myloading;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Initialise();
        mAuth=FirebaseAuth.getInstance();
        myloading=new ProgressDialog(this);
        myloading.setTitle("Setting Profile IMage");
        myloading.setMessage("in progress wait");

        CurrentUser=mAuth.getCurrentUser();
        CurrentuserId=CurrentUser.getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        UserProfileReference= FirebaseStorage.getInstance().getReference().child("Profile Image");
        RetrieveData();
        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });



        UserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "image select", Toast.LENGTH_SHORT).show();
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,gallerypic);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==gallerypic && resultCode == RESULT_OK && data != null)
        {
            Toast.makeText(this, "hhhh", Toast.LENGTH_SHORT).show();
            Uri imageUri= data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);



        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)

            {

                Toast.makeText(this, "gggg", Toast.LENGTH_SHORT).show();
                final Uri profileURI=result.getUri();
                final StorageReference filepath= UserProfileReference.child(CurrentuserId + ".jpeg");
                myloading.show();


                filepath.putFile(profileURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "updated...", Toast.LENGTH_SHORT).show();
                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful())
                                    {
                                        String downloadUri=task.getResult().toString();
                                        userpicstring=downloadUri;
                                        RootRef.child("Users").child(CurrentuserId).child("images").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    myloading.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "firebase me hgya bhai", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    myloading.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "kch gadbadd hgya", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    }
                                    else
                                    {
                                        myloading.dismiss();
                                        Toast.makeText(SettingsActivity.this, "errrorrrr", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "nhi hua ji kch tou gadbadd hai", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }

    private void RetrieveData() {

        RootRef.child("Users").child(CurrentuserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("images") )
                        {
                            String setUsername=dataSnapshot.child("name").getValue().toString();
                            String setUserstatus=dataSnapshot.child("status").getValue().toString();
                            String profileImage=dataSnapshot.child("images").getValue().toString();
                            Log.i("onDataChange: ",profileImage);
                            userpicstring=profileImage;
                            UserName.setText(setUsername);
                            USerStatus.setText(setUserstatus);
                            Picasso.get().load(profileImage).into(UserPic);



                        }
                       else if(dataSnapshot.exists() && dataSnapshot.hasChild("name")  )
                        {
                            String setUsername=dataSnapshot.child("name").getValue().toString();
                            String setUserstatus=dataSnapshot.child("status").getValue().toString();
                            UserName.setText(setUsername);
                            USerStatus.setText(setUserstatus);

                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this, "update your data.....", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void UpdateSettings() {
        String getUsername,getStatus,getdp,getpicstring;
        getUsername=UserName.getText().toString();
        getStatus = USerStatus.getText().toString();
        getpicstring=userpicstring;


        if(TextUtils.isEmpty(getUsername) || TextUtils.isEmpty(getStatus))
        {
            Toast.makeText(this, "fill it", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,String> UserProfileMap =new HashMap<>();
            String CurrentUserID=mAuth.getCurrentUser().getUid();
            UserProfileMap.put("UID",CurrentUserID);
            UserProfileMap.put("name",getUsername);
            UserProfileMap.put("status",getStatus);
            UserProfileMap.put("images",userpicstring);
            RootRef.child("Users").child(CurrentUserID).setValue(UserProfileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {   SendUserToMainactivity();
                                Toast.makeText(SettingsActivity.this, "Update hgya ji", Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                String ErrorMessage=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error:  "+ ErrorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void Initialise() {
        UpdateButton=(Button) findViewById(R.id.Update_button);
        UserName=(EditText) findViewById(R.id.set_username);
        USerStatus=(EditText)findViewById(R.id.set_status);
        UserPic=(CircleImageView) findViewById(R.id.set_profile_image);
    }

    private void SendUserToMainactivity() {

        Intent mainIntent= new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
