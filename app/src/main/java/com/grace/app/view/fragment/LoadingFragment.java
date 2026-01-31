package com.grace.app.view.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.databinding.FragmentLoadingBinding;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.view.DaggerLoadingViewComponent;
import com.grace.app.injection.module.view.LoadingViewModule;
import com.grace.app.model.MealPhoto;
import com.grace.app.model.MealResponse;
import com.grace.app.presenter.LoadingPresenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.util.LogUtil;
import com.grace.app.view.LoadingView;
import com.grace.app.view.activity.Main2Activity;
import com.grace.app.view.helper.ChangeFragmentHelper;
import com.grace.app.view.impl.BaseFragment;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.BuildConfig;

public final class LoadingFragment extends BaseFragment<LoadingPresenter, LoadingView> implements LoadingView {

    @Inject
    PresenterFactory<LoadingPresenter> mPresenterFactory;
    private FragmentLoadingBinding mBinding;
    private TextView mLoadingTextView;
    private ImageView mGraceLoadingImageView;

    private String mPhotoUri = "";
    private int mLoadingMessage = -1;
    private boolean working = false;

    MealPhoto mMealPhoto;

    public LoadingFragment() {
        // Required empty public constructor
    }

    public static LoadingFragment newInstance() {
        return new LoadingFragment();
    }


    public static LoadingFragment newInstance(String photoUri, int loadingMessage) {
        Bundle b = new Bundle();
        b.putString(Constants.PHOTO_URI_KEY, photoUri);
        b.putInt(Constants.LOADING_MESSAGE_KEY, loadingMessage);
        LoadingFragment loadingFragment = new LoadingFragment();
        loadingFragment.setArguments(b);
        return loadingFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentLoadingBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        mLoadingTextView = mBinding.loadingTextView;
        mGraceLoadingImageView = mBinding.graceLoadingImageView;
        getLoadingArguments();
        return view;
    }


    @Override
    protected void setupComponent(@NonNull AppComponent parentComponent) {
        DaggerLoadingViewComponent.builder()
                .appComponent(parentComponent)
                .loadingViewModule(new LoadingViewModule())
                .build()
                .inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        startLoadingAnimation();
    }

    /**
     * Load the params needed to fill the UI and manipulate photo
     */
    private void getLoadingArguments() {
        if (getArguments() != null) {
            if (getArguments().containsKey(Constants.PHOTO_URI_KEY))
                mPhotoUri = getArguments().getString(Constants.PHOTO_URI_KEY);
            if (getArguments().containsKey(Constants.LOADING_MESSAGE_KEY))
                mLoadingMessage = getArguments().getInt(Constants.LOADING_MESSAGE_KEY);
        }
    }

    /**
     * Start the loading animation
     */
    private void startLoadingAnimation() {

        if (mLoadingMessage != -1)
            mLoadingTextView.setText(mLoadingMessage);

        ObjectAnimator graceLoadingAnimator = ObjectAnimator.ofFloat(mGraceLoadingImageView, "alpha", 0.1f, 1.0f)
                .setDuration(Constants.LOADING_ANIMATION_DURATION);
        graceLoadingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        graceLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        graceLoadingAnimator.start();
    }

    /**
     * Check if the meal contains meal or bless the meal if it's already checked
     */
    private void takePhotoAction() {
        if (mPresenter != null && !working) {
            //checking if is photo of meal
            if (mLoadingMessage == R.string.let_me_see_text) {
                LogUtil.d(Constants.API_TAG, "takePhotoAction() | check if there's meal on the photo");
                mPresenter.isPhotoOfMeal(mPhotoUri);
            }
            //blessing photo
            else {
                LogUtil.d(Constants.API_TAG, "takePhotoAction() | bless meal");
                mPresenter.blessPhoto(mPhotoUri);
            }
            working = true;
        }
    }

    @Override
    protected void onPresenterReady() {
        takePhotoAction();
    }


    @Override
    public void onIsMealResponseSuccess(MealResponse mealResponse) {
        mMealPhoto = new MealPhoto();
        if (mealResponse.getIsMeal()) {
            mMealPhoto.setPhotoUri(mPhotoUri);
            mMealPhoto.setTitle(R.string.his_grace_asks_text);
            mMealPhoto.setSubTitle(R.string.do_you_want_to_text);
            mMealPhoto.setLeftButtonText(R.string.no_text);
            mMealPhoto.setRightButtonText(R.string.yes_text);
        } else {
            mMealPhoto.setTitle(R.string.his_grace_angered_text);
            mMealPhoto.setSubTitle(R.string.not_a_meal_text);
            mMealPhoto.setLeftButtonText(R.string.feel_wraith_text);
            mMealPhoto.setRightButtonText(R.string.another_try_text);
        }

        ChangeFragmentHelper.setPhotoFragment((AppCompatActivity) getActivity(),
                R.id.main_frame_layout, mMealPhoto);
    }

    @Override
    public void onIsMealResponseFailure(String error) {
        if (BuildConfig.DEBUG) {
            ((Main2Activity) getActivity()).showSnackBarMessage(error);
        } else {
            ((Main2Activity) getActivity())
                    .showSnackBarMessage(getString(R.string.something_went_wrong_text));
        }
        ChangeFragmentHelper.setGetMealFromFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout);
    }

    @Override
    public void onPhotoBlessed(String uri) {
        if (uri != null && !uri.isEmpty()) {
            mMealPhoto = new MealPhoto();
            mMealPhoto.setPhotoUri(uri);
            mMealPhoto.setTitle(R.string.his_grace_approves_text);
            mMealPhoto.setSubTitle(R.string.your_meal_is_blessed_text);
            mMealPhoto.setLeftButtonText(R.string.done_text);
            mMealPhoto.setRightButtonText(R.string.share_text);

            ChangeFragmentHelper.setPhotoFragment((AppCompatActivity) getActivity(),
                    R.id.main_frame_layout, mMealPhoto);
        } else {
            ((Main2Activity) getActivity())
                    .showSnackBarMessage(getString(R.string.something_went_wrong_text));
            ChangeFragmentHelper.setGetMealFromFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout);
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
    protected PresenterFactory<LoadingPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
