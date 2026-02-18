package com.cleverestech.apps.suresim;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.telephony.euicc.DownloadableSubscription;
import android.telephony.euicc.EuiccManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ModuleMain implements IXposedHookLoadPackage {
    private static final String TAG = "SureSimModule";
    private Application application;
    private ClipboardManager clipboardManager;

    @Override
    @SuppressLint("NewApi")
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                application = (Application) param.thisObject;
                clipboardManager = (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
                Log.d(TAG, "Application onCreate hooked");
            }
        });

        XposedHelpers.findAndHookMethod(EuiccManager.class, "isEnabled", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "EuiccManager.isEnabled hooked - original result: " + param.getResult());
                }
                param.setResult(true);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "EuiccManager.isEnabled - result set to true");
                }
            }
        });

        // Using reflection for DownloadableSubscription
        Class<?> downloadableSubscriptionClass = XposedHelpers.findClass("android.telephony.euicc.DownloadableSubscription", lpparam.classLoader);

        XposedHelpers.findAndHookMethod(downloadableSubscriptionClass, "getEncodedActivationCode", new XC_MethodHook() {
            @SuppressLint("DiscouragedPrivateApi")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(TAG, "DownloadableSubscription.getEncodedActivationCode hooked");

                Application application = AndroidAppHelper.currentApplication();
                if (application == null) {
                    Log.e(TAG, "Application is null");
                    return;
                }

                ClipboardManager clipboardManager = (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboardManager == null) {
                    Log.e(TAG, "ClipboardManager is null");
                    return;
                }

                String activationCode = (String) param.getResult();
                Log.d(TAG, "Encoded activation code: " + activationCode);

                if (activationCode != null) {
                    ClipData clip = ClipData.newPlainText("Encoded eSIM activation code", activationCode);
                    clipboardManager.setPrimaryClip(clip);
                    Log.d(TAG, "Activation code copied to clipboard");
                }
            }
        });
    }
}
