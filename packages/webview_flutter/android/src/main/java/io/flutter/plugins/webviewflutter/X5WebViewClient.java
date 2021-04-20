package io.flutter.plugins.webviewflutter;

import android.graphics.Bitmap;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
///x5的webClient
public class X5WebViewClient  extends WebViewClient {
    private final MethodChannel methodChannel;
    X5WebViewClient(MethodChannel methodChannel) {
        this.methodChannel = methodChannel;
    }
    @Override
    public void onPageStarted( WebView view, String url, Bitmap var3) {
        super.onPageStarted(view,url,var3);
        Map<String, Object> args = new HashMap<>();
        args.put("url", url);
        methodChannel.invokeMethod("onPageStarted", args);
    }
    @Override
    public void onPageFinished(WebView webView, String url) {
        Map<String, Object> args = new HashMap<>();
        args.put("url", url);
        methodChannel.invokeMethod("onPageFinished", args);
    }
    /**
     * 防止加载网页时调起系统浏览器
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        webView.loadUrl(url);
        return true;
    }
    /**
     * 防止加载网页时调起系统浏览器
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        webView.loadUrl(webResourceRequest.getUrl().toString());
        return true;
    }
}
