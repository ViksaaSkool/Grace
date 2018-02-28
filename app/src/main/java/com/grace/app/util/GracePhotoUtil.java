package com.grace.app.util;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import com.grace.app.GraceApplication;
import com.grace.app.constants.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.media.ExifInterface.TAG_ORIENTATION;

/**
 * Created by varsovski on 18-Dec-16.
 */

public class GracePhotoUtil {
    /**
     * Create output File for blessed photo
     *
     * @param photoName - name of the file
     * @return File
     */
    public static File getOutputMediaFile(String photoName) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.APP_FOLDER);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.d(Constants.CAMERA_TAG, "getOutputMediaFile() | failed to create directory");
                return null;
            }
        }
        // Create a media file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat(Constants.DATE_PHOTO_FORMAT)
                .format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath()
                + File.separator
                + photoName
                + timeStamp
                + Constants.GRACE_PHOTO_JPG);

        LogUtil.d(Constants.CAMERA_TAG, "getOutputMediaFile() | file path = "
                + mediaFile.getAbsolutePath());
        return mediaFile;
    }

    /**
     * Get selected photo from gallery/image picker
     *
     * @param selectedPhoto uri
     * @return string uri to selected photo
     */
    public static String getSelectedPhotoPath(Uri selectedPhoto) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String imgDecodableString = "";

        // Get the cursor
        Cursor cursor = GraceApplication
                .getAppComponent()
                .getApp()
                .getContentResolver()
                .query(selectedPhoto,
                        filePathColumn, null, null, null);
        // Move to first row
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
        }

        return imgDecodableString;
    }

    /**
     * Concatenating the provided photo of the meal with the watermark, i.e. bless the photo
     *
     * @param mealPhoto    - photo of meal that the user provided
     * @param watermark    - blessed watermark provided by the app
     * @param marginFactor - margin of the watermark form the bottom left corner in proportion to watermark width/height
     * @return - blessed Bitmap
     */
    public static Bitmap addBlessing(Bitmap mealPhoto, Bitmap watermark, int marginFactor) {

        Canvas canvas = new Canvas(mealPhoto);
        canvas.drawBitmap(mealPhoto, new Matrix(), null);
        canvas.drawBitmap(watermark, watermark.getWidth() / marginFactor,
                mealPhoto.getHeight() - (watermark.getHeight() + watermark.getHeight() / marginFactor), null);
        return mealPhoto;
    }


    /**
     * Determine the orientation of the provided photo
     *
     * @param photoPath - uri to the photo
     * @return
     */
    private static int getOrientation(String photoPath) {
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoPath);
            orientation = Integer.valueOf(exif.getAttribute(TAG_ORIENTATION));
            LogUtil.d(Constants.BLESS_TAG, "getOrientation() | orientation is: " + printOrientation(orientation));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d(Constants.BLESS_TAG, "getOrientation() | exception = " + e.getMessage());
        }
        return orientation;
    }


    /**
     * Handle mirroring and photo orientation
     *
     * @param photoPath uri of the photo that is provided by the user
     * @return bitmap in correct viewing position
     */
    public static Bitmap getHandledBitmap(String photoPath) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        switch (getOrientation(photoPath)) {
            case ExifInterface.ORIENTATION_UNDEFINED:
                //TODO
                bitmap = BitmapFactory.decodeFile(photoPath, options);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                bitmap = flip(BitmapFactory.decodeFile(photoPath, options),
                        ExifInterface.ORIENTATION_FLIP_HORIZONTAL);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap = rotateBitmap(BitmapFactory.decodeFile(photoPath, options), 180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                bitmap = flip(BitmapFactory.decodeFile(photoPath, options),
                        ExifInterface.ORIENTATION_FLIP_VERTICAL);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                bitmap = BitmapFactory.decodeFile(photoPath, options);
                //TODO
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap = rotateBitmap(BitmapFactory.decodeFile(photoPath, options), 90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                bitmap = BitmapFactory.decodeFile(photoPath, options);
                //TODO
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap = rotateBitmap(BitmapFactory.decodeFile(photoPath, options), 270);
                break;
            default:
                bitmap = BitmapFactory.decodeFile(photoPath, options);
                break;
        }

        return bitmap;

    }


    /**
     * Resize photo with provided width and height
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();

        return resizedBitmap;
    }

    /**
     * Resize given Bitmap - used to send smaller photo to service
     *
     * @param bitmap - bitmap that needs to be scaled
     * @param scale  - scale factor
     * @return - scaled Bitmap
     */
    public static Bitmap getResizedBitmap(Bitmap bitmap, int scale) {
        int scaleWidth = bitmap.getWidth() / scale;
        int scaleHeight = bitmap.getHeight() / scale;
        return Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
    }

    /**
     * Get the resize type for the watermark dependent on the
     * orientation (proportion) of the meal being blessed
     *
     * @param mealPhoto - provided photo of meal
     * @return - resize type
     */
    public static int getResizeType(Bitmap mealPhoto) {
        return mealPhoto.getWidth() > mealPhoto.getHeight() ? Constants.LANDSCAPE_RESIZE : Constants.PORTRAIT_RESIZE;
    }

    /**
     * Log orientation
     *
     * @param orientationTag
     * @return
     */
    private static String printOrientation(int orientationTag) {
        String orientation = "";
        switch (orientationTag) {
            case ExifInterface.ORIENTATION_UNDEFINED:
                orientation = "ORIENTATION_UNDEFINED";
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                orientation = "ORIENTATION_FLIP_HORIZONTAL";
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                orientation = "ORIENTATION_ROTATE_180";
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                orientation = "ORIENTATION_FLIP_VERTICAL";
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                orientation = "ORIENTATION_TRANSPOSE";
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                orientation = "ORIENTATION_ROTATE_90";
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                orientation = "ORIENTATION_TRANSVERSE ";
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                orientation = "ORIENTATION_UNDEFINED";
                break;
        }
        return orientation;
    }

    /* Helpers */

    public static float getPhotoSizeInMB(String photoUri) {
        return (float) new File(photoUri).length() / (1024 * 1024);
    }

    public static String convertPhotoToString(String photoPath) {

        float photoMB = getPhotoSizeInMB(photoPath);
        int scaleF = 3;
        if (photoMB > 1f && photoMB <= 3f)
            scaleF = 6;
        else if (photoMB > 3f && photoMB <= 6f)
            scaleF = 9;
        else if (photoMB > 6f)
            scaleF = 18;

        Bitmap bitmap = getResizedBitmap(BitmapFactory.decodeFile(photoPath), scaleF);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        String imageType = new File(photoPath).getName().substring(new File(photoPath).getName().lastIndexOf("."));


        return "data:image/" + imageType + ";base64," + Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private static Bitmap flip(Bitmap src, int type) {
        Matrix matrix = new Matrix();
        if (type == ExifInterface.ORIENTATION_FLIP_VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        } else if (type == ExifInterface.ORIENTATION_FLIP_HORIZONTAL) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


}
