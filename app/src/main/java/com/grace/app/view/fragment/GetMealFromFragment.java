package com.grace.app.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.grace.app.R;
import com.grace.app.constants.Constants;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by varsovski on 24-Dec-16.
 */

public class GetMealFromFragment extends BaseFragment<GetMealFromPresenter, GetMealFromView> implements GetMealFromView {

    @Inject
    PresenterFactory<GetMealFromPresenter> mPresenterFactory;
    @Inject
    SharedPreferences mSharedPreferences;

    private boolean disclaimerIsShown = false;

    Unbinder unbinder;
    @BindView(R.id.capture_meal_linear_layout)
    RelativeLayout mCaptureMealRelativeLayout;
    @BindView(R.id.from_gallery_linear_layout)
    RelativeLayout mFromGalleryRelativeLayout;


    public GetMealFromFragment() {
    }

    public static GetMealFromFragment newInstance() {
        return new GetMealFromFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_meal_from, container, false);
        unbinder = ButterKnife.bind(this, view);
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

    @OnClick({R.id.capture_meal_linear_layout, R.id.from_gallery_linear_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.capture_meal_linear_layout:
                ((Main2Activity) getActivity()).startExternalCamera();
                break;
            case R.id.from_gallery_linear_layout:
                ((Main2Activity) getActivity()).getPhotosFromGallery();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /* Getters and Setters */
    @NonNull
    @Override
    protected PresenterFactory<GetMealFromPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
