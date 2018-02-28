package com.grace.app.view.helper;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.grace.app.model.MealPhoto;
import com.grace.app.view.dialog.DisclaimerTermsAndConditionsDialogFragment;
import com.grace.app.view.fragment.GetMealFromFragment;
import com.grace.app.view.fragment.LoadingFragment;
import com.grace.app.view.fragment.PhotoFragment;

/**
 * Created by varsovski on 21-Aug-16.
 */
public class ChangeFragmentHelper {

    public static final String GET_MEAL_FROM_FRAGMENT = "getMealFromFragment";
    public static final String PHOTO_FRAGMENT = "photoFragment";
    public static final String TNC_DIALOG = "tncDialogFragment";
    public static final String LOADING_FRAGMENT = "loadingFragment";


    public static void setGetMealFromFragment(AppCompatActivity a, int container) {
        if (a != null) {
            FragmentTransaction ft = a.getSupportFragmentManager().beginTransaction();
            GetMealFromFragment fragment = GetMealFromFragment.newInstance();
            ft.replace(container, fragment, GET_MEAL_FROM_FRAGMENT);
            ft.commit();
        }

    }

    public static void setLoadingFragment(AppCompatActivity a, int container, String uri, int loadingMessage) {
        if (a != null) {
            FragmentTransaction ft = a.getSupportFragmentManager().beginTransaction();
            LoadingFragment fragment = LoadingFragment.newInstance(uri, loadingMessage);
            ft.replace(container, fragment, LOADING_FRAGMENT);
            ft.commit();
        }

    }

    public static void setPhotoFragment(AppCompatActivity a, int container, MealPhoto mealPhoto) {
        if (a != null) {
            FragmentTransaction ft = a.getSupportFragmentManager().beginTransaction();
            PhotoFragment fragment = PhotoFragment.newInstance(mealPhoto);
            ft.replace(container, fragment, PHOTO_FRAGMENT);
            ft.commit();
        }

    }

    public static void showTnCDialogFragment(AppCompatActivity activity) {
        new DisclaimerTermsAndConditionsDialogFragment().show(activity.getSupportFragmentManager(), TNC_DIALOG);
    }


}
