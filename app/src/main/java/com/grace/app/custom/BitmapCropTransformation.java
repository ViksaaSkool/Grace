package com.grace.app.custom;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.grace.app.constants.Constants;
import com.grace.app.util.LogUtil;

/**
 * Created by varsovski on 13-Jun-17.
 */

public class BitmapCropTransformation extends BitmapTransformation {

    private int layoutHeight;
    private int layoutWidth;

    public BitmapCropTransformation(Context context, int h, int w) {
        super(context);
        this.layoutHeight = h;
        this.layoutWidth = w;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        LogUtil.d(Constants.UI_TAG, "transform() | layoutWidth = " + layoutWidth + "; layoutHeight = "
                + layoutHeight + " toTransform.getHeight() = " + toTransform.getHeight()
                + "; toTransform.getWidth() = " + toTransform.getWidth()
                + "; outWidth = " + outWidth + " outHeight = " + outHeight + ";");

        int y = 0;
        int width = 0;
        int height = 0;


        if (layoutWidth < toTransform.getWidth())
            width = layoutWidth;
        else
            width = toTransform.getWidth();
        if (layoutHeight < toTransform.getHeight()) {
            height = layoutHeight;
            y = toTransform.getHeight() - layoutHeight;
        } else {
            height = toTransform.getHeight();
            y = 0;
        }

        return Bitmap.createBitmap(toTransform, 0, y, width, height);
    }

    @Override
    public String getId() {
        return "com.grace.app.custom.BitmapCropTransformation";
    }


}
