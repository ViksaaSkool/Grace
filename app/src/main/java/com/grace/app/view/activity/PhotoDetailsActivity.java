package com.grace.app.view.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.databinding.ActivityPhotoDetailsBinding;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.view.DaggerPhotoDetailsViewComponent;
import com.grace.app.injection.module.view.PhotoDetailsViewModule;
import com.grace.app.presenter.PhotoDetailsPresenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.view.PhotoDetailsView;
import com.grace.app.view.impl.BaseActivity;

import java.io.File;

import javax.inject.Inject;

public final class PhotoDetailsActivity extends BaseActivity<PhotoDetailsPresenter, PhotoDetailsView> implements PhotoDetailsView {

    @Inject
    PresenterFactory<PhotoDetailsPresenter> mPresenterFactory;
    @Inject
    RequestManager mRequestManager;

    private ActivityPhotoDetailsBinding mBinding;
    private PhotoView mDetailsPhotoView;

    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPhotoDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mDetailsPhotoView = mBinding.detailsPhotoView;
        mBinding.closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initUI();

    }

    @Override
    protected void setupComponent(@NonNull AppComponent parentComponent) {
        DaggerPhotoDetailsViewComponent.builder()
                .appComponent(parentComponent)
                .photoDetailsViewModule(new PhotoDetailsViewModule())
                .build()
                .inject(this);
    }

    private void initUI() {
        if (getIntent() != null
                && getIntent().getExtras() != null
                && getIntent().getExtras().get(Constants.PHOTO_URI_KEY) != null) {

            String uri = getIntent().getExtras().getString(Constants.PHOTO_URI_KEY, "");
            if (!uri.isEmpty()) {
                mAttacher = new PhotoViewAttacher(mDetailsPhotoView);
                mAttacher.setZoomable(false);

                mRequestManager.load(new File(uri))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(GlideException e,
                                                        Object model,
                                                        Target<Drawable> target,
                                                        boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource,
                                                           Object model,
                                                           Target<Drawable> target,
                                                           DataSource dataSource,
                                                           boolean isFirstResource) {
                                mAttacher.setZoomable(true);
                                mAttacher.update();
                                return false;
                            }
                        })
                        .transition(DrawableTransitionOptions.withCrossFade(Constants.CROSS_FADE_DURATION))
                        .into(mDetailsPhotoView);
            }
        }
    }

    @Override
    public void onInternetConnectionChange(boolean isConnected) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @NonNull
    @Override
    protected PresenterFactory<PhotoDetailsPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
