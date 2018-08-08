package com.wootae.mumsungsungxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Alan on 8/8/2018.
 */

public class EditMessagesDialog extends DialogFragment {
    private EditText etArrivalMessage;
    private EditText etDepatureMessage;
    Button btnConfirm;
    Button btnRestore;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_messages, null);

        etArrivalMessage = view.findViewById(R.id.arrived_message);
        etDepatureMessage = view.findViewById(R.id.left_message);
        btnConfirm = view.findViewById(R.id.btn_custom_message);
        btnRestore = view.findViewById(R.id.btn_restore_message);

        etArrivalMessage.setText(MainActivity.arrivalMessage);
        etDepatureMessage.setText(MainActivity.departureMessage);

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etArrivalMessage.setText(getString(R.string.default_arrival));
                etDepatureMessage.setText(getString(R.string.default_departure));
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mCustomMessagesRef.child("arrival").setValue(etArrivalMessage.getText().toString());
                MainActivity.mCustomMessagesRef.child("departure").setValue(etDepatureMessage.getText().toString());

                Toast.makeText(getActivity(), R.string.alert_custom_messages_set, Toast.LENGTH_LONG).show();
                dismiss();
            }
        });

        builder.setView(view);
        builder.setTitle(R.string.edit_messages);

        return builder.create();
    }
}
