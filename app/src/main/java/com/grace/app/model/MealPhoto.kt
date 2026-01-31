package com.grace.app.model;

import java.io.Serializable;

/**
 * Created by varsovski on 12-Jun-17.
 */

public class MealPhoto implements Serializable {

    private String photoUri;
    private int title;
    private int subTitle;
    private int leftButtonText;
    private int rightButtonText;


    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(int subTitle) {
        this.subTitle = subTitle;
    }

    public int getLeftButtonText() {
        return leftButtonText;
    }

    public void setLeftButtonText(int leftButtonText) {
        this.leftButtonText = leftButtonText;
    }

    public int getRightButtonText() {
        return rightButtonText;
    }

    public void setRightButtonText(int rightButtonText) {
        this.rightButtonText = rightButtonText;
    }


}
