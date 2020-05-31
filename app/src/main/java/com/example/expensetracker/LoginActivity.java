package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private Button login, gotoRegister;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(this);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.pass_login);

        login = findViewById(R.id.login_btn);
        gotoRegister = findViewById(R.id.gotoRegistration);

        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = email.getText().toString().trim();
                String getPass = password.getText().toString().trim();
                builder.setView(getLayoutInflater().inflate(R.layout.progress,null));
                dialog = builder.create();
                dialog.show();
                logintoFirebase(getEmail,getPass);
            }
        });
    }

    private void logintoFirebase(String getEmail, String getPass) {

        if(TextUtils.isEmpty(getEmail)){
            email.setError("Required");
            dialog.dismiss();
            return;
        }
        if(TextUtils.isEmpty(getPass)){
            password.setError("Required");
            dialog.dismiss();
            return;
        }

        auth.signInWithEmailAndPassword(getEmail,getPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            currentUser = auth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,ExpenseActivity.class));
                            Log.e("username", "onComplete: "+currentUser.getDisplayName() );
                            dialog.dismiss();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }
}
