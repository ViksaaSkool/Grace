package com.grace.app.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.grace.app.GraceApplication;
import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.util.GracePhotoUtil;
import com.grace.app.util.LogUtil;
import com.grace.app.view.LoadingView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by varsovski on 10-Jun-17.
 */

public class BlessPhotoAsyncTask extends AsyncTask<Void, Void, String> {

    private LoadingView mLoadingView;
    private String mUri;

    public BlessPhotoAsyncTask(LoadingView view, String uri) {
        this.mLoadingView = view;
        this.mUri = uri;
    }

    @Override
    protected String doInBackground(Void... params) {

        LogUtil.d(Constants.BLESS_TAG, "BlessPhotoAsyncTask doInBackground() | photo = " + mUri);

        //get meal photo
        Bitmap bottomImage = GracePhotoUtil.getHandledBitmap(mUri);

        //get and resize watermark
        Bitmap _topImage = BitmapFactory.decodeResource(GraceApplication.getAppComponent().getApp().getResources(),
                R.drawable.watermark_blessed_transparent);
        int widthHeight = 0;
        if (GracePhotoUtil.getResizeType(bottomImage) == Constants.PORTRAIT_RESIZE) {
            widthHeight = bottomImage.getWidth() / Constants.WATERMARK_DIMENSIONS_FACTOR;
            LogUtil.d(Constants.BLESS_TAG, "BlessPhotoAsyncTask doInBackground() | " +
                    "Constants.PORTRAIT_RESIZE dimensions = " + widthHeight);
        } else {
            widthHeight = bottomImage.getHeight() / Constants.WATERMARK_DIMENSIONS_FACTOR;
            LogUtil.d(Constants.BLESS_TAG, "BlessPhotoAsyncTask doInBackground() | "
                    + "Constants.LANDSCAPE_RESIZE dimensions = " + widthHeight);
        }
        Bitmap topImage = GracePhotoUtil.getResizedBitmap(_topImage, widthHeight, widthHeight);
        _topImage.recycle();


        //get output blessed photo
        String blessedPhotoUri = "";
        OutputStream blessedPhotoOutputStream = null;
        try {
            File file = GracePhotoUtil.getOutputMediaFile(Constants.GRACE_BLESSED_PHOTO);
            if (file != null) {
                blessedPhotoUri = file.getAbsolutePath();

                blessedPhotoOutputStream = new FileOutputStream(blessedPhotoUri);
                //magic happens - bless and create image
                GracePhotoUtil.addBlessing(bottomImage, topImage, Constants.MARGIN_FACTOR) //bless photo
                        .compress(Bitmap.CompressFormat.JPEG, 100, blessedPhotoOutputStream); //save as .jpeg image
            }

        } catch (IOException e) {
            LogUtil.d(Constants.BLESS_TAG, "doInBackground() | exception = " + e.getMessage());
            blessedPhotoUri = "";
        }

        //recycle bitmaps
        bottomImage.recycle();
        topImage.recycle();

        return blessedPhotoUri;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        LogUtil.d(Constants.BLESS_TAG, "BlessPhotoAsyncTask onPostExecute() | result = " + result);
        mLoadingView.onPhotoBlessed(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        LogUtil.d(Constants.BLESS_TAG, "BlessPhotoAsyncTask onCancelled() | canceled!");
    }
}
