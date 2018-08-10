package com.wootae.mumsungsungxi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class AnalysisActivity extends AppCompatActivity {
    TextView mon;
    TextView tues;
    TextView wens;
    TextView thurs;
    TextView fri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        mon = findViewById(R.id.analysis_mon);
        tues = findViewById(R.id.analysis_tues);
        wens = findViewById(R.id.analysis_wens);
        thurs = findViewById(R.id.analysis_thurs);
        fri = findViewById(R.id.analysis_fri);

//        String monday = LocalDateTime.now().with(DayOfWeek.MONDAY).format(DateTimeFormatter.ofPattern("M/d"));
        LocalDateTime today = LocalDateTime.now();

        mon.setText("월 " + today.with(DayOfWeek.MONDAY).format(DateTimeFormatter.ofPattern("M/d")));
        tues.setText("화 " + today.with(DayOfWeek.TUESDAY).format(DateTimeFormatter.ofPattern("M/d")));
        wens.setText("수 " + today.with(DayOfWeek.WEDNESDAY).format(DateTimeFormatter.ofPattern("M/d")));
        thurs.setText("목 " + today.with(DayOfWeek.THURSDAY).format(DateTimeFormatter.ofPattern("M/d")));
        fri.setText("금 " + today.with(DayOfWeek.FRIDAY).format(DateTimeFormatter.ofPattern("M/d")));


    }
}
