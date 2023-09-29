package com.example.dinacharyaapkdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

// ... (existing imports) ...

public class SignUp extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextFirstName, editTextLastName, editTextMobileNumber;
    Button buttonReg;
    ProgressBar progressBar;
    TextView textView;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // User is signed in, save their information to shared preferences
            SharedPreferences preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userEmail", currentUser.getEmail());
            editor.putString("userFirstName", editTextFirstName.getText().toString());
            editor.putString("userLastName", editTextLastName.getText().toString());
            editor.apply();

            // Redirect to the main activity
            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(intent);
            finish(); // Finish the SignUpActivity so the user cannot go back to it.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextFirstName = findViewById(R.id.firstname);
        editTextLastName = findViewById(R.id.lastname);
        editTextMobileNumber = findViewById(R.id.number);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressbar);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, firstName, lastName, mobileNumber;
                email = Objects.requireNonNull(editTextEmail.getText()).toString();
                password = Objects.requireNonNull(editTextPassword.getText()).toString();
                firstName = Objects.requireNonNull(editTextFirstName.getText()).toString();
                lastName = Objects.requireNonNull(editTextLastName.getText()).toString();
                mobileNumber = Objects.requireNonNull(editTextMobileNumber.getText()).toString();
                progressBar.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                        TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                        TextUtils.isEmpty(mobileNumber)) {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                    Toast.makeText(SignUp.this, "Authentication successful", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    updateUI(null);
                                    Toast.makeText(SignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }


}
