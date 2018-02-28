package com.grace.app.view.dialog;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.grace.app.GraceApplication;
import com.grace.app.R;
import com.grace.app.adapter.DisclaimerTncViewPagerAdapter;
import com.grace.app.constants.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by varsovski on 21-May-17.
 */

public class DisclaimerTermsAndConditionsDialogFragment extends BaseDialogFragment {


    @BindView(R.id.title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.dots_tab_layout)
    TabLayout mDotsTabLayout;
    @BindView(R.id.terms_and_conditions_check_box)
    AppCompatCheckBox mTermsAndConditionsCheckBox;
    @BindView(R.id.action_text_view)
    TextView mActionTextView;
    @BindView(R.id.content_view_pager)
    ViewPager mContentViewPager;
    Unbinder unbinder;

    @Inject
    SharedPreferences mSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_disclaimer_tnc, container, false);
        GraceApplication.getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this, v);
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

    @OnClick({R.id.exit_app_text_view, R.id.action_text_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exit_app_text_view:
                getActivity().finish();
                break;
            case R.id.action_text_view:
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
                break;
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
        unbinder.unbind();
    }
}
