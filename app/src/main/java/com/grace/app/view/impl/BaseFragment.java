package com.grace.app.view.impl;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.grace.app.GraceApplication;
import com.grace.app.injection.componenet.AppComponent;
import com.grace.app.presenter.BasePresenter;
import com.grace.app.presenter.loader.PresenterFactory;
import com.grace.app.presenter.loader.PresenterLoader;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseFragment<P extends BasePresenter<V>, V> extends Fragment implements LoaderManager.LoaderCallbacks<P> {
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
     * Is this the first start of the fragment (after onCreate)
     */
    private boolean mFirstStart;
    /**
     * Unique identifier for the loader, persisted across re-creation
     */
    private int mUniqueLoaderIdentifier;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirstStart = savedInstanceState == null || savedInstanceState.getBoolean(RECREATION_SAVED_STATE);
        mUniqueLoaderIdentifier = savedInstanceState == null ? BaseActivity.sViewCounter.incrementAndGet() : savedInstanceState.getInt(LOADER_ID_SAVED_STATE);

        injectDependencies();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LoaderManager.getInstance(this).initLoader(mUniqueLoaderIdentifier, null, this).startLoading();
    }

    private void injectDependencies() {
        setupComponent(((GraceApplication) getActivity().getApplication()).getAppComponent());
    }

    @Override
    public void onStart() {
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

        onPresenterReady();
    }

    @Override
    public void onStop() {
        if (mPresenter != null) {
            mPresenter.onStop();

            mPresenter.onViewDetached();
        }

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(RECREATION_SAVED_STATE, mFirstStart);
        outState.putInt(LOADER_ID_SAVED_STATE, mUniqueLoaderIdentifier);
    }

    @Override
    public final Loader<P> onCreateLoader(int id, Bundle args) {
        return new PresenterLoader<>(getActivity(), getPresenterFactory());
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
     * Change primaryColorDark
     *
     * @param color
     */
    public void changeStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), color));
        }
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

    /**
     * Hook for subclasses that need to act once the presenter is attached and started.
     */
    protected void onPresenterReady() {
    }
}
