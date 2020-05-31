package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensetracker.Model.ExpenseData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Button signup;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(this);

        email = findViewById(R.id.email_signup);
        password = findViewById(R.id.pass_signup);

        signup = findViewById(R.id.signupBtn);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  getEmail = email.getText().toString().trim();
                String getPass = password.getText().toString().trim();


                builder.setView(getLayoutInflater().inflate(R.layout.progress,null));
                dialog = builder.create();
                dialog.show();
                createAccounttoFirebase(getEmail,getPass);
            }
        });
    }

    private void createAccounttoFirebase(String getEmail, String getPass) {
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



        if(password.length()>5){
            auth.createUserWithEmailAndPassword(getEmail,getPass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                user = auth.getCurrentUser();

                                Intent i = new Intent(RegistrationActivity.this, ExpenseActivity.class);

                                startActivity(i);
                                dialog.dismiss();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegistrationActivity.this, "Failed registration", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
        else{
            password.setError("Password must be at least 6 digit");
            dialog.dismiss();
            return;
        }


    }
}
