package com.example.weatherornot;

import static com.example.weatherornot.AuthenticationValidator.passwordIsValid;
import static com.example.weatherornot.AuthenticationValidator.emailIsEmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpPage extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent signUpUser = new Intent(SignUpPage.this, MainActivity.class);
            startActivity(signUpUser);
        }
    }

    public void signUpAccount(View view) {
        EditText emailField = findViewById(R.id.signUpEmailField);
        EditText passwordField = findViewById(R.id.signUpPassword);
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (emailIsEmail(email) && passwordIsValid(password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Intent signUpUser = new Intent(SignUpPage.this, SearchPage.class);
                            startActivity(signUpUser);
                        } else {
                            Toast.makeText(SignUpPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } else {
            Toast.makeText(SignUpPage.this, "Please enter valid email address and password.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}