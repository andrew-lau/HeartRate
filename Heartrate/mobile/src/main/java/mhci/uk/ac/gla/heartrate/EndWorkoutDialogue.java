package mhci.uk.ac.gla.heartrate;

/**
 * Created by Andrew on 09/03/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


/**
 * Created by Andrew on 05/01/2015.
 */
public class EndWorkoutDialogue extends DialogFragment {



    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i("EndWorkoutDialogue", "onCreateDialog");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View dialogView = inflater.inflate(R.layout.workout_dialogue, null);
        builder.setTitle("End Workout?");
        builder.setView(dialogView)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        System.out.println("Ending the workout");

                        //End the workout activity as the user has ended the workout
                        getActivity().finish();

                        Context context = getActivity().getApplicationContext();
                        CharSequence workoutEnded = "The workout has ended.";
                        int thisDuration = Toast.LENGTH_SHORT;
                        Toast thisToast = Toast.makeText(context, workoutEnded, thisDuration);
                        thisToast.show();

                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
