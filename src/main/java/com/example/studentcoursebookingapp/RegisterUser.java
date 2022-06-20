package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{

    private TextView registerUser;
    private EditText editFullName, editUserName, editEmailAddress, editPassword, editUserType;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    //FirebaseFirestore fStore;

    CheckBox isAdmin, isStudent, isConstructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        //fStore = FirebaseFirestore.getInstance();



        registerUser = (Button) findViewById(R.id.signIn);
        registerUser.setOnClickListener(this);

        editFullName = (EditText) findViewById(R.id.fullName);
        editUserName = (EditText) findViewById(R.id.userName);
        editEmailAddress = (EditText) findViewById(R.id.email);
        editPassword = (EditText) findViewById(R.id.password);
        //editUserType = (EditText) findViewById(R.id.);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        isAdmin = findViewById(R.id.typeAdmin);
        isStudent = findViewById(R.id.typeStudent);
        isConstructor = findViewById(R.id.typeConstructor);

        isStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isAdmin.setChecked(false);
                    isConstructor.setChecked(false);
                }
            }
        });
        isAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isStudent.setChecked(false);
                    isConstructor.setChecked(false);
                }
            }
        });
        isConstructor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isStudent.setChecked(false);
                    isAdmin.setChecked(false);
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signIn:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editEmailAddress.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String fullName = editFullName.getText().toString().trim();
        String userName = editUserName.getText().toString().trim();
        //String userType = editUserType.getText().toString().trim();
        String student = isStudent.getText().toString().trim();
        String admin = isAdmin.getText().toString().trim();
        String constructor = isConstructor.getText().toString().trim();

        if(fullName.isEmpty()){
            editFullName.setError("Full Name is required!");
            editFullName.requestFocus();
            return;
        }
        if(userName.isEmpty()){
            editUserName.setError("User Name is required!");
            editUserName.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editEmailAddress.setError("Email is required!");
            editEmailAddress.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmailAddress.setError("Please provide valid Email Address!");
            editEmailAddress.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editPassword.setError("Password is required!");
            editPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            editPassword.setError("Min password length should be 6 characters!");
            editPassword.requestFocus();
            return;
        }
//        if(userType.isEmpty()){
//            editUserName.setError("User Type is required!");
//            editUserName.requestFocus();
//            return;
//        }
        if(!(isStudent.isChecked() || isAdmin.isChecked() || isConstructor.isChecked())){
            Toast.makeText(RegisterUser.this, "Select the account type", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(fullName, userName, email);
                            if(isStudent.isChecked()){
                                user.userType = "Student";
                            }
                            if(isAdmin.isChecked()){
                                user.userType = "Admin";
                            }
                            if(isConstructor.isChecked()){
                                user.userType="Constructor";
                            }

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                finish();
                                                //redirect to login user
                                            }else{
                                                Toast.makeText(RegisterUser.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(RegisterUser.this, "Failed to register!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}