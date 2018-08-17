package com.wootae.mumsungsungxi;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Alan on 8/11/2018.
 */

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.StudentListViewHolder> {
    private List<Attendance> attendances;
    private List<Student> students;
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

    private int getLastMonthStatus(Attendance attendance, LinearLayout[] rows, int startingRow) {
        LocalDate[] dates = attendance.lastMonthDates;
        String[] status = attendance.getLastMonthStatus();

        int currentRowNum = startingRow;
        LinearLayout currentRow = rows[currentRowNum-1];

        int attended = 0 ;
        int absented = 0;

        int tempCount = 0;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, Math.round(50 * mContext.getResources().getDisplayMetrics().density), 1f);
        layoutParams.setMargins(Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density));

        // name padding
        TextView tv_name = new TextView(mContext);
        tv_name.setLayoutParams(layoutParams);
        tv_name.setText(dates[0].getMonth().getValue() + "월");
        currentRow.addView(tv_name);

        // attended tv
        TextView tvAttendedBox = new TextView(mContext);
        tvAttendedBox.setLayoutParams(layoutParams);
        tvAttendedBox.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
        tvAttendedBox.setTextColor(Color.parseColor("#FFFFFF"));
        tvAttendedBox.setTextSize(4 * mContext.getResources().getDisplayMetrics().density);
//        tvAttendedBox.setGravity(Gravity.CENTER);

        // absent tv
        TextView tvAbsentedBox = new TextView(mContext);
        tvAbsentedBox.setLayoutParams(layoutParams);
        tvAbsentedBox.setText("결석: ");
        tvAbsentedBox.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
        tvAbsentedBox.setTextColor(Color.parseColor("#FFFFFF"));
//        tvAbsentedBox.setGravity(Gravity.CENTER);
        tvAbsentedBox.setTextSize(4 * mContext.getResources().getDisplayMetrics().density);

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

                if (tempCount == 0) {
                    currentRow.addView(tvAttendedBox);
                    tempCount++;
                } else if (tempCount == 1) {
                    currentRow.addView(tvAbsentedBox);
                    tempCount++;
                } else {
                    TextView tv = new TextView(mContext);
                    tv.setLayoutParams(layoutParams);
                    currentRow.addView(tv);
                }

                continue;
            }



            TextView tv = new TextView(mContext);
            tv.setLayoutParams(layoutParams);
            tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border));
            tv.setGravity(Gravity.CENTER);
            tv.setText(dates[i].format(DateTimeFormatter.ofPattern("M/d")).toString());


            if (status[i] != null) {
                if (status[i].equals(StudentStatus.ATTENDED)) {
                    tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
                    attended++;
                } else if (status[i].equals(StudentStatus.ABSENT)) {
                    tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
                    absented++;
                }
                tv.setTextColor(Color.parseColor("#FFFFFF"));
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

        tvAttendedBox.setText("출석: " + attended);
        tvAbsentedBox.setText("결석: " + absented);

//        currentRowNum++;
//        currentRow = rows[currentRowNum-1];
//
//        // monthly total
//        TextView tvBlankBox = new TextView(mContext);
//        TextView tvBlankBox2 = new TextView(mContext);
//        TextView tvBlankBox3 = new TextView(mContext);
//        tvBlankBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvBlankBox2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvBlankBox3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        currentRow.addView(tvBlankBox);
//        currentRow.addView(tvBlankBox2);
//        currentRow.addView(tvBlankBox3);
//
//        TextView tvAttendedBox = new TextView(mContext);
//        tvAttendedBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvAttendedBox.setText("출석: ");
//        tvAttendedBox.setBackgroundColor(Color.parseColor("#008000"));
//        tvAttendedBox.setTextColor(Color.parseColor("#FFFFFF"));
//        currentRow.addView(tvAttendedBox);
//
//        TextView tvAttended = new TextView(mContext);
//        tvAttended.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvAttended.setText(String.valueOf(attended));
//        currentRow.addView(tvAttended);
//
//        TextView tvAbsentedBox = new TextView(mContext);
//        tvAbsentedBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvAbsentedBox.setText("결석: ");
//        tvAbsentedBox.setBackgroundColor(Color.parseColor("#FF0000"));
//        tvAbsentedBox.setTextColor(Color.parseColor("#FFFFFF"));
//        currentRow.addView(tvAbsentedBox);
//
//        TextView tvAbsented = new TextView(mContext);
//        tvAbsented.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvAbsented.setText(String.valueOf(absented));
//        currentRow.addView(tvAbsented);


        return currentRowNum;
    }

    private int getThisMonthStatus(Attendance attendance, LinearLayout[] rows) {
        LocalDate[] dates = attendance.thisMonthDates;
        String[] status = attendance.getThisMonthStatus();
        int currentRowNum = 1;
        LinearLayout currentRow = rows[currentRowNum-1];


        int attended = 0 ;
        int absented = 0;

        int tempCount = 0;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, Math.round(50 * mContext.getResources().getDisplayMetrics().density), 1f);
        layoutParams.setMargins(Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density),Math.round(mContext.getResources().getDisplayMetrics().density));

        // prepading name
        TextView tv_name = new TextView(mContext);
        tv_name.setLayoutParams(layoutParams);
        tv_name.setText(dates[0].getMonth().getValue() + "월");
        currentRow.addView(tv_name);

        // attended tv
        TextView tvAttendedBox = new TextView(mContext);
        tvAttendedBox.setLayoutParams(layoutParams);
        tvAttendedBox.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
        tvAttendedBox.setTextColor(Color.parseColor("#FFFFFF"));
        tvAttendedBox.setTextSize(4 * mContext.getResources().getDisplayMetrics().density);
//        tvAttendedBox.setGravity(Gravity.CENTER);



        // absent tv
        TextView tvAbsentedBox = new TextView(mContext);
        tvAbsentedBox.setLayoutParams(layoutParams);
        tvAbsentedBox.setText("결석: ");
        tvAbsentedBox.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
        tvAbsentedBox.setTextColor(Color.parseColor("#FFFFFF"));
        tvAbsentedBox.setTextSize(4 * mContext.getResources().getDisplayMetrics().density);
//        tvAbsentedBox.setGravity(Gravity.CENTER);

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

                if (tempCount == 0) {
                    currentRow.addView(tvAttendedBox);
                    tempCount++;
                } else if (tempCount == 1) {
                    currentRow.addView(tvAbsentedBox);
                    tempCount++;
                } else {
                    TextView tv = new TextView(mContext);
                    tv.setLayoutParams(layoutParams);
                    currentRow.addView(tv);
                }

                continue;
            }

            TextView tv = new TextView(mContext);
            tv.setLayoutParams(layoutParams);
            tv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border));
            tv.setText(dates[i].format(DateTimeFormatter.ofPattern("M/d")).toString());
            tv.setGravity(Gravity.CENTER);

            if (status[i] != null) {
                if (status[i].equals(StudentStatus.ATTENDED)) {
                    tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
                    attended++;
                } else if (status[i].equals(StudentStatus.ABSENT)) {
                    tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
                    absented++;
                }
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            }


            currentRow.addView(tv);
        }

        // postpadding for the last row
        for (int i = dates[status.length-1].getDayOfWeek().getValue(); i < DayOfWeek.SATURDAY.getValue(); i++) {
            TextView tv = new TextView(mContext);
            tv.setLayoutParams(layoutParams);
            currentRow.addView(tv);
        }

        tvAttendedBox.setText("출석: " + attended);
        tvAbsentedBox.setText("결석: " + absented);

//        currentRowNum++;
//        currentRow = rows[currentRowNum-1];
//        // monthly total
//        TextView tvBlankBox = new TextView(mContext);
//        TextView tvBlankBox2 = new TextView(mContext);
//        TextView tvBlankBox3 = new TextView(mContext);
//        tvBlankBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvBlankBox2.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvBlankBox3.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        currentRow.addView(tvBlankBox);
//        currentRow.addView(tvBlankBox2);
//        currentRow.addView(tvBlankBox3);
//
//        TextView tvAttendedBox = new TextView(mContext);
//        tvAttendedBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvAttendedBox.setText("출석: ");
//        tvAttendedBox.setBackgroundColor(Color.parseColor("#008000"));
//        tvAttendedBox.setTextColor(Color.parseColor("#FFFFFF"));
//        currentRow.addView(tvAttendedBox);
//
//        TextView tvAttended = new TextView(mContext);
//        tvAttended.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvAttended.setText(String.valueOf(attended));
//        currentRow.addView(tvAttended);
//
//        TextView tvAbsentedBox = new TextView(mContext);
//        tvAbsentedBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvAbsentedBox.setText("결석: ");
//        tvAbsentedBox.setBackgroundColor(Color.parseColor("#FF0000"));
//        tvAbsentedBox.setTextColor(Color.parseColor("#FFFFFF"));
//        currentRow.addView(tvAbsentedBox);
//
//        TextView tvAbsented = new TextView(mContext);
//        tvAbsented.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
//        tvAbsented.setText(String.valueOf(absented));
//        currentRow.addView(tvAbsented);



        return currentRowNum;
    }

    public StudentListAdapter(Context context, List<Attendance> attendances, List<Student> students) {
        this.attendances = attendances;
        this.students = students;
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
        Student student = getStudent(attendance.getName());

        String[] thisWeek = attendance.getThisWeekStatus();

        holder.name.setText(student.getName());

        if (thisWeek[0] != null) {
            if (thisWeek[0].equals(StudentStatus.ATTENDED)) {
                holder.mon.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
                holder.mon.setText("○");
            } else {
                holder.mon.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
                holder.mon.setText("✕");
            }
            holder.mon.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[1] != null) {
            if (thisWeek[1].equals(StudentStatus.ATTENDED)) {
                holder.tues.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
                holder.tues.setText("○");
            } else {
                holder.tues.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
                holder.tues.setText("✕");
            }
            holder.tues.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[2] != null) {
            if (thisWeek[2].equals(StudentStatus.ATTENDED)) {
                holder.wens.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
                holder.wens.setText("○");
            } else {
                holder.wens.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
                holder.wens.setText("✕");
            }
            holder.wens.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[3] != null) {
            if (thisWeek[3].equals(StudentStatus.ATTENDED)) {
                holder.thurs.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
                holder.thurs.setText("○");
            } else {
                holder.thurs.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
                holder.thurs.setText("✕");
            }
            holder.thurs.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[4] != null) {
            if (thisWeek[4].equals(StudentStatus.ATTENDED)) {
                holder.fri.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
                holder.fri.setText("○");

            } else {
                holder.fri.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
                holder.fri.setText("✕");
            }
            holder.fri.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (thisWeek[5] != null) {
            if (thisWeek[5].equals(StudentStatus.ATTENDED)) {
                holder.sat.setBackgroundColor(ContextCompat.getColor(mContext, R.color.attended));
                holder.sat.setText("○");
            } else {
                holder.sat.setBackgroundColor(ContextCompat.getColor(mContext, R.color.absent));
                holder.sat.setText("✕");
            }
            holder.sat.setTextColor(Color.parseColor("#FFFFFF"));
        }

        holder.mCardView.setTag(attendance);

        int startingRow = getThisMonthStatus((Attendance) attendance, rows);
        int endingRow = getLastMonthStatus((Attendance) attendance, rows, startingRow + 1);
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


    public Student getStudent(String uid) {
        for (Student student : students) {
            if (student.getUid().equals(uid)) {
                return student;
            }
        }
        return null;
    }
}
