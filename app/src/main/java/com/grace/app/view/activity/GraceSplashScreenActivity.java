package com.grace.app.view.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;

import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.view.DaggerGraceSplashScreenViewComponent;
import com.grace.app.injection.module.view.GraceSplashScreenViewModule;
import com.grace.app.presenter.GraceSplashScreenPresenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.util.UiUtil;
import com.grace.app.view.GraceSplashScreenView;
import com.grace.app.view.helper.ChangeActivityHelper;
import com.grace.app.view.impl.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class GraceSplashScreenActivity extends BaseActivity<GraceSplashScreenPresenter,
        GraceSplashScreenView> implements GraceSplashScreenView {

    @Inject
    PresenterFactory<GraceSplashScreenPresenter> mPresenterFactory;

    @BindView(R.id.background_image_view)
    ImageView mBackgroundImageView;
    @BindView(R.id.logo_image_view)
    ImageView mLogoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setContentView(R.layout.activity_splash_screen);
        else
            setContentView(R.layout.activity_splash_screen_1);
        ButterKnife.bind(this);
    }

    @Override
    protected void setupComponent(@NonNull AppComponent parentComponent) {
        DaggerGraceSplashScreenViewComponent.builder()
                .appComponent(parentComponent)
                .graceSplashScreenViewModule(new GraceSplashScreenViewModule())
                .build()
                .inject(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        startAnimation();

    }

    private void startAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimationDrawable splashAnimation = ((AnimationDrawable) mBackgroundImageView.getBackground());
            int animationDuration = getResources().getInteger(R.integer.frame_duration_could_sun_animation_0)
                    + getResources().getInteger(R.integer.frame_duration_could_sun_animation) * 9;
            if (splashAnimation != null) {
                splashAnimation.setOneShot(true);
                splashAnimation.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animateAppLogo();
                    }
                }, animationDuration);

            }
        } else {
            mBackgroundImageView.setImageResource(R.drawable.clouds_8);
            animateAppLogo();
        }
    }


    private void animateAppLogo() {
        mLogoImageView.setVisibility(View.VISIBLE);
        AnimatorSet logoAnimationSet = new AnimatorSet();

        int logoFinalPosition = (UiUtil.containerHeight(this) / 2) - (UiUtil.containerHeight(this) / 5); //vector is 220, that means 220 + 220/2
        int logoStartPosition = UiUtil.containerHeight(this);

        final AppCompatActivity activity = this;

        logoAnimationSet.playTogether(
                ObjectAnimator.ofFloat(mLogoImageView, "alpha", 0.1f, 1.0f)
                        .setDuration(Constants.ANIMATION_LOGO_DURATION_L),
                ObjectAnimator.ofFloat(mLogoImageView, "Y", logoStartPosition, logoFinalPosition)
                        .setDuration(Constants.ANIMATION_LOGO_DURATION_L));
        logoAnimationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChangeActivityHelper.changeActivity(activity,
                                Main2Activity.class, true);
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }, Constants.LOADING_ANIMATION_DURATION);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        logoAnimationSet.start();
    }


    @Override
    public void onInternetConnectionChange(boolean isConnected) {

    }


    @NonNull
    @Override
    protected PresenterFactory<GraceSplashScreenPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
