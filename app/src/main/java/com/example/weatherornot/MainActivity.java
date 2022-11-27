package com.example.weatherornot;

import static com.example.weatherornot.AuthenticationValidator.emailIsEmail;
import static com.example.weatherornot.AuthenticationValidator.passwordIsValid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent loginUser = new Intent(MainActivity.this, SearchPage.class);
            startActivity(loginUser);
        }
    }
    public void continueAsGuest(View view) {
        Intent continueAsGuest = new Intent(MainActivity.this, SearchPage.class);
        startActivity(continueAsGuest);
    }

    public void loadSignUpPage(View view) {
        Intent loadSignUpPage = new Intent(MainActivity.this, SignUpPage.class);
        startActivity(loadSignUpPage);
    }

    public void loginButton(View view) {
        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        // >6 at least one capital, at least one special, at least one number

        if (!passwordIsValid(password)) {
            Toast.makeText(MainActivity.this, "Please enter an email with a capital letter, special character, and numbers.",
                    Toast.LENGTH_SHORT).show();
        }
        if (emailIsEmail(email)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("log", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent loginUser = new Intent(MainActivity.this, SearchPage.class);
                            startActivity(loginUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("log", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(MainActivity.this, "Please enter valid email address and password.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}