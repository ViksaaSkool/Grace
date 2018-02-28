package com.grace.app.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.grace.app.R;
import com.grace.app.constants.Constants;
import com.grace.app.util.LogUtil;
import com.udevel.widgetlab.TypingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by varsovski on 21-May-17.
 */

public class DisclaimerTermsAndConditionsFragment extends Fragment {


    @BindView(R.id.disclaimer_tnc_web_view)
    WebView mDisclaimerTncWebView;
    Unbinder unbinder;
    @BindView(R.id.loading_typing_indicator_view)
    TypingIndicatorView mLoadingTypingIndicatorView;

    private String mUrl;

    public static DisclaimerTermsAndConditionsFragment newInstance(String url) {
        DisclaimerTermsAndConditionsFragment fragment = new DisclaimerTermsAndConditionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DISCLAIMER_TNC_KEY, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disclaimer_tnc, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadWebView();
    }

    @SuppressWarnings("all")
    public void loadWebView() {
        if (mDisclaimerTncWebView != null) {

            mUrl = getArguments().getString(Constants.DISCLAIMER_TNC_KEY, "");

            if (mDisclaimerTncWebView.getSettings() != null && !mUrl.isEmpty()) {
                mDisclaimerTncWebView.getSettings().setJavaScriptEnabled(true);
                mDisclaimerTncWebView.getSettings().setLoadWithOverviewMode(true);
                mDisclaimerTncWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            }

            mDisclaimerTncWebView.setWebChromeClient(new WebChromeClient());
            mDisclaimerTncWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mDisclaimerTncWebView.loadUrl(mUrl);
            mDisclaimerTncWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    LogUtil.d(Constants.UI_TAG, "DisclaimerTermsAndCoditionsFragment shouldOverrideUrlLoading()");
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (mDisclaimerTncWebView != null)
                        mDisclaimerTncWebView.setVisibility(View.VISIBLE);
                    if (mLoadingTypingIndicatorView != null)
                        mLoadingTypingIndicatorView.setVisibility(View.GONE);
                    LogUtil.d(Constants.UI_TAG, "DisclaimerTermsAndCoditionsFragment " +
                            " onPageFinished() | url = " + url);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    if (mLoadingTypingIndicatorView != null)
                        mLoadingTypingIndicatorView.setVisibility(View.GONE);
                    if (mDisclaimerTncWebView != null) {
                        mDisclaimerTncWebView.setVisibility(View.VISIBLE);
                        mDisclaimerTncWebView.loadUrl(Constants.ERROR_WEB_PAGE);
                    }
                    LogUtil.d(Constants.UI_TAG, "DisclaimerTermsAndCoditionsFragment onReceivedError() | " +
                            "error = " + error.getDescription().toString());
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
