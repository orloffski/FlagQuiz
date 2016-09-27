package com.deitel.flagquiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by orlof on 27.09.2016.
 */

public class FinishDialogFragment extends DialogFragment {
    private OnResetQuizListener callback;

    public interface OnResetQuizListener{
        void onResetQuiz();
    }

    static FinishDialogFragment newInstance(String message){
        FinishDialogFragment dialogFragment = new FinishDialogFragment();

        Bundle args = new Bundle();
        args.putString("message", message);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            callback = (OnResetQuizListener) getTargetFragment();
        }catch (ClassCastException e){
            throw new ClassCastException("Calling Fragment must implement OnResetQuizListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onResetQuiz();
                    }
                });
        return builder.create();
    }
}
