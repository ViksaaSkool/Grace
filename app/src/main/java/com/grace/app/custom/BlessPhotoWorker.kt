package com.grace.app.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.grace.app.GraceApplication;
import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.util.GracePhotoUtil;
import com.grace.app.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Background worker for blessing a photo without AsyncTask.
 */
public final class BlessPhotoWorker {

    private BlessPhotoWorker() {
    }

    public static String bless(String uri) {
        LogUtil.d(Constants.BLESS_TAG, "BlessPhotoWorker bless() | photo = " + uri);

        //get meal photo
        Bitmap bottomImage = GracePhotoUtil.getHandledBitmap(uri);

        //get and resize watermark
        Bitmap _topImage = BitmapFactory.decodeResource(GraceApplication.getAppComponent().getApp().getResources(),
                R.drawable.watermark_blessed_transparent);
        int widthHeight;
        if (GracePhotoUtil.getResizeType(bottomImage) == Constants.PORTRAIT_RESIZE) {
            widthHeight = bottomImage.getWidth() / Constants.WATERMARK_DIMENSIONS_FACTOR;
            LogUtil.d(Constants.BLESS_TAG, "BlessPhotoWorker bless() | " +
                    "Constants.PORTRAIT_RESIZE dimensions = " + widthHeight);
        } else {
            widthHeight = bottomImage.getHeight() / Constants.WATERMARK_DIMENSIONS_FACTOR;
            LogUtil.d(Constants.BLESS_TAG, "BlessPhotoWorker bless() | "
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
            LogUtil.d(Constants.BLESS_TAG, "bless() | exception = " + e.getMessage());
            blessedPhotoUri = "";
        }

        //recycle bitmaps
        bottomImage.recycle();
        topImage.recycle();

        return blessedPhotoUri;
    }
}
