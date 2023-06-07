package com.example.diliproj;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage extends AppCompatActivity {

    FirebaseDatabase database;
    RecyclerView recyclerViewCourses;
    CoursesAdapter coursesAdapter;
    EditText editTextSearch;
    Spinner spinnerCategory;
    ArrayList<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Button bt = findViewById(R.id.add);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, CourseAdd.class);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();

        recyclerViewCourses = findViewById(R.id.listview);
        recyclerViewCourses.setVisibility(View.VISIBLE);
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        DatabaseReference ref = database.getReference().child("Courses");
        courses = new ArrayList<>();
        coursesAdapter = new CoursesAdapter(getApplicationContext(), courses);
        recyclerViewCourses.setAdapter(coursesAdapter);

        editTextSearch = findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Query query = ref.orderByChild("title").startAt(s.toString()).endAt(s.toString() + "\uf8ff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courses.clear();
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            Course item = childSnapshot.getValue(Course.class);
                            item.setTitle(childSnapshot.child("title").getValue(String.class));
                            item.setDescription(childSnapshot.child("description").getValue(String.class));
                            item.setImageUrl(childSnapshot.child("imageUrl").getValue(String.class));
                            courses.add(item);
                        }
                        coursesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinnerCategory = findViewById(R.id.spinnerCategory);
        List<String> categoryList = new ArrayList<>();
        categoryList.add("All Courses");
        String[] categoriesArray = getResources().getStringArray(R.array.categories_array);
        categoryList.addAll(Arrays.asList(categoriesArray));

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if ("All Courses".equals(selectedCategory)) {

                    showAllCourses();
                } else {

                    filterCoursesByCategory(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courses.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Course item = childSnapshot.getValue(Course.class);
                    item.setTitle(childSnapshot.child("title").getValue(String.class));
                    item.setDescription(childSnapshot.child("description").getValue(String.class));
                    item.setImageUrl(childSnapshot.child("imageUrl").getValue(String.class));
                    courses.add(item);
                }
                coursesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent_home = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intent_home);
                    finish();
                    return true;
                case R.id.navigation_user:
                    Intent intent_user = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent_user);

                    return true;
            }
            return false;
        });
    }

    private void showAllCourses() {
        DatabaseReference coursesRef = database.getReference("Courses");
        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courses.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Course item = childSnapshot.getValue(Course.class);
                    item.setTitle(childSnapshot.child("title").getValue(String.class));
                    item.setDescription(childSnapshot.child("description").getValue(String.class));
                    item.setImageUrl(childSnapshot.child("imageUrl").getValue(String.class));
                    courses.add(item);
                }
                coursesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterCoursesByCategory(String category) {
        Query query;
        if ("All Courses".equals(category)) {
            query = database.getReference().child("Courses");
        } else {
            query = database.getReference().child("Courses").orderByChild("category").equalTo(category);
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courses.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Course item = childSnapshot.getValue(Course.class);
                    item.setTitle(childSnapshot.child("title").getValue(String.class));
                    item.setDescription(childSnapshot.child("description").getValue(String.class));
                    item.setImageUrl(childSnapshot.child("imageUrl").getValue(String.class));
                    courses.add(item);
                }
                coursesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
