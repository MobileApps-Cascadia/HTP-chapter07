package com.deitel.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Created by Hiromi on 10/22/2015.
 */
public class BackgroundDialogFragment extends DialogFragment {
    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View colorView;
    private int color;

    // create an AlertDialog and return it
    @Override
    public Dialog onCreateDialog(Bundle bundle)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        final View backgroundColorDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_background_color, null);
        builder.setView(backgroundColorDialogView); // add GUI to dialog

        // set the AlertDialog's message
        builder.setTitle(R.string.title_background_color_dialog);
        builder.setCancelable(true);

        // get the color SeekBars and set their onChange listeners
        alphaSeekBar = (SeekBar) backgroundColorDialogView.findViewById(
                R.id.alphaSeekBar);
        redSeekBar = (SeekBar) backgroundColorDialogView.findViewById(
                R.id.redSeekBar);
        greenSeekBar = (SeekBar) backgroundColorDialogView.findViewById(
                R.id.greenSeekBar);
        blueSeekBar = (SeekBar) backgroundColorDialogView.findViewById(
                R.id.blueSeekBar);
        colorView = backgroundColorDialogView.findViewById(R.id.colorView);

        // register SeekBar event listeners
        alphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        redSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        // use current drawing color to set SeekBar values
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color = doodleView.getBackgroundColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        // add Set Color Button
        builder.setPositiveButton(R.string.button_set_color,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        getDoodleFragment().getDoodleView().setBackgroundColor(color);
                    }
                }
        ); // end call to setPositiveButton

        return builder.create(); // return dialog
    } // end method onCreateDialog

    // gets a reference to the DoodleFragment
    private DoodleFragment getDoodleFragment()
    {
        return (DoodleFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    // tell DoodleFragment that dialog is now displayed
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        DoodleFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    // tell DoodleFragment that dialog is no longer displayed
    @Override
    public void onDetach()
    {
        super.onDetach();
        DoodleFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }

    // OnSeekBarChangeListener for the SeekBars in the color dialog
    private OnSeekBarChangeListener colorChangedListener =
            new OnSeekBarChangeListener()
            {
                // display the updated color
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser)
                {
                    if (fromUser) // user, not program, changed SeekBar progress
                        color = Color.argb(alphaSeekBar.getProgress(),
                                redSeekBar.getProgress(), greenSeekBar.getProgress(),
                                blueSeekBar.getProgress());
                    colorView.setBackgroundColor(color);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) // required
                {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) // required
                {
                }
            }; // end colorChanged
}// end class ColorDialogFragment
