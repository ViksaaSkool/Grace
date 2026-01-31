package com.grace.app.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.databinding.FragmentGetMealFromBinding;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.view.DaggerGetMealFromComponent;
import com.grace.app.injection.module.view.GetMealFromModule;
import com.grace.app.presenter.GetMealFromPresenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.util.UiUtil;
import com.grace.app.view.GetMealFromView;
import com.grace.app.view.activity.Main2Activity;
import com.grace.app.view.helper.ChangeFragmentHelper;
import com.grace.app.view.impl.BaseFragment;

import javax.inject.Inject;

/**
 * Created by varsovski on 24-Dec-16.
 */

public class GetMealFromFragment extends BaseFragment<GetMealFromPresenter, GetMealFromView> implements GetMealFromView {

    @Inject
    PresenterFactory<GetMealFromPresenter> mPresenterFactory;
    @Inject
    SharedPreferences mSharedPreferences;

    private boolean disclaimerIsShown = false;

    private FragmentGetMealFromBinding mBinding;
    private RelativeLayout mCaptureMealRelativeLayout;
    private RelativeLayout mFromGalleryRelativeLayout;


    public GetMealFromFragment() {
    }

    public static GetMealFromFragment newInstance() {
        return new GetMealFromFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentGetMealFromBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        mCaptureMealRelativeLayout = mBinding.captureMealLinearLayout;
        mFromGalleryRelativeLayout = mBinding.fromGalleryLinearLayout;
        mBinding.captureMealLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked(v);
            }
        });
        mBinding.fromGalleryLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked(v);
            }
        });
        changeStatusBarColor(R.color.colorPrimaryDark);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UiUtil.setAndStartScaleAnimation(mCaptureMealRelativeLayout, 0f, 1f, Constants.SCALE_DURATION);
        UiUtil.setAndStartScaleAnimation(mFromGalleryRelativeLayout, 0f, 1f, Constants.SCALE_DURATION);
    }

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent) {
        DaggerGetMealFromComponent.builder()
                .appComponent(appComponent)
                .getMealFromModule(new GetMealFromModule())
                .build()
                .inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mSharedPreferences.getBoolean(Constants.DISCLAIMER_TNC_KEY, false)
                && !disclaimerIsShown) {
            ChangeFragmentHelper.showTnCDialogFragment((AppCompatActivity) getActivity());
            disclaimerIsShown = true;
        }

    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.capture_meal_linear_layout) {
            ((Main2Activity) getActivity()).startExternalCamera();
        } else if (id == R.id.from_gallery_linear_layout) {
            ((Main2Activity) getActivity()).getPhotosFromGallery();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }


    /* Getters and Setters */
    @NonNull
    @Override
    protected PresenterFactory<GetMealFromPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
