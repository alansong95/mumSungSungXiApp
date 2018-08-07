package com.wootae.mumsungsungxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Alan on 8/7/2018.
 */

public class AddStudentDialog extends DialogFragment {
    public interface OnCompleteListener {
        void onComplete(String[] studentData);
    }

    private OnCompleteListener mListener;

    private EditText etName;
    private EditText etPhoneNumber;
//    private EditText etEmail;
    private Spinner spinnerSection;
    private Button btnConfirm;

    private String name;
    private String phoneNumber;
//    private String email;
    private String section;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_student, null);

        etName = (EditText) view.findViewById(R.id.et_name);
        etPhoneNumber = (EditText) view.findViewById(R.id.et_phone_number);
//        etEmail = (EditText) view.findViewById(R.id.et_email);
        spinnerSection = (Spinner)  view.findViewById(R.id.spinner_section);
        btnConfirm = view.findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                phoneNumber = etPhoneNumber.getText().toString();
//                email = etEmail.getText().toString();
//                section = spinnerSection.getSelectedItem().toString();
                section = "o";

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.enter_name, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.enter_number, Toast.LENGTH_SHORT).show();
                } else if (section.equals("선택")) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.select_section, Toast.LENGTH_SHORT).show();
                } else {
                    String[] studentData = new String[4];
                    studentData[0] = name;
                    studentData[1] = phoneNumber;
//                    studentData[2] = email;
                    studentData[3] = section;

                    mListener.onComplete(studentData);
                    dismiss();
                }
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.add_student);

        return builder.create();
    }
}


