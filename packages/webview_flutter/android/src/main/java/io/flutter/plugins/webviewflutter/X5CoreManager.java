package io.flutter.plugins.webviewflutter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.lang.reflect.Field;
import java.util.HashMap;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class X5CoreManager  implements MethodChannel.MethodCallHandler {
    private final MethodChannel methodChannel;
    private final  Context context;
    ///x5浏览器初始化
    private int x5LoadStatus = -1; // -1 未加载状态  5 成功 10 失败
    X5CoreManager(BinaryMessenger messenger,Context context){
        this.context=context;
        methodChannel = new MethodChannel(messenger, "plugins.flutter.io/x5core_manager");
        methodChannel.setMethodCallHandler(this);
    }
    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        switch (methodCall.method) {
            case "initX5Core":
                initX5Core(context);
                break;
            default:
                result.notImplemented();
        }
    }


    ///初始化x5
    public  void initX5Core(Context context) {
        Log.e("FileReader", "初始化X5");
        if(context==null) return;
        if (!QbSdk.canLoadX5(context)) {
            //重要
            QbSdk.reset(context);
        }
        QbSdkPreInitCallback preInitCallback = new QbSdkPreInitCallback();
        // 在调用TBS初始化、创建WebView之前进行如下配置，以开启优化方案
        HashMap<String, Object> map = new HashMap<String, Object>();
        //map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        //map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setNeedInitX5FirstTime(true);
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.e("FileReader", "TBS下载完成");
            }

            @Override
            public void onInstallFinish(int i) {
                Log.e("FileReader", "TBS安装完成");

            }

            @Override
            public void onDownloadProgress(int i) {
                Log.e("FileReader", "TBS下载进度:" + i);
            }
        });

        QbSdk.initX5Environment(context, preInitCallback);


    }


    class QbSdkPreInitCallback implements QbSdk.PreInitCallback {

        @Override
        public void onCoreInitFinished() {
            Log.e("FileReader", "TBS内核初始化结束");
        }

        @Override
        public void onViewInitFinished(boolean b) {
            if (context == null) {
                return;
            }
            if (b) {
                x5LoadStatus = 5;
                Log.e("FileReader", "TBS内核初始化成功" + "--" + QbSdk.canLoadX5(context));
            } else {
                x5LoadStatus = 10;
                resetQbSdkInit();
                Log.e("FileReader", "TBS内核初始化失败" + "--" + QbSdk.canLoadX5(context));
            }
            //onX5LoadComplete();
        }
    }
    ///反射 重置初始化状态(没网情况下加载失败)
    private void resetQbSdkInit() {
        try {
            Field field = QbSdk.class.getDeclaredField("s");
            field.setAccessible(true);
            field.setBoolean(null, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    void dispose() {
        methodChannel.setMethodCallHandler(null);
    }
}
