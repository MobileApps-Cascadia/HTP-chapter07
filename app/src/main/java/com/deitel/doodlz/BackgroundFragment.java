package com.deitel.doodlz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by seanchung on 10/23/15.
 */
public class BackgroundFragment extends DialogFragment {

    private final static String TAG = "BackgroundFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View backgroundDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_background, null);

        builder.setView(backgroundDialogView); // add GUI to dialog
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        // set selected radio button
        ((RadioButton)backgroundDialogView.findViewById(doodleView.getBackgroundRadioButtonID())).setChecked(true);
        final RadioGroup radioGroup = (RadioGroup) backgroundDialogView.findViewById(R.id.radioGroup);

        // set the AlertDialog's message
        builder.setTitle(R.string.title_background_dialog);
        builder.setCancelable(true);

        builder.setPositiveButton("Set background", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int checkedRadio = radioGroup.getCheckedRadioButtonId();
                Log.d(TAG, "Selected radio id:" + checkedRadio);
                doodleView.setBackground(checkedRadio);
                doodleView.invalidate();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });

        return builder.create();
    }

    // gets a reference to the DoodleFragment
    private DoodleFragment getDoodleFragment()
    {
        return (DoodleFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

}
