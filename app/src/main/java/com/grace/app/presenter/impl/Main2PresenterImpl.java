package com.grace.app.presenter.impl;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.grace.app.GraceApplication;
import com.grace.app.interactor.Main2Interactor;
import com.grace.app.presenter.Main2Presenter;
import com.grace.app.view.Main2View;

import javax.inject.Inject;

public final class Main2PresenterImpl extends BasePresenterImpl<Main2View> implements Main2Presenter {

    /**
     * The interactor
     */
    @NonNull
    private final Main2Interactor mInteractor;

    private int orientationSensor;

    @Inject
    public Main2PresenterImpl(@NonNull Main2Interactor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onStart(boolean firstStart) {
        super.onStart(firstStart);
        registerOrientationSensor();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPresenterDestroyed() {
        super.onPresenterDestroyed();
    }


    @Override
    public int getOrientation() {
        return orientationSensor;
    }

    private void registerOrientationSensor() {
        SensorManager sensorManager = (SensorManager) GraceApplication
                .getAppComponent()
                .getApp()
                .getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[1] < 6.5 && event.values[1] > -6.5) {
                    orientationSensor = Configuration.ORIENTATION_LANDSCAPE;
                } else {
                    orientationSensor = Configuration.ORIENTATION_PORTRAIT;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {


            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }
}