package com.grace.app.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.grace.app.GraceApplication;
import com.grace.app.R;

import java.io.File;

/**
 * Created by varsovski on 08-Jan-17.
 */

public class ShareUtil {

    public static void shareBlessedPhoto(Activity a, String blessedPhotoUri) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        final File photoFile = new File(blessedPhotoUri);
        Uri photoUri = FileProvider.getUriForFile(a,
                a.getPackageName() + ".provider",
                photoFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        a.startActivity(Intent.createChooser(shareIntent,
                GraceApplication.getAppComponent()
                        .getApp()
                        .getString(R.string.share_meal_text)));
    }
}

