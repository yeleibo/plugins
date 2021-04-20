package io.flutter.plugins.webviewflutter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;


import androidx.annotation.NonNull;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;
///腾讯x5内核的webView
public class X5WebView implements PlatformView, MethodChannel.MethodCallHandler {
    private static final String JS_CHANNEL_NAMES_FIELD = "javascriptChannelNames";
    //具体的webView
    private final WebView x5WebView;
    //与flutter通讯的通道，，用于执行一些回调方法如onPageFinished,onPageStarted
    private final MethodChannel methodChannel;
    private final Handler platformThreadHandler;

    X5WebView(
            final Context context,
            BinaryMessenger messenger,
            int id,
            Map<String, Object> params,
            View containerView) {
        x5WebView =new  WebView(context);
        // Allow local storage.
        x5WebView.getSettings().setDomStorageEnabled(true);
        x5WebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // Multi windows is set with FlutterWebChromeClient by default to handle internal bug: b/159892679.
        x5WebView.getSettings().setSupportMultipleWindows(true);
        //x5WebView.setWebChromeClient(new WebChromeClient());


        methodChannel = new MethodChannel(messenger, "plugins.flutter.io/webview_" + id);
        //设置通道的处理方法
        methodChannel.setMethodCallHandler(this);

        x5WebView.setWebViewClient(new X5WebViewClient(methodChannel));
        platformThreadHandler = new Handler(context.getMainLooper());

        //根据初始化的参数进行相应的操作
        Map<String, Object> settings = (Map<String, Object>) params.get("settings");
        if (settings != null) applySettings(settings);

        if (params.containsKey(JS_CHANNEL_NAMES_FIELD)) {
            List<String> names = (List<String>) params.get(JS_CHANNEL_NAMES_FIELD);
            if (names != null) registerJavaScriptChannelNames(names);
        }

        Integer autoMediaPlaybackPolicy = (Integer) params.get("autoMediaPlaybackPolicy");
        if (autoMediaPlaybackPolicy != null) updateAutoMediaPlaybackPolicy(autoMediaPlaybackPolicy);
        if (params.containsKey("userAgent")) {
            String userAgent = (String) params.get("userAgent");
            updateUserAgent(userAgent);
        }
        if (params.containsKey("initialUrl")) {
            String url = (String) params.get("initialUrl");
            x5WebView.loadUrl(url);
        }
    }
    @Override
    public View getView() {
        return x5WebView;
    }
    ///下面的代码复制来自FlutterWebView
    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {

    }

    @Override
    public void onFlutterViewDetached() {

    }

    @Override
    public void dispose() {
        methodChannel.setMethodCallHandler(null);
        x5WebView.destroy();
    }

    @Override
    public void onInputConnectionLocked() {

    }

    @Override
    public void onInputConnectionUnlocked() {

    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            case "loadUrl":
                loadUrl(methodCall, result);
                break;
            case "updateSettings":
                updateSettings(methodCall, result);
                break;
            case "canGoBack":
                canGoBack(result);
                break;
            case "canGoForward":
                canGoForward(result);
                break;
            case "goBack":
                goBack(result);
                break;
            case "goForward":
                goForward(result);
                break;
            case "reload":
                reload(result);
                break;
            case "currentUrl":
                currentUrl(result);
                break;
            case "evaluateJavascript":
                evaluateJavaScript(methodCall, result);
                break;
            case "addJavascriptChannels":
                addJavaScriptChannels(methodCall, result);
                break;
            case "removeJavascriptChannels":
                removeJavaScriptChannels(methodCall, result);
                break;
            case "clearCache":
                clearCache(result);
                break;
            case "getTitle":
                getTitle(result);
                break;
            case "scrollTo":
                scrollTo(methodCall, result);
                break;
            case "scrollBy":
                scrollBy(methodCall, result);
                break;
            case "getScrollX":
                getScrollX(result);
                break;
            case "getScrollY":
                getScrollY(result);
                break;
            default:
                result.notImplemented();
        }
    }


    @SuppressWarnings("unchecked")
    private void loadUrl(MethodCall methodCall, MethodChannel.Result result) {
        Map<String, Object> request = (Map<String, Object>) methodCall.arguments;
        String url = (String) request.get("url");
        Map<String, String> headers = (Map<String, String>) request.get("headers");
        if (headers == null) {
            headers = Collections.emptyMap();
        }
        x5WebView.loadUrl(url, headers);
        result.success(null);
    }

    private void canGoBack(MethodChannel.Result result) {
        result.success(x5WebView.canGoBack());
    }

    private void canGoForward(MethodChannel.Result result) {
        result.success(x5WebView.canGoForward());
    }

    private void goBack(MethodChannel.Result result) {
        if (x5WebView.canGoBack()) {
            x5WebView.goBack();
        }
        result.success(null);
    }

    private void goForward(MethodChannel.Result result) {
        if (x5WebView.canGoForward()) {
            x5WebView.goForward();
        }
        result.success(null);
    }

    private void reload(MethodChannel.Result result) {
        x5WebView.reload();
        result.success(null);
    }

    private void currentUrl(MethodChannel.Result result) {
        result.success(x5WebView.getUrl());
    }

    @SuppressWarnings("unchecked")
    private void updateSettings(MethodCall methodCall, MethodChannel.Result result) {
        applySettings((Map<String, Object>) methodCall.arguments);
        result.success(null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void evaluateJavaScript(MethodCall methodCall, final MethodChannel.Result result) {
        String jsString = (String) methodCall.arguments;
        if (jsString == null) {
            throw new UnsupportedOperationException("JavaScript string cannot be null");
        }
        x5WebView.evaluateJavascript(
                jsString,
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        result.success(value);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void addJavaScriptChannels(MethodCall methodCall, MethodChannel.Result result) {
        List<String> channelNames = (List<String>) methodCall.arguments;
        registerJavaScriptChannelNames(channelNames);
        result.success(null);
    }

    @SuppressWarnings("unchecked")
    private void removeJavaScriptChannels(MethodCall methodCall, MethodChannel.Result result) {
        List<String> channelNames = (List<String>) methodCall.arguments;
        for (String channelName : channelNames) {
            x5WebView.removeJavascriptInterface(channelName);
        }
        result.success(null);
    }

    private void clearCache(MethodChannel.Result result) {
        x5WebView.clearCache(true);
        WebStorage.getInstance().deleteAllData();
        result.success(null);
    }

    private void getTitle(MethodChannel.Result result) {
        result.success(x5WebView.getTitle());
    }

    private void scrollTo(MethodCall methodCall, MethodChannel.Result result) {
        Map<String, Object> request = methodCall.arguments();
        int x = (int) request.get("x");
        int y = (int) request.get("y");

        x5WebView.scrollTo(x, y);

        result.success(null);
    }

    private void scrollBy(MethodCall methodCall, MethodChannel.Result result) {
        Map<String, Object> request = methodCall.arguments();
        int x = (int) request.get("x");
        int y = (int) request.get("y");

        x5WebView.scrollBy(x, y);
        result.success(null);
    }

    private void getScrollX(MethodChannel.Result result) {
        result.success(x5WebView.getScrollX());
    }

    private void getScrollY(MethodChannel.Result result) {
        result.success(x5WebView.getScrollY());
    }

    private void applySettings(Map<String, Object> settings) {
        for (String key : settings.keySet()) {
            switch (key) {
                case "jsMode":
                    Integer mode = (Integer) settings.get(key);
                    if (mode != null) updateJsMode(mode);
                    break;
                case "hasNavigationDelegate":
                    final boolean hasNavigationDelegate = (boolean) settings.get(key);

                    break;
                case "debuggingEnabled":
                    final boolean debuggingEnabled = (boolean) settings.get(key);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        x5WebView.setWebContentsDebuggingEnabled(debuggingEnabled);
                    }
                    break;
                case "gestureNavigationEnabled":
                    break;
                case "userAgent":
                    updateUserAgent((String) settings.get(key));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown WebView setting: " + key);
            }
        }
    }

    private void updateJsMode(int mode) {
        switch (mode) {
            case 0: // disabled
                x5WebView.getSettings().setJavaScriptEnabled(false);
                break;
            case 1: // unrestricted
                x5WebView.getSettings().setJavaScriptEnabled(true);
                break;
            default:
                throw new IllegalArgumentException("Trying to set unknown JavaScript mode: " + mode);
        }
    }

    private void updateAutoMediaPlaybackPolicy(int mode) {
        // This is the index of the AutoMediaPlaybackPolicy enum, index 1 is always_allow, for all
        // other values we require a user gesture.
        boolean requireUserGesture = mode != 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            x5WebView.getSettings().setMediaPlaybackRequiresUserGesture(requireUserGesture);
        }
    }

    private void registerJavaScriptChannelNames(List<String> channelNames) {
        for (String channelName : channelNames) {
            x5WebView.addJavascriptInterface(
                    new JavaScriptChannel(methodChannel, channelName, platformThreadHandler), channelName);
        }
    }

    private void updateUserAgent(String userAgent) {
        x5WebView.getSettings().setUserAgentString(userAgent);
    }


}

