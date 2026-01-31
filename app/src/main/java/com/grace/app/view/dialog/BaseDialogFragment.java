package com.grace.app.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created by varsovski on 21-May-17.
 */

public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        if (this instanceof DisclaimerTermsAndConditionsDialogFragment) {
            setCancelable(false);
        } else {
            setCancelable(true);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initDialog() {
    }


    @Override
    public void onStart() {
        super.onStart();
        setDialogDimensions(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);


    }

    public void setDialogDimensions(int width, int height) {
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(width, height);
    }


    abstract public void initUI();
}
