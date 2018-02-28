package com.grace.app.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.grace.app.constants.Constants;
import com.grace.app.view.fragment.DisclaimerTermsAndConditionsFragment;

/**
 * Created by varsovski on 25-May-17.
 */

public class DisclaimerTncViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;

    public DisclaimerTncViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        DisclaimerTermsAndConditionsFragment fragment = new DisclaimerTermsAndConditionsFragment();
        String url = "";
        if (position == 0)
            url = Constants.DISCLAIMER_URL;
        else
            url = Constants.TNC_URL;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DISCLAIMER_TNC_KEY, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
