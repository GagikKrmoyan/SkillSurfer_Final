package com.example.diliproj;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diliproj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseInfo extends AppCompatActivity {

    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> showRegistrationDialog());

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String place = intent.getStringExtra("place");
        String paidOrFree = intent.getStringExtra("paidOrFree");
        String courseId = intent.getStringExtra("courseId");

        TextView titleTv = findViewById(R.id.title);
        TextView descTv = findViewById(R.id.desc);
        TextView placeTv = findViewById(R.id.place);

        TextView paidOrFreeTv = findViewById(R.id.paidOrFree);

        titleTv.setText(title);
        descTv.setText(desc);
        placeTv.setText(place);
        paidOrFreeTv.setText(paidOrFree);


        if (courseId != null) {
            final String finalCourseId = courseId;
            FirebaseDatabase.getInstance().getReference("Courses")
                    .child(courseId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Course course = dataSnapshot.getValue(Course.class);
                                if (course != null) {
                                    String categoryId = course.getCategory();

                                    Log.d("CourseInfo", "CourseId: " + finalCourseId);
                                    Log.d("CourseInfo", "Retrieved Course: " + course.toString());
                                }
                            } else {
                                Toast.makeText(CourseInfo.this, "Course not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(CourseInfo.this, "Failed to retrieve course.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(CourseInfo.this, "Invalid courseId.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRegistrationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.registration_dialog);

        EditText emailEditText = dialog.findViewById(R.id.emailEditText);
        Button submitButton = dialog.findViewById(R.id.submitButton);

        EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        String name = nameEditText.getText().toString().trim();


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                emailEditText.setText(email);
            }
        }

        submitButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                String registrationDetails = "Email: " + email;
                Toast.makeText(this, "Successfully registered! Confirm Email has been sent\n\n" + registrationDetails, Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}


