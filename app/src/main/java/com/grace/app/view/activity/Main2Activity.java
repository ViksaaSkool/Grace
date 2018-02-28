package com.grace.app.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.FrameLayout;

import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.injection.componenet.view.DaggerMain2ViewComponent;
import com.grace.app.injection.module.view.Main2ViewModule;
import com.grace.app.presenter.Main2Presenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.util.GracePhotoUtil;
import com.grace.app.util.LogUtil;
import com.grace.app.view.Main2View;
import com.grace.app.view.helper.ChangeFragmentHelper;
import com.grace.app.view.impl.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class Main2Activity extends BaseActivity<Main2Presenter, Main2View> implements Main2View {

    @Inject
    PresenterFactory<Main2Presenter> mPresenterFactory;
    @Inject
    SharedPreferences mSharedPreferences;
    @BindView(R.id.main_frame_layout)
    FrameLayout mRootLayout;

    private static final int PERMISSION_CAMERA_CODE = 1;
    private static final int PERMISSION_READ_WRITE_CODE = 2;

    private String mCameraPhoto = "";

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int RESULT_LOAD_IMG = 1044;

    private int buttonFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        ChangeFragmentHelper.setGetMealFromFragment(this, R.id.main_frame_layout);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void setupComponent(@NonNull AppComponent parentComponent) {
        DaggerMain2ViewComponent.builder()
                .appComponent(parentComponent)
                .main2ViewModule(new Main2ViewModule())
                .build()
                .inject(this);
    }

    @Override
    public void onInternetConnectionChange(boolean isConnected) {
        LogUtil.d(Constants.APP_TAG, "onInternetConnectionChange() | " +
                "isConnected = " + isConnected);
        if (!isConnected)
            showSnackBarMessage(getString(R.string.no_internets));
    }


    @Subscribe
    public void onLoadingInterrupted(Boolean isInterrupted) {
        if (isInterrupted) {
            showSnackBarMessage(getString(R.string.interrupted_text));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtil.d(Constants.APP_TAG, "onRequestPermissionsResult() | "
                + " requestCode = " + requestCode
                + " grantResults = " + Arrays.toString(grantResults));
        switch (requestCode) {

            case PERMISSION_CAMERA_CODE:
                if (grantResults[0] == 0) {
                    LogUtil.d(Constants.APP_TAG, "onRequestPermissionsResult() " +
                            "| PERMISSION_CAMERA granted!");
                    startExternalCamera();
                } else {
                    LogUtil.d(Constants.APP_TAG, "onRequestPermissionsResult() | " +
                            "PERMISSION_CAMERA NOT granted!");
                    showSnackBarMessage(getString(R.string.permission_not_granted_text));
                }
                break;

            case PERMISSION_READ_WRITE_CODE:
                if (grantResults[0] == 0 && grantResults[1] == 0) {
                    LogUtil.d(Constants.APP_TAG, "onRequestPermissionsResult() | " +
                            "PERMISSION_READ_WRITE granted!");
                    if (buttonFlag == R.id.from_gallery_linear_layout)
                        getPhotosFromGallery();
                    else
                        startExternalCamera();
                } else {
                    LogUtil.d(Constants.APP_TAG, "onRequestPermissionsResult() | " +
                            "PERMISSION_READ_WRITE NOT granted!");
                    showSnackBarMessage(getString(R.string.permission_not_granted_text));
                }
                break;

            default:
                LogUtil.d(Constants.APP_TAG, "onRequestPermissionsResult() | why am I here?");
                break;
        }
    }


    /**
     * Check for permissions and start external
     * Camera app in order to take meal Photo for blessing
     */
    public void startExternalCamera() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            LogUtil.d(Constants.APP_TAG, "startExternalCamera() | has permission or is <M");

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = GracePhotoUtil.getOutputMediaFile(Constants.GRACE_PHOTO);
            if (file != null) {
                mCameraPhoto = file.getAbsolutePath();

                // SDK_INT >= 24
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    Uri photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                }
                // SDK_INT < 24
                else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                }

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        } else {
            LogUtil.d(Constants.APP_TAG, "startExternalCamera() | request permission");
            buttonFlag = R.id.capture_meal_linear_layout;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CODE);
            else
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_WRITE_CODE);

        }
    }


    /**
     * Check for permissions and start the Gallery in
     * order to select photo for blessing
     */
    public void getPhotosFromGallery() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            LogUtil.d(Constants.APP_TAG, "getPhotosFromGallery() | has permission or is <M");
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        } else {
            LogUtil.d(Constants.APP_TAG, "getPhotosFromGallery() | request permission");
            buttonFlag = R.id.from_gallery_linear_layout;
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_WRITE_CODE);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                //ToastUtil.showToast("taken Photo Uri = " + mCameraPhoto, Toast.LENGTH_SHORT);
                setFragment(ChangeFragmentHelper.PHOTO_FRAGMENT, mCameraPhoto);

            } else { // Result was a failure
                LogUtil.d(Constants.CAMERA_TAG, "onActivityResult() | no photo taken!");
                showSnackBarMessage(getString(R.string.no_photo_was_taken));
            }
        } else if (requestCode == RESULT_LOAD_IMG) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {

                String selectedPhotoPath = GracePhotoUtil.getSelectedPhotoPath(data.getData());
                //ToastUtil.showToast("Photo selected = " + selectedPhotoPath, Toast.LENGTH_SHORT);

                setFragment(ChangeFragmentHelper.PHOTO_FRAGMENT, selectedPhotoPath);

            } else { // Result was a failure
                LogUtil.d(Constants.CAMERA_TAG, "onActivityResult() | no photo selected!");
                showSnackBarMessage(getString(R.string.no_photo_was_selected));
            }
        }
    }


    public void setFragment(final String fragment, final String uri) {
        final AppCompatActivity activity = this;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                switch (fragment) {
                    case ChangeFragmentHelper.GET_MEAL_FROM_FRAGMENT:
                        ChangeFragmentHelper.setGetMealFromFragment(activity, R.id.main_frame_layout);
                        break;
                    case ChangeFragmentHelper.PHOTO_FRAGMENT:
                        // ChangeFragmentHelper.setPhotoFragment(activity, R.id.main_frame_layout, uri);
                        ChangeFragmentHelper.setLoadingFragment(activity, R.id.main_frame_layout, uri, R.string.let_me_see_text);
                        break;

                    default:
                        LogUtil.d(Constants.UI_TAG, "setFragment() | default: " + fragment);
                        break;
                }
            }
        });
    }

    public void showSnackBarMessage(String message) {
        if (mRootLayout != null) {
            Snackbar.make(mRootLayout, message, Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager() != null
                && getSupportFragmentManager()
                .findFragmentByTag(ChangeFragmentHelper.GET_MEAL_FROM_FRAGMENT) == null)
            ChangeFragmentHelper.setGetMealFromFragment(this, R.id.main_frame_layout);
        else
            super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /* Getters and Setters */
    @NonNull
    @Override
    protected PresenterFactory<Main2Presenter> getPresenterFactory() {
        return mPresenterFactory;
    }
}
