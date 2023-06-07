package com.example.diliproj;



import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class FavoritesCatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_catalog);

        List<String> favoriteCourses = getIntent().getStringArrayListExtra("favoriteCourses");

        TextView coursesTextView = findViewById(R.id.coursesTextView);
        StringBuilder sb = new StringBuilder();

        if (favoriteCourses != null && !favoriteCourses.isEmpty()) {
            for (String course : favoriteCourses) {
                sb.append(course).append("\n");
            }
        } else {
            sb.append("No favorite courses");
        }

        coursesTextView.setText(sb.toString());
    }
}

