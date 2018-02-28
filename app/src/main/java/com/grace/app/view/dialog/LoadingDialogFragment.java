package com.grace.app.view.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grace.app.R;
import com.grace.app.constants.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by varsovski on 24-Dec-16.
 */

public class LoadingDialogFragment extends DialogFragment {

    @BindView(R.id.loading_image_view)
    ImageView mLoadingImageView;
    @BindView(R.id.loading_text_view)
    TextView mLoadingTextView;
    @BindView(R.id.root_relative_layout)
    RelativeLayout mRootRelativeLayout;

    private Handler mHandler;
    private Runnable mRunnable;


    public static LoadingDialogFragment newInstance(String message) {
        Bundle b = new Bundle();
        b.putString(Constants.LOADING_MESSAGE_KEY, message);
        LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setArguments(b);
        return loadingDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_loading, container, false);
        ButterKnife.bind(this, v);
        initUI();
        return v;
    }

    private void initUI() {
        setCancelable(false);

        //set the animation
       // AnimationUtil.setAnimationToView(Techniques.Pulse, 2000, true, mLoadingImageView);

        //set text
        String loadingMessage = getArguments().getString(Constants.LOADING_MESSAGE_KEY, "");
        if (!loadingMessage.isEmpty())
            mLoadingTextView.setText(loadingMessage);

        mHandler = new Handler();

    }

    @Override
    public void onResume() {
        // Call super onResume after sizing
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }

        setLoadingTextAnimation(mLoadingTextView);

        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHandler != null)
            mHandler.removeCallbacks(mRunnable);
    }

    private void setLoadingTextAnimation(final TextView textView) {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (textView != null) {
                    if (textView.getTag() == null)
                        textView.setTag("");

                    String dots = textView.getTag().toString();
                    String text = textView.getText().toString().substring(0, textView.getText().toString().length() - dots.length());
                    if (dots.length() == 3)
                        dots = "";
                    else
                        dots += ".";
                    textView.setTag(dots);
                    textView.setText(text + dots);
                }

                mHandler.postDelayed(mRunnable, 1000);
            }
        };
        mHandler.postDelayed(mRunnable, 1000);

    }
}
