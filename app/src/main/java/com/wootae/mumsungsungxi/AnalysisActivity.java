package com.wootae.mumsungsungxi;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisActivity extends AppCompatActivity implements ExcelDialog.ExcelListener {
    private static final String TAG = "ANALYSISACTIVITY_DEBUG";
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
    private ChildEventListener mStudentListener;

    private static final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static DatabaseReference mAttendanceRef = mRootRef.child("attendance");
    private static DatabaseReference mStudentsRef = mRootRef.child("students");

    private List<Attendance> attendances;

    LocalDate today;
    LocalDate[] thisWeek;
    String[] thisWeekStr;

    List<Student> students;

    // Excel Listener
    public void createExcel(int year, int month) {
        Toast.makeText(this, "Year: " + year + "Month: " + month, Toast.LENGTH_SHORT).show();

        File sdCard = Environment.getExternalStorageDirectory();
        Log.d("Testing140", "140: "+ sdCard.toString());
        //add on the your app's path
        File dir = new File(sdCard.getAbsolutePath() + "/mumSungSungXi");

        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate today = LocalDate.now();
        Period intervalPeriod = Period.between(from, today);
        int monthDiff = 12 * intervalPeriod.getYears() + intervalPeriod.getMonths() + 1;

        for (int i = 0; i < monthDiff; i++) {
            File file = new File(dir, (from.format(DateTimeFormatter.ofPattern("yyyy-M"))+".csv"));

            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file, false);

                // header (dates)
                outputStream.write(",".getBytes());
                //outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                for (int j = 0; j < from.getDayOfMonth(); j++) {
                    Log.d("TESTING161", "161: ");
                    outputStream.write((from.getMonth().getValue() + "/" + from.getDayOfMonth() + ",").getBytes());
                    from = from.plusDays(1);
                }
                outputStream.write("\n".getBytes());

                // back to 1st of month for contents
                // contents
                for (Attendance attendance : attendances) {
                    HashMap<String, String> map = attendance.getMap();

                    Log.d("TESTING161", "161: " + from.minusDays(1).toString());
                    Log.d("TESTING161", "161: " + map.get(from.minusDays(1).toString()));

                    if (map.get(from.minusDays(1).toString()) != null) { // fix this
                        // then data for the month exist for this student

                        from = from.minusMonths(1);
                        Log.d("TESTING162", "162: " + from.toString());
                        outputStream.write((getStudent(attendance.getName()).getName() + ",").getBytes());
                        for (int j = 0; j < from.getDayOfMonth(); j++) {
                            Log.d("TESTING162", "162: " + from.toString());
                            String status = map.get(from.toString());
                            if (status.equals("ATTENDED")) {
                                outputStream.write(("출석,").getBytes());
                            } else if (status.equals("ABSENT")) {
                                outputStream.write(("결석,").getBytes());
                            }

                            from = from.plusDays(1);
                        }
                        outputStream.write("\n".getBytes());
                    }

                }
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        students = new ArrayList<>();

        mon = findViewById(R.id.analysis_mon);
        tues = findViewById(R.id.analysis_tues);
        wens = findViewById(R.id.analysis_wens);
        thurs = findViewById(R.id.analysis_thurs);
        fri = findViewById(R.id.analysis_fri);
        sat = findViewById(R.id.analysis_sat);

//        String monday = LocalDateTime.now().with(DayOfWeek.MONDAY).format(DateTimeFormatter.ofPattern("M/d"));
        today = LocalDate.now();
        thisWeek = new LocalDate[6];
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
        mAdapter = new StudentListAdapter(this, attendances, students);
        mRecyclerView.setAdapter(mAdapter);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        Log.d("TESTING160", "160: ");


        attachDatabaseReadListener();
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        detachDatabaseReadListener();
//        clearLists();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        detachDatabaseReadListener();
//        clearLists();
//    }

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
        if (mStudentListener == null) {
            mStudentListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Student student = dataSnapshot.getValue(Student.class);
                    students.add(student);

                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    mAdapter.notifyDataSetChanged();
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
            mStudentsRef.addChildEventListener(mStudentListener);
        }


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
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mAdapter.notifyDataSetChanged();
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


    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_analysis, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.excel:
                ExcelDialog excelDialog = new ExcelDialog();
                excelDialog.show(getSupportFragmentManager(), "");
                return true;
            case R.id.test:
                Log.d("TESTING170", "170: starting");
                for (Student student : students) {
                    Log.d("TESTING170", "170: " + student.getName());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Excel
    private void createExcelFile() {
        Toast.makeText(this, "Creating an excel file", Toast.LENGTH_SHORT).show();



//        WritableWorkbook workBook = createWorkBook("attendance.xls");
//        WritableSheet sheet = createSheet(workBook, "8월", 0);
//        WritableSheet sheet2 = createSheet(workBook, "9월", 1);
//        try {
//            writeCell(1,1, "TESTING", false, sheet);
//        } catch (WriteException e) {
//            Log.d(TAG, e.getMessage());
//        }

        File sdCard = Environment.getExternalStorageDirectory();
        Log.d("Testing140", "140: "+ sdCard.toString());
        //add on the your app's path
        File dir = new File(sdCard.getAbsolutePath() + "/mumSungSungXi");
        File file = new File(dir, "test.csv");

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, true);
            //outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            for (int i = 0; i < 10; i++) {
                outputStream.write((i+1 + ",").getBytes());
                outputStream.write((i + ",").getBytes());
                outputStream.write("\n".getBytes());

            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
//
//    public WritableWorkbook createWorkBook(String fileName) {
//        WorkbookSettings wbSettings = new WorkbookSettings();
//        wbSettings.setUseTemporaryFileDuringWrite(true);
//
//        // get the sdcard's directory
//        File sdCard = Environment.getExternalStorageDirectory();
//        Log.d("Testing140", "140: "+ sdCard.toString());
//        //add on the your app's path
//        File dir = new File(sdCard.getAbsolutePath() + "/mumSungSungXi");
//        Log.d("Testing141", "141: " + dir);
//        //make them in case they're not there
//        boolean temp = dir.mkdirs();
//        Log.d("Testing142", "142: " + temp);
//        //create a standard java.io.File object for the Workbook to use
//        File wbfile = new File(dir,fileName);
//
//        WritableWorkbook wb = null;
//
//        try{
//            //create a new WritableWorkbook using the java.io.File and
//            //WorkbookSettings from above
//            wb = Workbook.createWorkbook(wbfile,wbSettings);
//        }catch(IOException ex){
//            Log.e(TAG,ex.getStackTrace().toString());
//            Log.e(TAG, ex.getMessage());
//        }
//
//        return wb;
//    }
//
//    public WritableSheet createSheet(WritableWorkbook wb, String sheetName, int sheetIndex){
//        //create a new WritableSheet and return it
//        return wb.createSheet(sheetName, sheetIndex);
//    }
//
//    public void writeCell(int columnPosition, int rowPosition, String contents, boolean headerCell, WritableSheet sheet) throws RowsExceededException, WriteException {
//        //create a new cell with contents at position
//        Label newCell = new Label(columnPosition,rowPosition,contents);
//
//        if (headerCell){
//            //give header cells size 10 Arial bolded
//            WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
//            WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
//            //center align the cells' contents
//            headerFormat.setAlignment(Alignment.CENTRE);
//            newCell.setCellFormat(headerFormat);
//        }
//
//        sheet.addCell(newCell);
//    }

    public Student getStudent(String uid) {
        for (Student student : students) {
            if (student.getUid().equals(uid)) {
                return student;
            }
        }
        return null;
    }
}
