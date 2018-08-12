package com.wootae.mumsungsungxi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisActivity extends AppCompatActivity {
    TextView mon;
    TextView tues;
    TextView wens;
    TextView thurs;
    TextView fri;
    TextView sat;

    private RecyclerView mRecyclerView;
    private StudentListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ChildEventListener mAttendanceListener;

    private static final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference mAttendanceRef = mRootRef.child("attendance");

    private List<Attendance> attendances;

    LocalDateTime today;
    LocalDateTime[] thisWeek;
    String[] thisWeekStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        mon = findViewById(R.id.analysis_mon);
        tues = findViewById(R.id.analysis_tues);
        wens = findViewById(R.id.analysis_wens);
        thurs = findViewById(R.id.analysis_thurs);
        fri = findViewById(R.id.analysis_fri);
        sat = findViewById(R.id.analysis_sat);

//        String monday = LocalDateTime.now().with(DayOfWeek.MONDAY).format(DateTimeFormatter.ofPattern("M/d"));
        today = LocalDateTime.now();
        thisWeek = new LocalDateTime[6];
        thisWeek[0] = today.with(DayOfWeek.MONDAY);
        thisWeek[1] = today.with(DayOfWeek.TUESDAY);
        thisWeek[2] = today.with(DayOfWeek.WEDNESDAY);
        thisWeek[3] = today.with(DayOfWeek.THURSDAY);
        thisWeek[4] = today.with(DayOfWeek.FRIDAY);
        thisWeek[5] = today.with(DayOfWeek.SATURDAY);

        thisWeekStr = new String[6];
        thisWeekStr[0] = thisWeek[0].format(DateTimeFormatter.ofPattern("M/d"));
        thisWeekStr[1] = thisWeek[1].format(DateTimeFormatter.ofPattern("M/d"));
        thisWeekStr[2] = thisWeek[2].format(DateTimeFormatter.ofPattern("M/d"));
        thisWeekStr[3] = thisWeek[3].format(DateTimeFormatter.ofPattern("M/d"));
        thisWeekStr[4] = thisWeek[4].format(DateTimeFormatter.ofPattern("M/d"));
        thisWeekStr[5] = thisWeek[5].format(DateTimeFormatter.ofPattern("M/d"));

        mon.setText("월 " + thisWeekStr[0]);
        tues.setText("화 " + thisWeekStr[1]);
        wens.setText("수 " + thisWeekStr[2]);
        thurs.setText("목 " + thisWeekStr[3]);
        fri.setText("금 " + thisWeekStr[4]);
        sat.setText("토 " + thisWeekStr[5]);

        attendances = new ArrayList<>();

        Button btnDebug = findViewById(R.id.btn_debug);
        btnDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Attendance attendance : attendances) {
                    Log.d("TESTING126", attendance.getName() + "'s map");
                    for (Map.Entry<String, String> entry : attendance.getMap().entrySet()) {
                        Log.d("TESTING126", "key: " + entry.getKey() + "value: " + entry.getValue());
                    }
                }
            }
        });

        mRecyclerView = findViewById(R.id.recylcer_view_analysis);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new StudentListAdapter(this, attendances);
        mRecyclerView.setAdapter(mAdapter);


//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);

    }

    @Override
    protected void onResume() {
        super.onResume();

        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        detachDatabaseReadListener();
        clearLists();
    }

    private void clearLists() {
        attendances.clear();
    }

    private void detachDatabaseReadListener() {
        if (mAttendanceListener != null) {
            mAttendanceRef.removeEventListener(mAttendanceListener);
            mAttendanceListener = null;
        }
    }

    private void attachDatabaseReadListener() {
        if (mAttendanceListener == null) {
            mAttendanceListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("TESTING125", "key: " + dataSnapshot.getKey() + "value: " + dataSnapshot.getValue());
                    Attendance attendance = new Attendance(dataSnapshot.getKey(), (HashMap<String, String>) dataSnapshot.getValue(), thisWeek[0]);
                    attendances.add(attendance);

                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mAttendanceRef.addChildEventListener(mAttendanceListener);
        }
    }
}
