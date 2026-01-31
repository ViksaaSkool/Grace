package com.grace.app.view.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.custom.BitmapCropTransformation;
import com.grace.app.databinding.FragmentPhotoBinding;
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


/**
 * Created by varsovski on 24-Dec-16.
 */

public class PhotoFragment extends BaseFragment<PhotoPresenter, PhotoView> implements PhotoView {

    @Inject
    PresenterFactory<PhotoPresenter> mPresenterFactory;
    @Inject
    RequestManager mRequestManager;

    private FragmentPhotoBinding mBinding;
    private ImageView mMealImageView;
    private RelativeLayout mMealBackgroundRelativeLayout;
    private TextView mTitleTextView;
    private AutoResizeTextView mSubtitleTextView;
    private Button mLeftButton;
    private Button mRightButton;
    private ImageView mErrorImageView;
    private RelativeLayout mErrorRelativeLayout;
    private RelativeLayout mOverlayRelativeLayout;
    private MaterialRippleLayout mLeftButtonRipple;
    private MaterialRippleLayout mRightButtonRipple;
    private RelativeLayout mTapFullRelativeLayout;

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
        mBinding = FragmentPhotoBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mMealImageView = mBinding.mealImageView;
        mMealBackgroundRelativeLayout = mBinding.mealBackgroundRelativeLayout;
        mTitleTextView = mBinding.titleTextView;
        mSubtitleTextView = mBinding.subtitleTextView;
        mLeftButton = mBinding.leftButton;
        mRightButton = mBinding.rightButton;
        mErrorImageView = mBinding.errorImageView;
        mErrorRelativeLayout = mBinding.errorRelativeLayout;
        mOverlayRelativeLayout = mBinding.overlayRelativeLayout;
        mLeftButtonRipple = mBinding.leftButtonRipple;
        mRightButtonRipple = mBinding.rightButtonRipple;
        mTapFullRelativeLayout = mBinding.tapFullRelativeLayout;
        mBinding.mealBackgroundRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked(v);
            }
        });
        mBinding.leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked(v);
            }
        });
        mBinding.rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked(v);
            }
        });
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
                            .transform(new BitmapCropTransformation(
                                    (int) (UiUtil.containerHeight((AppCompatActivity) getActivity()) * 0.6),
                                    UiUtil.containerWidth((AppCompatActivity) getActivity())))
                            .transition(DrawableTransitionOptions.withCrossFade(Constants.CROSS_FADE_DURATION))
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


    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.meal_background_relative_layout) {
            handlePhotoClick();
            mTapFullRelativeLayout.setVisibility(View.GONE);
        } else if (id == R.id.left_button) {
            handleLeftButtonClick();
        } else if (id == R.id.right_button) {
            handleRightButtonClick();
        }
    }

    private void handleLeftButtonClick() {
        int textId = mMealPhoto.getLeftButtonText();
        if (textId == R.string.done_text || textId == R.string.no_text) {
            ChangeFragmentHelper.setGetMealFromFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout);
        } else if (textId == R.string.feel_wraith_text) {
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
        }
    }

    private void handleRightButtonClick() {
        int textId = mMealPhoto.getRightButtonText();
        if (textId == R.string.yes_text) {
            ChangeFragmentHelper.setLoadingFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout,
                    mMealPhoto.getPhotoUri(), R.string.blessing_photo_text);
        } else if (textId == R.string.share_text) {
            ShareUtil.shareBlessedPhoto(getActivity(), mMealPhoto.getPhotoUri());
        } else if (textId == R.string.another_try_text) {
            ChangeFragmentHelper.setGetMealFromFragment((AppCompatActivity) getActivity(), R.id.main_frame_layout);
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
        mBinding = null;
    }


    /* Getters and Setters */
    @NonNull
    @Override
    protected PresenterFactory<PhotoPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
