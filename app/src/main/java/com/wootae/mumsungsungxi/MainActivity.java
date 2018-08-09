package com.wootae.mumsungsungxi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddStudentDialog.AddStudentListener, StudentAdapter.EditStudentRequestListener, EditStudentDialog.EditStudentListener {
    // Debug
    private static final String TAG = "MainActivity_Debug";

    // Static variables
    public static final int NUM_OF_CLASSES = 5;
    public static final int RC_PHOTO_PICKER_ADD = 1;
    public static final int RC_PHOTO_PICKER_EDIT = 1;

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


    // Firebase auth & real-time database
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 123;
    private static final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private static final DatabaseReference mStudentsRef = mRootRef.child("students");
    public static DatabaseReference mCustomMessagesRef = mRootRef.child("customMessages");

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
    Student currentStudent;

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
        messageDialog editStudentDialog = new messageDialog();

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
    public void addStudentToDatabase(final String name, final String phoneNumber, final String section, Uri pictureUri) {
        if (pictureUri == null) {
            Student student = new Student(name, phoneNumber, section, "");
            DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());

            mStudentRef.setValue(student);
            numOfStudents++;
        } else {
            // profile picture included
            // compress the image
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            final Student student = new Student(name, phoneNumber, section);

            StorageReference profilePictureRef = mProfilePictureStorageReference.child(student.getUid());
            profilePictureRef.putBytes(fileInBytes).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
//                    Student student = new Student(name, phoneNumber, section, downloadUri.toString());

                    student.setPictureUri(downloadUri.toString());
                    DatabaseReference mStudentRef = mStudentsRef.child(student.getUid());

                    mStudentRef.setValue(student);
                    numOfStudents++;
                }
            });
        }
    }
    public static void deleteStudentFromDatabase(Student student) {
        mStudentsRef.child(student.getUid()).removeValue();
    }
    public void editStudentOnDatabase(String name, String phoneNumber, String section, final String uid, Uri pictureUri) {
        if (pictureUri == null) {
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
        } else {
            // upload picture
            final Student student = getStudent(uid);

            Log.d(TAG, "EDITSTUDENTONDATBASE: " + pictureUri);
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();

            StorageReference profilePictureRef = mProfilePictureStorageReference.child(student.getUid());
            profilePictureRef.putBytes(fileInBytes).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    student.setPictureUri(downloadUri.toString());
                    Log.d(TAG, "TESTING***: " +  downloadUri);
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "TESTING***: failed");
                    e.printStackTrace();
                }
            }).addOnPausedListener(this, new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, "TESTING***3: " +  downloadUri);
                }
            }).addOnCompleteListener(this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Uri downloadUri = task.getResult().getDownloadUrl();
                    Log.d(TAG, "TESTING***4: " +  downloadUri);
                }
            }).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "uploaded: " + progress);
                }
            });

//            Log.d(TAG, "TESTING***2: " +  downloadUri);

            String oldSection = student.getSection();

            student.setName(name);
            student.setPhoneNumber(phoneNumber);
            student.setSection(section);
//            student.setPictureUri(downloadUri.toStrin

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

                    mStudentAdapterOne.notifyDataSetChanged();
                    mStudentAdapterTwo.notifyDataSetChanged();
                    mStudentAdapterThree.notifyDataSetChanged();
                    mStudentAdapterFour.notifyDataSetChanged();
                    mStudentAdapterFive.notifyDataSetChanged();
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
