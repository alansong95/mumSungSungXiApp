package com.wootae.mumsungsungxi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AddStudentDialog.AddStudentListener, StudentAdapter.EditStudentRequestListener, EditStudentDialog.EditStudentListener, AnnouncementDialog.AnnouncementListener {
    // Debug
    private static final String TAG = "MainActivity_Debug";

    private static Context mContext;

    // Static variables
    public static final int NUM_OF_CLASSES = 5;
    public static final int RC_PHOTO_PICKER_ADD = 1;
    public static final int RC_PHOTO_PICKER_EDIT = 1;

    // Hakwon
    private static int numOfStudents = 0; // decide this or static variable in Student class
    public static List<Student> students;
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


    // Firebase auth & real-time database
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 123;
    private static final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static final DatabaseReference mStudentsRef = mRootRef.child("students");
    public static DatabaseReference mCustomMessagesRef = mRootRef.child("customMessages");
    private static DatabaseReference mAttendanceRef = mRootRef.child("attendance");

    // Firebase storage
    public static FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    public static StorageReference mProfilePictureStorageReference = mFirebaseStorage.getReference().child("profilePictures");

    // Firebase 2
    private ChildEventListener mStudentEventListener;
    private ChildEventListener mCustomMessagesEventListener;

    // Layout
    TabLayout mTabLayout;
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;

    //
    public static ProgressBar mProgressBar;

    // AddStudentDialog Listener
    public void addNewStudent(String[] studentData, Uri pictureUri) {
        addStudentToDatabase(studentData[0], studentData[1], studentData[2], pictureUri);
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
        MessageDialog editStudentDialog = new MessageDialog();

        Bundle args = new Bundle();
        args.putString("uid", student.getUid());
        editStudentDialog.setArguments(args);

        editStudentDialog.show(getSupportFragmentManager(), "");
//        editStudentDialog.setCancelable(false);
    }

    // EditStudentDialog Listener
    public void editStudent(String[] studentData, Uri pictureUri) {
        editStudentOnDatabase(studentData[0], studentData[1], studentData[2], studentData[3], pictureUri);
    }

    // Announcement Listener
    @Override
    public void sendAnnouncement(List<Student> recipients, String message) {
        Log.d(TAG, "Sending Announcement");
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "To: ");
        for (Student student : recipients) {
            Log.d(TAG, student.toString());
        }

        for (Student student : recipients) {
            sendMessage(student, message);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mProgressBar = findViewById(R.id.progress_bar);

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
        mProgressBar.setVisibility(ProgressBar.GONE);
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
//            case R.id.test:
////                printStatus();
//                testing();
//                return true;
            case R.id.announcement:
                AnnouncementDialog announcementDialog = new AnnouncementDialog();
                announcementDialog.show(getSupportFragmentManager(), "");
                return true;
            case R.id.analysis:
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayListExtra();
                MainActivity.this.startActivity(new Intent(MainActivity.this, AnalysisActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Hakwon methods
    public void addStudentToDatabase(final String name, final String phoneNumber, final String section, Uri pictureUri) {
        if (pictureUri == null) {
            Student student = new Student(name, phoneNumber, section, "");
            DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());

            mStudentRef.setValue(student);
            numOfStudents++;
        } else {
            // profile picture included
            // compress the image
            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);

                bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            final Student student = new Student(name, phoneNumber, section);

            StorageReference profilePictureRef = mProfilePictureStorageReference.child(student.getUid());
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            profilePictureRef.putBytes(fileInBytes).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
//                    Student student = new Student(name, phoneNumber, section, downloadUri.toString());

                    student.setPictureUri(downloadUri.toString());
                    DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());

                    mStudentRef.setValue(student);
                    numOfStudents++;
                    mProgressBar.setVisibility(ProgressBar.GONE);
                }
            });
        }
    }
    public static void deleteStudentFromDatabase(Student student) {
        mStudentsRef.child(student.getUid()).removeValue();

        mAttendanceRef.child(student.getUid()).removeValue();
    }
    public void editStudentOnDatabase(final String name, final String phoneNumber, final String section, final String uid, Uri pictureUri) {
        // change on attendance
        final Student student = getStudent(uid);
        Log.d("TESTING137", "student name: " + student.getName());

        if (pictureUri == null) {
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
                classOne.sort(new StudentComparer());
            } else if (section.equals(classNames[1])) {
                classTwo.add(student);
                classTwo.sort(new StudentComparer());
            } else if (section.equals(classNames[2])) {
                classThree.add(student);
                classThree.sort(new StudentComparer());
            } else if (section.equals(classNames[3])) {
                classFour.add(student);
                classFour.sort(new StudentComparer());
            } else if (section.equals(classNames[4])) {
                classFive.add(student);
                classFive.sort(new StudentComparer());
            } else {
                Log.d(TAG, "Wrong Section");
            }

            DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());
            mStudentRef.setValue(student);
        } else {
            // upload picture
            Log.d(TAG, "EDITSTUDENTONDATBASE: " + pictureUri);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);
                bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), true);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            StorageReference profilePictureRef = mProfilePictureStorageReference.child(student.getUid());
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            profilePictureRef.putBytes(fileInBytes).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    student.setPictureUri(downloadUri.toString());
                    Log.d(TAG, "TESTING***: " +  downloadUri);
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
                        classOne.sort(new StudentComparer());
                    } else if (section.equals(classNames[1])) {
                        classTwo.add(student);
                        classTwo.sort(new StudentComparer());
                    } else if (section.equals(classNames[2])) {
                        classThree.add(student);
                        classThree.sort(new StudentComparer());
                    } else if (section.equals(classNames[3])) {
                        classFour.add(student);
                        classFour.sort(new StudentComparer());
                    } else if (section.equals(classNames[4])) {
                        classFive.add(student);
                        classFive.sort(new StudentComparer());
                    } else {
                        Log.d(TAG, "Wrong Section");
                    }

                    DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());
                    mStudentRef.setValue(student);



                    mProgressBar.setVisibility(ProgressBar.GONE);
                }
            });
        }
    }

    public static Student getStudent(String uid) {
        for (Student student : students) {
            if (student.getUid().equals(uid)) {
                return student;
            }
        }
        return null;
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
                        classOne.sort(new StudentComparer());
//                        classOne.sort();
                    } else if (section.equals(classNames[1])) {
                        classTwo.add(student);
                        classTwo.sort(new StudentComparer());
                    } else if (section.equals(classNames[2])) {
                        classThree.add(student);
                        classThree.sort(new StudentComparer());
                    } else if (section.equals(classNames[3])) {
                        classFour.add(student);
                        classFour.sort(new StudentComparer());
                    } else if (section.equals(classNames[4])) {
                        classFive.add(student);
                        classFive.sort(new StudentComparer());
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

                    StorageReference deleteRef = mProfilePictureStorageReference.child(student.getUid());
                    deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

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
            mCustomMessagesEventListener = null;
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
        String permission2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        int grant = ContextCompat.checkSelfPermission(this, permission);
        int grant2 = ContextCompat.checkSelfPermission(this, permission2);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
        if (grant2 != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission2;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    public static void studentAction(Student student, String status) {
        Log.d(TAG, "SENDMESSAGE: " + student.getName() + " is " + status);

        DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DatabaseReference mStudentAttendanceRef = mAttendanceRef.child(student.getUid()).child(date);

        if (status.equals(StudentStatus.ARRIVED)) {
            System.out.println(date);

            student.setStatus(StudentStatus.ARRIVED);
            student.setUpdatedDate(date);
            mStudentRef.setValue(student);
            mStudentAttendanceRef.setValue(StudentStatus.ATTENDED);

            sendMessage(student, arrivalMessage);
            // update for analysis
        } else if (status.equals(StudentStatus.DEPARTED)) {
            //                sendMessage(student, 하원);
            // update for analysis

            student.setStatus(StudentStatus.DEPARTED);
            student.setUpdatedDate(date);
            mStudentRef.setValue(student);
            mStudentAttendanceRef.setValue(StudentStatus.ATTENDED);

            sendMessage(student, departureMessage);
        } else if (status.equals(StudentStatus.ABSENT)) {
            // update for analysis

            student.setStatus(StudentStatus.ABSENT);
            student.setUpdatedDate(date);
            mStudentRef.setValue(student);
            mStudentAttendanceRef.setValue(StudentStatus.ABSENT);
        }

//        switch (status) {
//            case StudentStatus.ARRIVED:
//                System.out.println(date);
//
//                student.setStatus(StudentStatus.ARRIVED);
//                student.setUpdatedDate(date);
//                mStudentRef.setValue(student);
//                mStudentAttendanceRef.setValue(StudentStatus.ATTENDED);
//
////                sendMessage(student, 등원메세지);
//                // update for analysis
//                break;
//            case DEPARTED:
////                sendMessage(student, 하원);
//                // update for analysis
//
//                student.setStatus(StudentStatus.DEPARTED);
//                student.setUpdatedDate(date);
//                mStudentRef.setValue(student);
//                mStudentAttendanceRef.setValue(StudentStatus.ATTENDED);
//
//                break;
//            case ABSENT:
//                // update for analysis
//
//                student.setStatus(StudentStatus.ABSENT);
//                student.setUpdatedDate(date);
//                mStudentRef.setValue(student);
//                mStudentAttendanceRef.setValue(StudentStatus.ABSENT);
//
//                break;
//        }
    }

//    // place holder for now
//    public void sendMessage(Student student, String message) {
//        Log.d(TAG, message + " sent to: " + student.getName() + " " + student.getPhoneNumber());
//    }

    public static void sendMessage(Student student, String message) {
        //        Log.d(TAG, message + " sent to: " + student.getName() + " " + student.getPhoneNumber());

        message =  message.replace("ㅇㅇㅇ", student.getName());

        // sending sms
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(student.getPhoneNumber(), null, message, null, null);
            Toast.makeText(mContext, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(mContext,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void testing() {
        LocalDate startDate = LocalDate.of(2018, 5, 1);
        LocalDate today = LocalDate.now();

        Random random = new Random();
        String value = "";

        for (Student student : students) {
            LocalDate date = startDate;

            DatabaseReference ref = mAttendanceRef.child(student.getUid());

            while (date.compareTo(today) != 0) {
                if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    date = date.plusDays(1);
                    continue;
                } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    if (random.nextFloat() <= 0.50f) {
                        value = "ATTENDED";
                    } else {
                        date = date.plusDays(1);
                        continue;
                    }
                } else {
                    if (random.nextFloat() <= 0.10f) {
                        value = "ABSENT";
                    } else {
                        value = "ATTENDED";
                    }
                }
                ref.child(date.toString()).setValue(value);
                date = date.plusDays(1);
            }
        }
    }
}
