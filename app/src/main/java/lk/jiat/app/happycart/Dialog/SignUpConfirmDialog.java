package lk.jiat.app.happycart.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import lk.jiat.app.happycart.ProfileFragment;

public class SignUpConfirmDialog extends AlertDialog.Builder {
    public EditText editText2;
    private String verificationId;
    private ProfileFragment fragment;
    private Context context;

    public SignUpConfirmDialog(Context context) {
        super(context);
        init();
    }

    public SignUpConfirmDialog(ProfileFragment fragment,Context context, String verificationId) {
        super(context);
        this.verificationId=verificationId;
        this.fragment=fragment;
        init();
    }

    private void init() {
        setTitle("Please Enter Your Phone Code");
        setCancelable(true);

        // Create a layout for the dialog
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create the second EditText
        editText2 = new EditText(getContext());
        editText2.setHint("Enter Mobile Code");
        layout.addView(editText2);

        // Set the layout for the dialog
        setView(layout);

        // Set positive button
        setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle positive button click
                String code = editText2.getText().toString();
                fragment.updatePhoneNumber(code);
//                new ProfileFragment().updatePhoneNumber(code);
                // Perform actions with the entered texts
            }
        });

        // Set negative button
        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle negative button click
                dialog.dismiss();
            }
        });
    }
}

