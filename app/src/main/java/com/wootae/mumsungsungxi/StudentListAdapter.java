package com.wootae.mumsungsungxi;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Alan on 8/11/2018.
 */

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder> {
    private List<Attendance> attendances;
    private static Context mContext;

    LinearLayout[] rows;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class StudentListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public CardView mCardView;
        TextView mon;
        TextView tues;
        TextView wens;
        TextView thurs;
        TextView fri;
        TextView sat;

        LinearLayout monthlyView;

        boolean toggle;

        public StudentListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name_analysis);
            mCardView = view.findViewById(R.id.card_view_analysis);
            mon = view.findViewById(R.id.analysis_mon_each);
            tues = view.findViewById(R.id.analysis_tues_each);
            wens = view.findViewById(R.id.analysis_wens_each);
            thurs = view.findViewById(R.id.analysis_thurs_each);
            fri = view.findViewById(R.id.analysis_fri_each);
            sat = view.findViewById(R.id.analysis_sat_each);

            monthlyView = view.findViewById(R.id.monthly);

            rows = new LinearLayout[] {
                    view.findViewById(R.id.row1),
                view.findViewById(R.id.row2),
                view.findViewById(R.id.row3),
                view.findViewById(R.id.row4),
                view.findViewById(R.id.row5),
                view.findViewById(R.id.row6),
                    view.findViewById(R.id.row7),
                    view.findViewById(R.id.row8),
                    view.findViewById(R.id.row9),
                    view.findViewById(R.id.row10),
                    view.findViewById(R.id.row11),
                    view.findViewById(R.id.row12),
            };

            monthlyView.setVisibility(View.GONE);
            toggle = true;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, name.getText().toString(), Toast.LENGTH_SHORT).show();
                    if (toggle == true) {
                        monthlyView.setVisibility(View.VISIBLE);
                        toggle = false;
                    } else {
                        monthlyView.setVisibility(View.GONE);
                        toggle = true;
                    }
                }
            });


        }
    }

    private int getLastMonthStatus(Attendance attendance, LinearLayout[] rows) {
        LocalDate[] dates = attendance.lastMonthDates;
        String[] status = attendance.getLastMonthStatus();
        int currentRowNum = 1;
        LinearLayout currentRow = rows[currentRowNum-1];

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, Math.round(50 * mContext.getResources().getDisplayMetrics().density), 1f);
        layoutParams.setMargins(Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density));

        // name padding
        TextView tv_name = new TextView(mContext);
        tv_name.setLayoutParams(layoutParams);
        tv_name.setText(dates[0].getMonth() + "월");
        currentRow.addView(tv_name);


        // prepading for the first row
        if (dates[0].getDayOfWeek().equals(DayOfWeek.SUNDAY)) {

        } else {
            for (int i = 1; i < dates[0].getDayOfWeek().getValue(); i++) {
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(layoutParams);
                currentRow.addView(tv);
                Log.d("TESTING135", "adding prepading");
            }
        }




        for (int i = 0; i < status.length; i++) {
            if (dates[i].getDayOfWeek() == DayOfWeek.SUNDAY) {
                if (i == 0) {
                    continue;
                }
                currentRowNum++;
                currentRow = rows[currentRowNum-1];

                TextView tv = new TextView(mContext);
                tv.setLayoutParams(layoutParams);
                currentRow.addView(tv);

                continue;
            }



            TextView tv = new TextView(mContext);
            tv.setLayoutParams(layoutParams);
            tv.setText(dates[i].format(DateTimeFormatter.ofPattern("M/d")).toString());

            if (status[i] != null) {
                if (status[i].equals(StudentStatus.ATTENDED)) {
                    tv.setBackgroundColor(Color.parseColor("#008000"));
                } else if (status[i].equals(StudentStatus.ABSENT)) {
                    tv.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }

            currentRow.addView(tv);
        }

        // postpadding for the last row
        for (int i = dates[status.length-1].getDayOfWeek().getValue(); i < DayOfWeek.SATURDAY.getValue(); i++) {
            Log.d("TESTING133", "date: " + dates[status.length]);

            Log.d("TESTING132", "i: " + i );
            TextView tv = new TextView(mContext);
            tv.setLayoutParams(layoutParams);
            currentRow.addView(tv);
        }


        return currentRowNum;
    }

    private int getThisMonthStatus(Attendance attendance, LinearLayout[] rows, int startingRow) {
        LocalDate[] dates = attendance.thisMonthDates;
        String[] status = attendance.getThisMonthStatus();
        int currentRowNum = startingRow;
        LinearLayout currentRow = rows[currentRowNum-1];

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, Math.round(50 * mContext.getResources().getDisplayMetrics().density), 1f);
        layoutParams.setMargins(Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density));

        // prepading name
        TextView tv_name = new TextView(mContext);
        tv_name.setLayoutParams(layoutParams);
        tv_name.setText(dates[0].getMonth() + "월");
        currentRow.addView(tv_name);

        // prepading for the first row
        if (dates[0].getDayOfWeek().equals(DayOfWeek.SUNDAY)) {

        } else {
            for (int i = 1; i < dates[0].getDayOfWeek().getValue(); i++) {
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(layoutParams);
                currentRow.addView(tv);
                Log.d("TESTING135", "adding prepading");
            }
        }

        for (int i = 0; i < status.length; i++) {
            if (dates[i].getDayOfWeek() == DayOfWeek.SUNDAY) {
                if (i == 0) {
                    continue;
                }
                currentRowNum++;
                currentRow = rows[currentRowNum-1];

                TextView tv = new TextView(mContext);
                tv.setLayoutParams(layoutParams);
                currentRow.addView(tv);

                continue;
            }

            TextView tv = new TextView(mContext);
            tv.setLayoutParams(layoutParams);
            tv.setText(dates[i].format(DateTimeFormatter.ofPattern("M/d")).toString());

            if (status[i] != null) {
                if (status[i].equals(StudentStatus.ATTENDED)) {
                    tv.setBackgroundColor(Color.parseColor("#008000"));
                } else if (status[i].equals(StudentStatus.ABSENT)) {
                    tv.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }


            currentRow.addView(tv);
        }

        // postpadding for the last row
        for (int i = dates[status.length-1].getDayOfWeek().getValue(); i < DayOfWeek.SATURDAY.getValue(); i++) {
            TextView tv = new TextView(mContext);
            tv.setLayoutParams(layoutParams);
            currentRow.addView(tv);
        }

        return currentRowNum;
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
        String[] thisWeek = attendance.getThisWeekStatus();
        holder.name.setText(attendance.getName());

        if (thisWeek[0] != null) {
            if (thisWeek[0].equals(StudentStatus.ATTENDED)) {
                holder.mon.setBackgroundColor(Color.parseColor("#008000"));
                holder.mon.setText("○");
            } else {
                holder.mon.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.mon.setText("✕");
            }
            holder.mon.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[1] != null) {
            if (thisWeek[1].equals(StudentStatus.ATTENDED)) {
                holder.tues.setBackgroundColor(Color.parseColor("#008000"));
                holder.tues.setText("○");
            } else {
                holder.tues.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.tues.setText("✕");
            }
            holder.tues.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[2] != null) {
            if (thisWeek[2].equals(StudentStatus.ATTENDED)) {
                holder.wens.setBackgroundColor(Color.parseColor("#008000"));
                holder.wens.setText("○");
            } else {
                holder.wens.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.wens.setText("✕");
            }
            holder.wens.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[3] != null) {
            if (thisWeek[3].equals(StudentStatus.ATTENDED)) {
                holder.thurs.setBackgroundColor(Color.parseColor("#008000"));
                holder.thurs.setText("○");
            } else {
                holder.thurs.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.thurs.setText("✕");
            }
            holder.thurs.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[4] != null) {
            if (thisWeek[4].equals(StudentStatus.ATTENDED)) {
                holder.fri.setBackgroundColor(Color.parseColor("#008000"));
                holder.fri.setText("○");

            } else {
                holder.fri.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.fri.setText("✕");
            }
            holder.fri.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[5] != null) {
            if (thisWeek[5].equals(StudentStatus.ATTENDED)) {
                holder.sat.setBackgroundColor(Color.parseColor("#008000"));
                holder.sat.setText("○");
            } else {
                holder.sat.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.sat.setText("✕");
            }
            holder.sat.setTextColor(Color.parseColor("#FFFFFF"));
        }

        holder.mCardView.setTag(attendance);

        int startingRow = getLastMonthStatus((Attendance) attendance, rows);
        int endingRow = getThisMonthStatus((Attendance) attendance, rows, startingRow + 1);
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
