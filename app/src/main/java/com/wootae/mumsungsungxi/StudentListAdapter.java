package com.wootae.mumsungsungxi;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alan on 8/11/2018.
 */

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder> {
    private List<Student> students;
    private static Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class StudentListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public ListView mListView;
        public StudentListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_analysis);
            mListView = view.findViewById(R.id.list_view_analysis);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, name.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public StudentListAdapter(Context context, List<Student> students) {
        this.students = students;
        mContext = context;

        Log.d("TESTING123", "WTF");

        for (Student student : students) {
            Log.d("TESTING123", student.getName());
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StudentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.list_student_analysis, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_student_analysis, parent, false);
        return new StudentListViewHolder(view);

//        StudentListViewHolder vh = new StudentListViewHolder(v);
//        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(StudentListViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Student student = students.get(position);
        holder.name.setText(student.getName());
        holder.mListView.setTag(student);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return students.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
}
