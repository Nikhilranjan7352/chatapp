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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private EditText UserEmail,UserPassword;
    private Button LoginButton,PhoneLogin;
    private TextView NeedNewAccountLink;
    private ProgressDialog loading;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        Initialise();
        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();

            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        PhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent=new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(phoneIntent);

            }
        });




        ;
    }

    private void LoginUser() {
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
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {   loading.dismiss();

                                Toast.makeText(LoginActivity.this,"succesful",Toast.LENGTH_SHORT).show();
                                SendUserToMainactivity();

                            }
                            else
                            {   loading.dismiss();
                                String message;
                                message=task.getException().toString();
                                Toast.makeText(LoginActivity.this,"ERROR: "+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void SendUserToRegisterActivity() {

        Intent registerIntent= new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void Initialise() {

        UserEmail=(EditText) findViewById(R.id.login_email);
        UserPassword=(EditText) findViewById(R.id.login_password);
        LoginButton=(Button) findViewById(R.id.login_button);
        NeedNewAccountLink=(TextView) findViewById(R.id.need_new_account);
        loading=new ProgressDialog(this);
        PhoneLogin=(Button) findViewById(R.id.login_phone);

    }



    private void SendUserToMainactivity() {

        Intent mainIntent= new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
