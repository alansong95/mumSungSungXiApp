package com.wootae.mumsungsungxi;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by Alan on 8/8/2018.
 */


public class MessageDialog extends AppCompatDialogFragment {
//    private SMSDialogListener listener;

    Button button1;
    Button button2;
    Button button3;

    String uid;

    Student student;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_message, null);

        button1 = (Button) view.findViewById(R.id.button1);
        button2 = (Button) view.findViewById(R.id.button2);
        button3 = (Button) view.findViewById(R.id.button3);

        uid = getArguments().getString("uid");
        student = MainActivity.getStudent(uid);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.arrived(uid);
                MainActivity.studentAction(student, StudentStatus.ARRIVED);
                dismiss();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.left(uid);
                MainActivity.studentAction(student, StudentStatus.DEPARTED);
                dismiss();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.absent(uid);
                MainActivity.studentAction(student, StudentStatus.ABSENT);
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            listener = (SMSDialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + "must implement SMSDialogListener");
//        }
//
//    }
//
//    public interface SMSDialogListener {
//        void arrived(String uid);
//        void left(String uid);
//        void absent(String uid);
//    }
}
