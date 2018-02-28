package com.grace.app.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by varsovski on 21-May-17.
 */

public abstract class BaseDialogFragment extends DialogFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDialog();
        initUI();
    }

    private void initDialog() {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            setStyle(STYLE_NO_TITLE, 0);
        }
        if (this instanceof DisclaimerTermsAndConditionsDialogFragment)
            setCancelable(false);
        else
            setCancelable(true);
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
