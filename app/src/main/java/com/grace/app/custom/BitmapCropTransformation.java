package com.grace.app.custom;

import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.grace.app.constants.Constants;
import com.grace.app.util.LogUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * Created by varsovski on 13-Jun-17.
 */

public class BitmapCropTransformation extends BitmapTransformation {
    private static final String ID = "com.grace.app.custom.BitmapCropTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));

    private int layoutHeight;
    private int layoutWidth;

    public BitmapCropTransformation(int h, int w) {
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


        width = Math.min(layoutWidth, toTransform.getWidth());
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
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        byte[] dims = ByteBuffer.allocate(8)
                .putInt(layoutHeight)
                .putInt(layoutWidth)
                .array();
        messageDigest.update(dims);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitmapCropTransformation)) {
            return false;
        }
        BitmapCropTransformation other = (BitmapCropTransformation) o;
        return layoutHeight == other.layoutHeight && layoutWidth == other.layoutWidth;
    }

    @Override
    public int hashCode() {
        int result = ID.hashCode();
        result = 31 * result + layoutHeight;
        result = 31 * result + layoutWidth;
        return result;
    }


}
