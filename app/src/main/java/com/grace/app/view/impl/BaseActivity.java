package com.grace.app.view.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.grace.app.GraceApplication;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.presenter.BasePresenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.presenter.loader.PresenterLoader;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity<P extends BasePresenter<V>, V> extends AppCompatActivity implements LoaderManager.LoaderCallbacks<P> {
    /**
     * Common counter for views (fragments and activities) that is used to generate loader ids
     */
    public static final AtomicInteger sViewCounter = new AtomicInteger(0);

    private final static String RECREATION_SAVED_STATE = "recreation_state";
    private final static String LOADER_ID_SAVED_STATE = "loader_id_state";
    /**
     * Do we need to call {@link #doStart()} from the {@link #onLoadFinished(Loader, BasePresenter)} method.
     * Will be true if presenter wasn't loaded when {@link #onStart()} is reached
     */
    private final AtomicBoolean mNeedToCallStart = new AtomicBoolean(false);
    /**
     * The presenter for this view
     */
    @Nullable
    protected P mPresenter;
    /**
     * Is this the first start of the activity (after onCreate)
     */
    private boolean mFirstStart;
    /**
     * Unique identifier for the loader, persisted across re-creation
     */
    private int mUniqueLoaderIdentifier;

    private NetworkStateReceiver mNetworkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirstStart = savedInstanceState == null || savedInstanceState.getBoolean(RECREATION_SAVED_STATE);
        mUniqueLoaderIdentifier = savedInstanceState == null ? BaseActivity.sViewCounter.incrementAndGet() : savedInstanceState.getInt(LOADER_ID_SAVED_STATE);

        injectDependencies();

        getSupportLoaderManager().initLoader(mUniqueLoaderIdentifier, null, this).startLoading();
    }

    private void injectDependencies() {
        setupComponent(((GraceApplication) getApplication()).getAppComponent());
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNetworkStateReceiver == null)
            mNetworkStateReceiver = new NetworkStateReceiver();
        registerReceiver(mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter == null) {
            mNeedToCallStart.set(true);
        } else {
            doStart();
        }
    }

    /**
     * Call the presenter callbacks for onStart
     */
    @SuppressWarnings("unchecked")
    private void doStart() {
        assert mPresenter != null;

        mPresenter.onViewAttached((V) this);

        mPresenter.onStart(mFirstStart);

        mFirstStart = false;
    }

    @Override
    protected void onStop() {
        if (mPresenter != null) {
            mPresenter.onStop();

            mPresenter.onViewDetached();
        }

        if (mNetworkStateReceiver != null)
            unregisterReceiver(mNetworkStateReceiver);

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(RECREATION_SAVED_STATE, mFirstStart);
        outState.putInt(LOADER_ID_SAVED_STATE, mUniqueLoaderIdentifier);
    }

    @Override
    public final Loader<P> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<>(this, getPresenterFactory());
    }

    @Override
    public final void onLoadFinished(Loader<P> loader, P presenter) {
        mPresenter = presenter;

        if (mNeedToCallStart.compareAndSet(true, false)) {
            doStart();
        }
    }

    @Override
    public final void onLoaderReset(Loader<P> loader) {
        mPresenter = null;
    }

    /**
     * Get the presenter factory implementation for this view
     *
     * @return the presenter factory
     */
    @NonNull
    protected abstract PresenterFactory<P> getPresenterFactory();

    /**
     * Setup the injection component for this view
     *
     * @param appComponent the app component
     */
    protected abstract void setupComponent(@NonNull AppComponent appComponent);


    private class NetworkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    onInternetConnectionChange(activeNetwork.isConnected());
                } else {
                    onInternetConnectionChange(false);
                }
            }
        }
    }

    public abstract void onInternetConnectionChange(boolean isConnected);
}
