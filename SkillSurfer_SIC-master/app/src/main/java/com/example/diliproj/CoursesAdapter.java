package com.example.diliproj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {
    private Context context;

    private List<Course> courseList;
    FirebaseDatabase database;
    FirebaseAuth auth;


    public CoursesAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.title.setText(course.getTitle());
        holder.desc.setText(course.getDescription());
        holder.url.setText(course.getImageUrl());

        final int itemPosition = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CourseInfo.class);
                intent.putExtra("title", course.getTitle());
                intent.putExtra("desc", course.getDescription());
                intent.putExtra("url", course.getImageUrl());
                intent.putExtra("place", course.getPlace());
                intent.putExtra("paidOrFree", course.getPaidOrFree());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void filterList(ArrayList<Course> filteredList) {
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, url;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            url=itemView.findViewById(R.id.url);
        }
    }
}
