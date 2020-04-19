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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private EditText Phone_number,OTP_code;
    private Button Send_verification_code,Verify_code;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);



        Phone_number=(EditText) findViewById(R.id.phone_number_text);
        OTP_code=(EditText) findViewById(R.id.enter_otpcode);
        Send_verification_code = (Button) findViewById(R.id.request_code);
        Verify_code=(Button) findViewById(R.id.phone_login_verify_button);
        loadingbar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();


        Send_verification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber=Phone_number.getText().toString();
                if(TextUtils.isEmpty(phoneNumber))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Enter THe phone number", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                        loadingbar.setTitle("number verification");
                        loadingbar.setMessage("ho rha hai verifiaction");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                PhoneLoginActivity.this,               // Activity (for callback binding)
                                callbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java


                    }
            }
        });
        Verify_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                String verificationcodee=OTP_code.getText().toString();
                if(TextUtils.isEmpty(verificationcodee))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    loadingbar.setTitle("Code verification");
                    loadingbar.setMessage("ho rha hai code verifiaction");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationcodee);
                    signInWithPhoneAuthCredential(credential );
                    SendUserToMainActivity();
                }

            }
        });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e)

            {
                loadingbar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                loadingbar.dismiss();
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(PhoneLoginActivity.this, "Verification Code sent", Toast.LENGTH_SHORT).show();


                // ...
            }

        };



    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingbar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "loggen in successful", Toast.LENGTH_SHORT).show();



                        } else
                            {
                                loadingbar.dismiss();
                                Toast.makeText(PhoneLoginActivity.this, "erroe occured", Toast.LENGTH_SHORT).show();

                            }

                    }
                });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent= new Intent(PhoneLoginActivity.this,MainActivity.class);

        startActivity(mainIntent);
        finish();
    }


}

