package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail,UserPassword;
    private Button CreateUserButton;
    private TextView AlreadyhaveAccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    private DatabaseReference Rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Initialise();
        mAuth=FirebaseAuth.getInstance();
        Rootref= FirebaseDatabase.getInstance().getReference();
        AlreadyhaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginactivity();
            }
        });
        CreateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

    }

    private void RegisterUser() {

        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {

            Toast.makeText(this, "gdgd", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loading.setTitle("task process");
            loading.setMessage("ho rha hai bhai");
            loading.setCanceledOnTouchOutside(true);
            loading.show();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())


                            {
                                String currentUserID=mAuth.getCurrentUser().getUid();
                                Rootref.child("Users").child(currentUserID).setValue("");


                                loading.dismiss();

                                Toast.makeText(RegisterActivity.this,"succesful",Toast.LENGTH_SHORT).show();
                                
                                SendUserToMainactivity();

                            }
                            else
                            {   loading.dismiss();
                                String message;
                                message=task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"ERROR: "+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void Initialise() {

        UserEmail=(EditText) findViewById(R.id.register_email);
        UserPassword=(EditText) findViewById(R.id.register_password);
        CreateUserButton=(Button) findViewById(R.id.register_button);
        AlreadyhaveAccountLink=(TextView) findViewById(R.id.already_have_account);
        loading=new ProgressDialog(this);
    }
    private void SendUserToLoginactivity() {

        Intent loginIntent= new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
    private void SendUserToMainactivity() {

        Intent mainIntent= new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
