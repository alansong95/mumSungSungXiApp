package com.wootae.mumsungsungxi;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Alan on 8/11/2018.
 */

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder> {
    private List<Attendance> attendances;
    private static Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class StudentListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public CardView mCardView;
        public StudentListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_analysis);
            mCardView = view.findViewById(R.id.card_view_analysis);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, name.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public StudentListAdapter(Context context, List<Attendance> attendances) {
        this.attendances = attendances;
        mContext = context;

        Log.d("TESTING123", "WTF");

        for (Attendance attendance : attendances) {
            Log.d("TESTING123", attendance.getName());
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

        Attendance attendance = attendances.get(position);
        holder.name.setText(attendance.getName());
        holder.mCardView.setTag(attendance);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return attendances.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
}
