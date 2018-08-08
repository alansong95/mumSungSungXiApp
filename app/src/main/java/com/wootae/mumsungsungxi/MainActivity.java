package com.wootae.mumsungsungxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddStudentDialog.AddStudentListener, StudentAdapter.EditStudentRequestListener, EditStudentDialog.EditStudentListener {
    // Debug
    private static final String TAG = "MainActivity_Debug";

    // Static variables
    public static final int NUM_OF_CLASSES = 5;

    // Hakwon
    private static int numOfStudents = 0; // decide this or static variable in Student class
    private static List<Student> students;
    public static String arrivalMessage;
    public static String departureMessage;

    // Classes
    private static String[] classNames;
    public static List<Student> classOne;
    public static List<Student> classTwo;
    public static List<Student> classThree;
    public static List<Student> classFour;
    public static List<Student> classFive;

    // Student Adapters
    public static StudentAdapter mStudentAdapterOne;
    public static StudentAdapter mStudentAdapterTwo;
    public static StudentAdapter mStudentAdapterThree;
    public static StudentAdapter mStudentAdapterFour;
    public static StudentAdapter mStudentAdapterFive;


    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 123;
    private static final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static final DatabaseReference mStudentsRef = mRootRef.child("students");
    public static DatabaseReference mCustomMessagesRef = mRootRef.child("customMessages");

    // Firebase 2
    private ChildEventListener mStudentEventListener;
    private ChildEventListener mCustomMessagesEventListener;

    // Layout
    TabLayout mTabLayout;
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;

    // AddStudentDialog Listener
    public void addNewStudent(String[] studentData) {
        addStudentToDatabase(studentData[0], studentData[1], studentData[2]);
    }

    // StudentAdapter Listener (Edit Student Request)
    public void editStudentRequest(Student student) {
        Toast.makeText(this, "EDIT: " + student.getName(), Toast.LENGTH_SHORT).show();
        EditStudentDialog editStudentDialog = new EditStudentDialog();

        Bundle args = new Bundle();
        args.putString("uid", student.getUid());
        editStudentDialog.setArguments(args);

        editStudentDialog.show(getSupportFragmentManager(), "");
        editStudentDialog.setCancelable(false);
    }
    public void openMessageDialog(Student student) {
        Toast.makeText(this, "Opening Message Dialog for: " + student.getName(), Toast.LENGTH_SHORT).show();
        messageDialog editStudentDialog = new messageDialog();

        Bundle args = new Bundle();
        args.putString("uid", student.getUid());
        editStudentDialog.setArguments(args);

        editStudentDialog.show(getSupportFragmentManager(), "");
//        editStudentDialog.setCancelable(false);
    }

    // EditStudentDialog Listener
    public void editStudent(String[] studentData) {
        editStudentOnDatabase(studentData[0], studentData[1], studentData[2], studentData[3]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tablayout & Viewpager
        mViewPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tablayout);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // Firebase security
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    Toast.makeText(MainActivity.this, "You are signed in", Toast.LENGTH_SHORT).show();

                    attachDatabaseReadListener();

                } else {
                    // user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        // Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_black_48dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddStudentDialog addStudentDialog = new AddStudentDialog();
                addStudentDialog.show(getSupportFragmentManager(), "");
                addStudentDialog.setCancelable(false);
            }
        });

        // Initiate Hakwon variables
        students = new ArrayList<>();
        classNames = getResources().getStringArray(R.array.classes);

        // student adapters
        classOne = new ArrayList<>();
        classTwo = new ArrayList<>();
        classThree = new ArrayList<>();
        classFour = new ArrayList<>();
        classFive = new ArrayList<>();
        mStudentAdapterOne = new StudentAdapter(this, classOne);
        mStudentAdapterTwo = new StudentAdapter(this, classTwo);
        mStudentAdapterThree = new StudentAdapter(this, classThree);
        mStudentAdapterFour = new StudentAdapter(this, classFour);
        mStudentAdapterFive = new StudentAdapter(this, classFive);

        requestMessagePermission();

    }

    // MainActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null)
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

        detachDatabaseReadListener();
        clearLists();
//        mViewPagerAdapter.clear();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_messages:
                EditMessagesDialog editMessagesDialog = new EditMessagesDialog();
                editMessagesDialog.show(getSupportFragmentManager(), "");
//                editMessagesDialog.setCancelable(false);
                return true;
            case R.id.logout:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.test:
                printStatus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Hakwon methods
    public static void addStudentToDatabase(String name, String phoneNumber, String section) {
        Student student = new Student(name, phoneNumber, section);
        numOfStudents++;

        DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());
        mStudentRef.setValue(student);
    }
    public static void deleteStudentFromDatabase(Student student) {
        mStudentsRef.child(student.getUid()).removeValue();
    }
    public static void editStudentOnDatabase(String name, String phoneNumber, String section, String uid) {
        Student student = getStudent(uid);
        String oldSection = student.getSection();

        student.setName(name);
        student.setPhoneNumber(phoneNumber);
        student.setSection(section);

        int index = students.indexOf(student);
        students.set(index, student);

        boolean temp = false;
        if (oldSection.equals(classNames[0])) {
            temp = classOne.remove(student);
        } else if (oldSection.equals(classNames[1])) {
            temp = classTwo.remove(student);
        } else if (oldSection.equals(classNames[2])) {
            temp = classThree.remove(student);
        } else if (oldSection.equals(classNames[3])) {
            temp = classFour.remove(student);
        } else if (oldSection.equals(classNames[4])) {
            temp = classFive.remove(student);
        } else {
            Log.d(TAG, "Wrong Section");
        }

        if (section.equals(classNames[0])) {
            classOne.add(student);
        } else if (section.equals(classNames[1])) {
            classTwo.add(student);
        } else if (section.equals(classNames[2])) {
            classThree.add(student);
        } else if (section.equals(classNames[3])) {
            classFour.add(student);
        } else if (section.equals(classNames[4])) {
            classFive.add(student);
        } else {
            Log.d(TAG, "Wrong Section");
        }

        DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());
        mStudentRef.setValue(student);

    }

    public static Student getStudent(String uid) {
        for (Student student : students) {
            if (student.getUid().equals(uid)) {
                return student;
            }
        }
        return null;
    }

    public static void sendMessage(Student student, studentStatus status) {
        Log.d(TAG, "SENDMESSAGE: " + student.getName() + " is " + status);
    }



    // Firebase real-time database
    private void attachDatabaseReadListener() {
        if (mStudentEventListener == null) {
            mStudentEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Student student = dataSnapshot.getValue(Student.class);
                    students.add(student);
                    String section = student.getSection();

                    if (section.equals(classNames[0])) {
                        classOne.add(student);
                    } else if (section.equals(classNames[1])) {
                        classTwo.add(student);
                    } else if (section.equals(classNames[2])) {
                        classThree.add(student);
                    } else if (section.equals(classNames[3])) {
                        classFour.add(student);
                    } else if (section.equals(classNames[4])) {
                        classFive.add(student);
                    } else {
                        Log.d(TAG, "Wrong Section");
                    }

                    mViewPagerAdapter.notifyDataSetChanged();
//                    mStudentAdapter.add(student);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    mViewPagerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Student student = dataSnapshot.getValue(Student.class);
                    String section = student.getSection();
                    Log.d(TAG, "student: " + student);

                    boolean temp = false;

                    if (section.equals(classNames[0])) {
                        temp = classOne.remove(student);
                    } else if (section.equals(classNames[1])) {
                        temp = classTwo.remove(student);
                    } else if (section.equals(classNames[2])) {
                        temp = classThree.remove(student);
                    } else if (section.equals(classNames[3])) {
                        temp = classFour.remove(student);
                    } else if (section.equals(classNames[4])) {
                        temp = classFive.remove(student);
                    } else {
                        Log.d(TAG, "Wrong Section");
                    }
                    Log.d(TAG, "asd: " + temp);
                    temp = students.remove(student);
                    Log.d(TAG, "asd: " + temp);
                    mViewPagerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mStudentsRef.addChildEventListener(mStudentEventListener);
        }

        if (mCustomMessagesEventListener == null) {
            mCustomMessagesEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().equals("arrival")) {
                        arrivalMessage = dataSnapshot.getValue().toString();
                    } else if (dataSnapshot.getKey().equals("departure")) {
                        departureMessage = dataSnapshot.getValue().toString();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().equals("arrival")) {
                        arrivalMessage = dataSnapshot.getValue().toString();
                    } else if (dataSnapshot.getKey().equals("departure")) {
                        departureMessage = dataSnapshot.getValue().toString();
                    }
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
            mCustomMessagesRef.addChildEventListener(mCustomMessagesEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mStudentEventListener != null) {
            mStudentsRef.removeEventListener(mStudentEventListener);
            mStudentEventListener = null;
        }
        if (mCustomMessagesEventListener != null) {
            mCustomMessagesRef.removeEventListener(mCustomMessagesEventListener);
        }
    }

    // Firebase log out
    private void onSignedOutCleanup() {
//        mStudentAdapter.clear();
        detachDatabaseReadListener();
        clearLists();
    }

    private void clearLists() {
        classOne.clear();
        classTwo.clear();
        classThree.clear();
        classFour.clear();
        classFive.clear();
        students.clear();
    }

    // debug
    public void printStatus() {
        String temp = "DEBUG_STATUS";
        Log.d(temp, "Printing the current status");
        Log.d(temp, "students list: ");
        for (Student student : students) {
            Log.d(temp, student.toString());
        }

        Log.d(temp, "classOne list: ");
        for (Student student : classOne) {
            Log.d(temp, student.toString());
        }
        Log.d(temp, "classTwo list: ");
        for (Student student : classTwo) {
            Log.d(temp, student.toString());
        }
        Log.d(temp, "classThree list: ");
        for (Student student : classThree) {
            Log.d(temp, student.toString());
        }
        Log.d(temp, "classFour list: ");
        for (Student student : classFour) {
            Log.d(temp, student.toString());
        }
        Log.d(temp, "classFive list: ");
        for (Student student : classFive) {
            Log.d(temp, student.toString());
        }

        Log.d(temp, arrivalMessage);
        Log.d(temp, departureMessage);
    }


    // Message
    private void requestMessagePermission() {
        String permission = "android.permission.SEND_SMS";
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    public void sendMessage(String message, Student student) {
        // sending sms
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(student.getPhoneNumber(), null, message, null, null);
            Toast.makeText(this, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
