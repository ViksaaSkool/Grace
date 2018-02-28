package com.grace.app.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.view.DaggerPhotoDetailsViewComponent;
import com.grace.app.injection.module.view.PhotoDetailsViewModule;
import com.grace.app.presenter.PhotoDetailsPresenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.view.PhotoDetailsView;
import com.grace.app.view.impl.BaseActivity;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public final class PhotoDetailsActivity extends BaseActivity<PhotoDetailsPresenter, PhotoDetailsView> implements PhotoDetailsView {

    @Inject
    PresenterFactory<PhotoDetailsPresenter> mPresenterFactory;
    @Inject
    RequestManager mRequestManager;

    @BindView(R.id.details_photo_view)
    PhotoView mDetailsPhotoView;

    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);
        ButterKnife.bind(this);
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
                        .listener(new RequestListener<File, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e,
                                                       File model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource,
                                                           File model,
                                                           Target<GlideDrawable> target,
                                                           boolean isFromMemoryCache,
                                                           boolean isFirstResource) {
                                mAttacher.setZoomable(true);
                                mAttacher.update();
                                return false;
                            }
                        })
                        .crossFade(Constants.CROSS_FADE_DURATION)
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


    @OnClick(R.id.close_image_view)
    public void onViewClicked() {
        onBackPressed();
    }

    @NonNull
    @Override
    protected PresenterFactory<PhotoDetailsPresenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
