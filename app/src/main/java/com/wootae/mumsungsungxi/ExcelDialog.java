package com.wootae.mumsungsungxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDate;


/**
 * Created by Alan on 8/14/2018.
 */

public class ExcelDialog extends DialogFragment {
    public interface ExcelListener {
        void createExcel(int year, int month);
    }

    private EditText etYear;
    private EditText etMonth;
    Button btnConfirm;
    Button btnCancel;

    private String monthStr;
    private String yearStr;
    private int month;
    private int year;

    private ExcelListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (ExcelDialog.ExcelListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ExcelListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_excel, null);

        etYear = view.findViewById(R.id.et_excel_year);
        etMonth = view.findViewById(R.id.et_excel_month);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnCancel = view.findViewById(R.id.btn_cancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (etYear.getText().toString().trim().equals("")) {
                Toast.makeText(getActivity(), "연도를 입력해 주세요.", Toast.LENGTH_SHORT).show();

            } else {
                year = Integer.parseInt(etYear.getText().toString().trim());

                if (etMonth.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "월을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    month = Integer.parseInt(etMonth.getText().toString().trim());

                    LocalDate from = LocalDate.of(year, month, 1);
                    LocalDate today = LocalDate.now();

                    if (month < 1 || month > 12) {
                        Toast.makeText(getActivity(), "월을 바르게 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                    if (year < 2018 || year > 2100) {
                        Toast.makeText(getActivity(), "연도르 바르게 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                    if (today.compareTo(from) < 0) {
                        Toast.makeText(getActivity(), "입력값이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else if (month > 0 && month < 13 && year > 2017 && year < 2100) {
                        mListener.createExcel(year, month);
                        dismiss();
                    }
                }
            }


//                name = etName.getText().toString().trim();
//                phoneNumber = etPhoneNumber.getText().toString().trim();
////                email = etEmail.getText().toString();
//                section = spinnerSection.getSelectedItem().toString();
//
//                if (TextUtils.isEmpty(name)) {
//                    Toast.makeText(getActivity().getApplicationContext(), R.string.enter_name, Toast.LENGTH_SHORT).show();
//                } else if (TextUtils.isEmpty(phoneNumber)) {
//                    Toast.makeText(getActivity().getApplicationContext(), R.string.enter_number, Toast.LENGTH_SHORT).show();
//                } else if (section.equals("섹션 선택")) {
//                    Toast.makeText(getActivity().getApplicationContext(), R.string.select_section, Toast.LENGTH_SHORT).show();
//                } else {
//                    String[] studentData = new String[3];
//                    studentData[0] = name;
//                    studentData[1] = phoneNumber;
////                    studentData[2] = email;
//                    studentData[2] = section;
//
//                    mListener.addNewStudent(studentData, mImageUri);
//                    dismiss();
//                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.edit_messages);

        return builder.create();
    }
}
