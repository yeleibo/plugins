// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.webviewflutter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;
import java.util.Map;

public final class WebViewFactory extends PlatformViewFactory {
  private final BinaryMessenger messenger;
  private final View containerView;

  WebViewFactory(BinaryMessenger messenger, View containerView) {
    super(StandardMessageCodec.INSTANCE);
    this.messenger = messenger;
    this.containerView = containerView;
  }

  @SuppressWarnings("unchecked")
  @Override
  public PlatformView create(Context context, int id, Object args) {
    Map<String, Object> params = (Map<String, Object>) args;
    if(QbSdk.isTbsCoreInited()){
      Log.e("FileReader", "加载了X5浏览器" );
      return  new X5WebView(context, messenger, id, params, containerView);
    }
    Log.e("FileReader", "加载了系统浏览器" );
    return new FlutterWebView(context, messenger, id, params, containerView);
  }
}
