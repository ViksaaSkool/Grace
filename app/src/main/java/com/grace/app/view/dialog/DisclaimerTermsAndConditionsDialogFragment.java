package com.grace.app.view.dialog;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.grace.app.GraceApplication;
import com.grace.app.R;
import com.grace.app.adapter.DisclaimerTncViewPagerAdapter;
import com.grace.app.constants.Constants;
import com.grace.app.databinding.DialogDisclaimerTncBinding;

import javax.inject.Inject;

/**
 * Created by varsovski on 21-May-17.
 */

public class DisclaimerTermsAndConditionsDialogFragment extends BaseDialogFragment {

    private DialogDisclaimerTncBinding mBinding;
    private TextView mTitleTextView;
    private TabLayout mDotsTabLayout;
    private AppCompatCheckBox mTermsAndConditionsCheckBox;
    private TextView mActionTextView;
    private ViewPager mContentViewPager;

    @Inject
    SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DialogDisclaimerTncBinding.inflate(inflater, container, false);
        View v = mBinding.getRoot();
        GraceApplication.getAppComponent().inject(this);
        mTitleTextView = mBinding.titleTextView;
        mDotsTabLayout = mBinding.dotsTabLayout;
        mTermsAndConditionsCheckBox = mBinding.termsAndConditionsCheckBox;
        mActionTextView = mBinding.actionTextView;
        mContentViewPager = mBinding.contentViewPager;
        mBinding.exitAppTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClicked(view);
            }
        });
        mBinding.actionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewClicked(view);
            }
        });
        return v;
    }

    @Override
    public void initUI() {

        //viewpager
        if (mContentViewPager != null) {
            DisclaimerTncViewPagerAdapter adapterViewPager = new DisclaimerTncViewPagerAdapter(getChildFragmentManager());
            mContentViewPager.setAdapter(adapterViewPager);
            mDotsTabLayout.setupWithViewPager(mContentViewPager, true);
            mContentViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                // This method will be invoked when a new page becomes selected.
                @Override
                public void onPageSelected(int position) {
                }

                // This method will be invoked when the current page is scrolled
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    handlePagerPositionChange(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        //checkbox
        if (mTermsAndConditionsCheckBox != null) {
            mTermsAndConditionsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mActionTextView.setEnabled(true);
                    } else {
                        mActionTextView.setEnabled(false);
                    }
                }
            });
        }
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.exit_app_text_view) {
            getActivity().finish();
        } else if (id == R.id.action_text_view) {
            //Next
            if (mActionTextView.getText()
                    .toString().equals(getString(R.string.next_step_text))) {
                mActionTextView.setText(R.string.enter_app_text);
                setNextArrow(false);
                mContentViewPager.setCurrentItem(1);
            }
            //OK
            else {
                mSharedPreferences.edit()
                        .putBoolean(Constants.DISCLAIMER_TNC_KEY, true).apply();
                dismiss();
            }
        }
    }


    private void setNextArrow(boolean flag) {
        Drawable nextDrawable = null;
        if (flag)
            nextDrawable = AppCompatResources.getDrawable(getActivity(), R.drawable.ic_next_wrapper);
        else
            nextDrawable = AppCompatResources.getDrawable(getActivity(), R.drawable.ic_dummy_next_wrapper);
        mActionTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, nextDrawable, null);
    }


    private void handlePagerPositionChange(int position) {
        if (position == 0) {
            mTitleTextView.setText(R.string.disclaimer_title);
            mActionTextView.setText(R.string.next_step_text);
            mTermsAndConditionsCheckBox.setVisibility(View.INVISIBLE);
            mActionTextView.setEnabled(true);
            setNextArrow(true);
        } else {
            mTitleTextView.setText(R.string.terms_and_conditions_title);
            mActionTextView.setText(R.string.enter_app_text);
            mTermsAndConditionsCheckBox.setVisibility(View.VISIBLE);
            setNextArrow(false);
            if (mTermsAndConditionsCheckBox.isChecked())
                mActionTextView.setEnabled(true);
            else
                mActionTextView.setEnabled(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
