package com.wootae.mumsungsungxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 8/10/2018.
 */

public class AnnouncementDialog extends DialogFragment {
    public interface AnnouncementListener {
        void sendAnnouncement(List<Student> recipients, String message);
    }

    private AnnouncementListener mListener;

    private Spinner spinnerTo;
    private MultiSelectionSpinner spinnerMultiTo;
    private EditText etAnnouncement;
    private Button btnConfirm;
    private Button btnCancel;

    private String recipient;
    private String message;
    private List<Student> recipients;

    private String[] spinnerToDropdown;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_announcement, null);

        recipients = new ArrayList<>();

        spinnerTo = view.findViewById(R.id.spinner_to);
        etAnnouncement = view.findViewById(R.id.et_announcement);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnCancel = view.findViewById(R.id.btn_cancel);
        spinnerMultiTo = view.findViewById(R.id.spinner_multi_to);

        List<Student> students = MainActivity.students;
        students.sort(new StudentComparer());

        spinnerMultiTo.setItems(students);

        spinnerToDropdown = getResources().getStringArray(R.array.spinner_to_dropdown);

        etAnnouncement.setText(R.string.announcement_pre);

        // Spinner set up
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerToDropdown);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(spinnerAdapter);

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("ANNOUNCEMENT2", "x: " + position + " " + id);
                if (position == 7) { // 개별 선택
                    spinnerMultiTo.setVisibility(View.VISIBLE);
                } else {
                    spinnerMultiTo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });




        // Confirm button
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = etAnnouncement.getText().toString();
                recipient = spinnerTo.getSelectedItem().toString();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.enter_message, Toast.LENGTH_SHORT).show();
                } else if (recipient.equals(spinnerToDropdown[0])) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.select_recipient, Toast.LENGTH_SHORT).show();
                } else {
                    if (recipient.equals(spinnerToDropdown[1])) { //전체
                        mListener.sendAnnouncement(MainActivity.students, message);
                    } else if (recipient.equals(spinnerToDropdown[2])) { // 1교시
                        mListener.sendAnnouncement(MainActivity.classOne, message);
                    } else if (recipient.equals(spinnerToDropdown[3])) { // 2교시
                        mListener.sendAnnouncement(MainActivity.classTwo, message);
                    } else if (recipient.equals(spinnerToDropdown[4])) { // 3교시
                        mListener.sendAnnouncement(MainActivity.classThree, message);
                    } else if (recipient.equals(spinnerToDropdown[5])) { // 4교시
                        mListener.sendAnnouncement(MainActivity.classFour, message);
                    } else if (recipient.equals(spinnerToDropdown[6])) { // 5교시
                        mListener.sendAnnouncement(MainActivity.classFive, message);
                    } else if (recipient.equals(spinnerToDropdown[7])) { // 개별선택
                        List<Integer> indicies = spinnerMultiTo.getSelectedIndicies();
                        for (int i : indicies) {
                            recipients.add(MainActivity.students.get(i));
                        }
                        mListener.sendAnnouncement(recipients, message);
                    }
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


//        etArrivalMessage = view.findViewById(R.id.arrived_message);
//        etDepatureMessage = view.findViewById(R.id.left_message);
//        btnConfirm = view.findViewById(R.id.btn_custom_message);
//        btnRestore = view.findViewById(R.id.btn_restore_message);
//
//        etArrivalMessage.setText(MainActivity.arrivalMessage);
//        etDepatureMessage.setText(MainActivity.departureMessage);
//
//        btnRestore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                etArrivalMessage.setText(getString(R.string.default_arrival));
//                etDepatureMessage.setText(getString(R.string.default_departure));
//            }
//        });
//
//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MainActivity.mCustomMessagesRef.child("arrival").setValue(etArrivalMessage.getText().toString());
//                MainActivity.mCustomMessagesRef.child("departure").setValue(etDepatureMessage.getText().toString());
//
//                Toast.makeText(getActivity(), R.string.alert_custom_messages_set, Toast.LENGTH_LONG).show();
//                dismiss();
//            }
//        });

        builder.setView(view);
        builder.setTitle(R.string.announcement);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (AnnouncementListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AnnouncementListener");
        }
    }
}
