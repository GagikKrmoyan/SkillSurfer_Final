package com.example.diliproj;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diliproj.Course;
import com.example.diliproj.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CourseAdd extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int SELECT_PHOTO_REQUEST = 1;

    private EditText titleEditText;
    private EditText descEditText;
    private EditText placeEditText;
    private RadioGroup paidFreeRadioGroup;
    private RadioButton paidRadioButton;
    private RadioButton freeRadioButton;
    private EditText priceEditText;
    private Button addCourseButton;
    private Button selectPhotoButton;
    private Spinner categorySpinner;
    private Uri selectedPhotoUri;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add);

        titleEditText = findViewById(R.id.title);
        descEditText = findViewById(R.id.desc);
        placeEditText = findViewById(R.id.place);
        paidFreeRadioGroup = findViewById(R.id.paidFreeRadioGroup);
        paidRadioButton = findViewById(R.id.paidRadioButton);
        freeRadioButton = findViewById(R.id.freeRadioButton);
        priceEditText = findViewById(R.id.priceEditText);
        addCourseButton = findViewById(R.id.addC);

        categorySpinner = findViewById(R.id.spinner_category);

        paidFreeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.paidRadioButton) {
                    priceEditText.setVisibility(View.VISIBLE);
                } else {
                    priceEditText.setVisibility(View.GONE);
                }
            }
        });

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String desc = descEditText.getText().toString();
                String place = placeEditText.getText().toString();
                String paidOrFree = "";
                double price = 0.0;

                int checkedId = paidFreeRadioGroup.getCheckedRadioButtonId();
                if (checkedId == R.id.paidRadioButton) {
                    paidOrFree = "Paid";
                    String priceText = priceEditText.getText().toString();
                    if (!priceText.isEmpty()) {
                        price = Double.parseDouble(priceText);
                    }
                } else if (checkedId == R.id.freeRadioButton) {
                    paidOrFree = "Free";
                }

                if (title.isEmpty() || desc.isEmpty() || place.isEmpty() || paidOrFree.isEmpty() || selectedCategory.isEmpty()) {
                    Toast.makeText(CourseAdd.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Course course = new Course(title, desc, place, paidOrFree, price, selectedCategory);
                uploadCourse(course);
            }
        });

        categorySpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedPhotoUri = data.getData();
                Toast.makeText(this, "Image selected: " + selectedPhotoUri.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadCourse(final Course course) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Courses");
        final String courseId = UUID.randomUUID().toString();

        if (selectedPhotoUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("CourseImages").child(courseId);
            storageReference.putFile(selectedPhotoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();
                                            course.setImageUrl(downloadUrl);
                                            saveCourseToDatabase(course, courseId);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CourseAdd.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                            saveCourseToDatabase(course, courseId);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CourseAdd.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            saveCourseToDatabase(course, courseId);
                        }
                    });
        } else {
            saveCourseToDatabase(course, courseId);
        }
    }

    private void saveCourseToDatabase(Course course, String courseId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Courses").child(courseId);
        Map<String, Object> courseValues = new HashMap<>();
        courseValues.put("title", course.getTitle());
        courseValues.put("description", course.getDescription());
        courseValues.put("place", course.getPlace());
        courseValues.put("paidOrFree", course.getPaidOrFree());
        courseValues.put("price", course.getPrice());
        courseValues.put("category", course.getCategory());
        courseValues.put("imageUrl", course.getImageUrl());

        databaseReference.setValue(courseValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CourseAdd.this, "Course added successfully", Toast.LENGTH_SHORT).show();
                        // Pass the current user's ID to MainActivity
                        Intent intent = new Intent(CourseAdd.this, MainActivity.class);
                        intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CourseAdd.this, "Failed to add course", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedCategory = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        selectedCategory = "";
    }
}
