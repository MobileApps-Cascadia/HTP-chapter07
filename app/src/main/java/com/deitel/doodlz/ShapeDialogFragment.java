package com.deitel.doodlz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ShapeDialogFragment extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle bundle)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_shape_dialog);
        builder.setCancelable(true);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();

        builder.setPositiveButton(R.string.button_shape_line,
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    doodleView.setShape(DoodleView.DrawingMode.LINE);
                }
            }
        );

        builder.setNeutralButton(R.string.button_shape_oval,
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    doodleView.setShape(DoodleView.DrawingMode.OVAL);
                }
            }
        );

        builder.setNegativeButton(R.string.button_shape_rect,
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    doodleView.setShape(DoodleView.DrawingMode.RECT);
                }
            }
        );

        return builder.create();
    }

    private DoodleFragment getDoodleFragment()
    {
        return (DoodleFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }
}
