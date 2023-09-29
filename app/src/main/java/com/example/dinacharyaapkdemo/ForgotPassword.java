package com.example.dinacharyaapkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private Button forgotButton;
    private EditText forgotEmail;
    private FirebaseAuth auth;
    private String email;

    private TextView goBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();
        forgotButton = findViewById(R.id.forgotPass);
        forgotEmail = findViewById(R.id.forgotEmail);
        goBackToLogin = findViewById(R.id.goBackToLogin);
        goBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    private void validate(){
         email = forgotEmail.getText().toString();
         if(email.isEmpty()){
             forgotEmail.setError("Required!");
         }else{
             forgotPassword();
         }


    }

    private void forgotPassword() {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Check your email to reset password...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();
                }else{
                    Toast.makeText(ForgotPassword.this, "Error :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}