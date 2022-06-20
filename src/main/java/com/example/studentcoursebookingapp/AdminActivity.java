package com.example.studentcoursebookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminActivity extends AppCompatActivity{

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private DatabaseReference databaseReference;

    TextView textName;
    TextView textCode;
    Button buttonAddCourse, buttonRemoveCourse, buttonEditCourse;
    ListView listViewCourses;

    List<Course> courses;
    DatabaseReference databaseCourses;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        textName = (TextView) findViewById(R.id.courseName);
        textCode = (TextView) findViewById(R.id.courseCode);
        listViewCourses = (ListView) findViewById(R.id.listCourse);
        buttonAddCourse = (Button) findViewById(R.id.addCourse);
        buttonRemoveCourse = (Button) findViewById(R.id.removeCourse);
        buttonEditCourse = (Button) findViewById(R.id.editCourse);

        courses = new ArrayList<>();
        databaseCourses = FirebaseDatabase.getInstance().getReference("courses");
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.activity_list_item);
        buttonAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });
        buttonRemoveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeProduct();
            }
        });
        buttonEditCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseName = textName.getText().toString().trim();
                String courseCode = textCode.getText().toString().trim();
                editButton(courseName,courseCode);
            }
        });

        final TextView greetingTextView = (TextView) findViewById(R.id.greeting);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String fullName = userProfile.fullName;
                    String userName = userProfile.userName;
                    String email = userProfile.email;
                    String userType = userProfile.userType;

                    greetingTextView.setText("Welcome " + fullName + "! You are logged in as " + userType + ".");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void editButton(String courseName, String courseCode) {
        HashMap user = new HashMap();
        //user.put("courseName", textName.getText().toString());

        user.put("courseName", courseName);
        user.put("courseCode", courseCode);

        databaseReference = FirebaseDatabase.getInstance().getReference("courses");
        for (int i = 0; i < courses.size(); i++) {
            String getName = textName.getText().toString().trim();
            String getCode = textCode.getText().toString().trim();
            if (courses.get(i).getCourseName().equals(getName)) {
                String id = courses.get(i).getId();
                databaseReference.child(id).updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            textName.setText("");
                            textCode.setText("");

                            Toast.makeText(AdminActivity.this, "Successfully updated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AdminActivity.this, "Failed to complete", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }if (courses.get(i).getCourseCode().equals(getCode)) {
                String id = courses.get(i).getId();
                databaseReference.child(id).updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            textName.setText("");
                            textCode.setText("");

                            Toast.makeText(AdminActivity.this, "Successfully updated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AdminActivity.this, "Failed to complete", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    private void removeProduct(){
        reference = FirebaseDatabase.getInstance().getReference("courses");
        for(int i =0; i<courses.size();i++){
            String getName = textName.getText().toString().trim();
            String getCode = textCode.getText().toString().trim();
            if(courses.get(i).getCourseName().equals(getName) && courses.get(i).getCourseCode().equals(getCode)){
                String id = courses.get(i).getId();

                reference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AdminActivity.this, "Successfully Deleted", Toast.LENGTH_LONG).show();
                            textName.setText("");
                            textCode.setText("");
                        }else{
                            Toast.makeText(AdminActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                courses.remove(i);
                break;
            }
//            else{
//                Toast.makeText(AdminActivity.this,"No courses matched", Toast.LENGTH_LONG).show();
//            }
        }
    }

    protected void onStart(){
        super.onStart();
        databaseCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courses.clear();
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Course course = postSnapshot.getValue(Course.class);
                    courses.add(course);
                }
                ProductList productsAdapter = new ProductList(AdminActivity.this,courses);
                listViewCourses.setAdapter(productsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addProduct() {
        String name = textName.getText().toString().trim();
        String code = textCode.getText().toString().trim();

        if(!TextUtils.isEmpty(name)){
            String id = databaseCourses.push().getKey();

            Course course = new Course(id,name,code);
            databaseCourses.child(id).setValue(course);
            //arrayAdapter.notifyDataSetChanged();

            textName.setText("");
            textCode.setText("");

            Toast.makeText(this,"Course Added", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Please enter a course", Toast.LENGTH_LONG).show();
        }
    }
}