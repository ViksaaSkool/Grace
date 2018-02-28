package com.grace.app.view.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.RequestManager;
import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.custom.BitmapCropTransformation;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.view.DaggerPhotoComponent;
import com.grace.app.injection.module.view.PhotoModule;
import com.grace.app.model.MealPhoto;
import com.grace.app.presenter.PhotoPresenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.util.ShareUtil;
import com.grace.app.util.UiUtil;
import com.grace.app.view.PhotoView;
import com.grace.app.view.activity.PhotoDetailsActivity;
import com.grace.app.view.helper.ChangeActivityHelper;
import com.grace.app.view.helper.ChangeFragmentHelper;
import com.grace.app.view.impl.BaseFragment;
import com.lb.auto_fit_textview.AutoResizeTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by varsovski on 24-Dec-16.
 */

public class PhotoFragment extends BaseFragment<PhotoPresenter, PhotoView> implements PhotoView {

    @Inject
    PresenterFactory<PhotoPresenter> mPresenterFactory;
    @Inject
    RequestManager mRequestManager;


    @BindView(R.id.meal_image_view)
    ImageView mMealImageView;
    @BindView(R.id.meal_background_relative_layout)
    RelativeLayout mMealBackgroundRelativeLayout;
    @BindView(R.id.title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.subtitle_text_view)
    AutoResizeTextView mSubtitleTextView;
    @BindView(R.id.left_button)
    Button mLeftButton;
    @BindView(R.id.right_button)
    Button mRightButton;
    @BindView(R.id.error_image_view)
    ImageView mErrorImageView;
    @BindView(R.id.error_relative_layout)
    RelativeLayout mErrorRelativeLayout;
    @BindView(R.id.overlay_relative_layout)
    RelativeLayout mOverlayRelativeLayout;
    @BindView(R.id.left_button_ripple)
    MaterialRippleLayout mLeftButtonRipple;
    @BindView(R.id.right_button_ripple)
    MaterialRippleLayout mRightButtonRipple;
    @BindView(R.id.tap_full_relative_layout)
    RelativeLayout mTapFullRelativeLayout;

    Unbinder unbinder;

    private MealPhoto mMealPhoto;
    private boolean feelingWraith = false;

    public PhotoFragment() {
    }

    public static PhotoFragment newInstance(MealPhoto mealPhoto) {
        Bundle b = new Bundle();
        b.putSerializable(Constants.MEAL_PHOTO_OBJECT_KEY, mealPhoto);
        PhotoFragment photoFragment = new PhotoFragment();
        photoFragment.setArguments(b);
        return photoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        unbinder = ButterKnife.bind(this, view);
        getMealPhotoAndInitUI();
        return view;
    }

    @Override
    protected void setupComponent(@NonNull AppComponent appComponent) {
        DaggerPhotoComponent.builder()
                .appComponent(appComponent)
                .photoModule(new PhotoModule())
                .build()
                .inject(this);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void getMealPhotoAndInitUI() {
        if (getArguments() != null
                && getArguments().getSerializable(Constants.MEAL_PHOTO_OBJECT_KEY) != null) {
            mMealPhoto = (MealPhoto) getArguments().getSerializable(Constants.MEAL_PHOTO_OBJECT_KEY);


            if (mMealPhoto != null) {
                //init photo
                if (mMealPhoto.getPhotoUri() == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        mMealBackgroundRelativeLayout
                                .setBackgroundColor(getResources()
                                        .getColor(R.color.colorPrimary, null));
                    else
                        mMealBackgroundRelativeLayout
                                .setBackgroundColor(getResources()
                                        .getColor(R.color.colorPrimary));


                    mMealImageView.setVisibility(View.GONE);
                    mOverlayRelativeLayout.setVisibility(View.GONE);
                    mErrorRelativeLayout.setVisibility(View.VISIBLE);

                    changeStatusBarColor(R.color.colorPrimaryDark);

                } else {
                    mErrorRelativeLayout.setVisibility(View.GONE);
                    mOverlayRelativeLayout.setVisibility(View.VISIBLE);
                    mRequestManager.load(mMealPhoto.getPhotoUri())
                            .bitmapTransform(new BitmapCropTransformation(getActivity(),
                                    (int) (UiUtil.containerHeight((AppCompatActivity) getActivity()) * 0.6),
                                    UiUtil.containerWidth((AppCompatActivity) getActivity())))
                            .crossFade(Constants.CROSS_FADE_DURATION)
                            .into(mMealImageView);

                    changeStatusBarColor(android.R.color.black);
                    showTapFullPhotoLayout();
                }

                //init text
                mTitleTextView.setText(mMealPhoto.getTitle());
                mSubtitleTextView.setText(mMealPhoto.getSubTitle());
                mLeftButton.setText(mMealPhoto.getLeftButtonText());
                mRightButton.setText(mMealPhoto.getRightButtonText());


                UiUtil.setAndStartScaleAnimation(mRightButtonRipple,
                        0f, 1f, Constants.SCALE_DURATION);
                UiUtil.setAndStartScaleAnimation(mLeftButtonRipple,
                        0f, 1f, Constants.SCALE_DURATION);


            }

        }
    }


    @OnClick({R.id.meal_background_relative_layout, R.id.left_button, R.id.right_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.meal_background_relative_layout:
                handlePhotoClick();
                mTapFullRelativeLayout.setVisibility(View.GONE);
                break;
            case R.id.left_button:
                handleLeftButtonClick();
                break;
            case R.id.right_button:
                handleRightButtonClick();
                break;
        }
    }

    private void handleLeftButtonClick() {
        switch (mMealPhoto.getLeftButtonText()) {
            case R.string.done_text:
                ChangeFragmentHelper.setGetMealFromFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout);
                break;

            case R.string.no_text:
                ChangeFragmentHelper.setGetMealFromFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout);
                break;

            case R.string.feel_wraith_text:
                //TODO animation
                if (!feelingWraith) {
                    ObjectAnimator graceLoadingAnimator = ObjectAnimator.ofFloat(mErrorImageView, "alpha", 1.0f, 0.1f)
                            .setDuration(Constants.LOADING_ANIMATION_DURATION);
                    graceLoadingAnimator.setRepeatMode(ValueAnimator.REVERSE);
                    graceLoadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
                    graceLoadingAnimator.start();
                    graceLoadingAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            feelingWraith = true;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            feelingWraith = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            feelingWraith = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
                break;

        }
    }

    private void handleRightButtonClick() {
        switch (mMealPhoto.getRightButtonText()) {

            case R.string.yes_text:
                ChangeFragmentHelper.setLoadingFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout,
                        mMealPhoto.getPhotoUri(), R.string.blessing_photo_text);
                break;

            case R.string.share_text:
                ShareUtil.shareBlessedPhoto(getActivity(), mMealPhoto.getPhotoUri());
                break;

            case R.string.another_try_text:
                ChangeFragmentHelper.setGetMealFromFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout);
                break;
        }
    }

    private void handlePhotoClick() {
        if (mMealPhoto.getRightButtonText() != R.string.another_try_text) {
            Bundle b = new Bundle();
            b.putString(Constants.PHOTO_URI_KEY, mMealPhoto.getPhotoUri());

            ChangeActivityHelper.changeActivityExtra(getActivity(), PhotoDetailsActivity.class, b, false);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void showTapFullPhotoLayout() {
        mTapFullRelativeLayout.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(mTapFullRelativeLayout, "scaleY", 0.0f, 1.0f);
        anim.setDuration(Constants.TAP_DURATION);
        anim.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /* Getters and Setters */
    @NonNull
    @Override
    protected PresenterFactory<PhotoPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
