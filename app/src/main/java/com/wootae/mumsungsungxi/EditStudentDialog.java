package com.wootae.mumsungsungxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Alan on 8/7/2018.
 */

public class EditStudentDialog extends DialogFragment {
    public interface EditStudentListener {
        void editStudent(String[] studentData, Uri pictureUri);
    }

    private EditStudentListener mListener;

//    private Student student;

    private EditText etName;
    private EditText etPhoneNumber;
//    private EditText etEmail;
    private Spinner spinnerSection;
    private Button btnConfirm;
    private Button btnCancel;


    private ImageView ivPicture;
    private Uri mImageUri;

    private String name;
    private String phoneNumber;
//    private String email;
    private String section;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (EditStudentListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditStudentListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_student, null);

        mImageUri = null;

        String uid = getArguments().getString("uid");
//        Toast.makeText(getActivity(), "uid: " + uid, Toast.LENGTH_SHORT).show();
        final Student student = MainActivity.getStudent(uid);

        etName = (EditText) view.findViewById(R.id.et_name);
        etPhoneNumber = (EditText) view.findViewById(R.id.et_phone_number);
//        etEmail = (EditText) view.findViewById(R.id.et_email);
        spinnerSection = (Spinner)  view.findViewById(R.id.spinner_section);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnCancel = view.findViewById(R.id.btn_cancel);


        ivPicture = view.findViewById(R.id.iv_picture);
        if (student.getPictureUri().equals("")) {
            Glide.with(this).load(R.drawable.default_profile_select).fitCenter().into(ivPicture);
        } else {
            Glide.with(this).load(student.getPictureUri()).signature(new StringSignature(String.valueOf(System.currentTimeMillis()))).fitCenter().into(ivPicture);
//            Glide.with(this).using(new FirebaseImageLoader()).load(MainActivity.mProfilePictureStorageReference.child(student.getUid())).into(ivPicture);
        }


        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open file chooser
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, MainActivity.RC_PHOTO_PICKER_EDIT);
            }
        });


        // Spinner set up
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.spinner_dropdown));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(spinnerAdapter);

        etName.setText(student.getName());
        etPhoneNumber.setText(student.getPhoneNumber());
//        etEmail.setText(student.getEmail());
        int spinnerPosition = spinnerAdapter.getPosition(student.getSection());
        spinnerSection.setSelection(spinnerPosition);



        // Confirm button
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString().trim();
                phoneNumber = etPhoneNumber.getText().toString().trim();
//                email = etEmail.getText().toString();
                section = spinnerSection.getSelectedItem().toString();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.enter_name, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.enter_number, Toast.LENGTH_SHORT).show();
                } else if (section.equals("섹션 선택")) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.select_section, Toast.LENGTH_SHORT).show();
                } else {
                    String[] studentData = new String[4];
                    studentData[0] = name;
                    studentData[1] = phoneNumber;
                    studentData[2] = section;
                    studentData[3] = student.getUid();

                    mListener.editStudent(studentData, mImageUri);
                    dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.edit_student);

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.RC_PHOTO_PICKER_EDIT) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                mImageUri = data.getData();

                Glide.with(this).load(mImageUri).fitCenter().into(ivPicture);
                //resize
                // or (same thing)
//                 ivPicture.setImageURI(mImageUri);
            }
        }
    }
}


