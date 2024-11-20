package com.cleverestech.apps.suresim;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.telephony.euicc.DownloadableSubscription;
import android.telephony.euicc.EuiccManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ModuleMain implements IXposedHookLoadPackage {
    private static Application application;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.isFirstApplication) {
            return;
        }

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                application = (Application) param.thisObject;
            }
        });

        XposedHelpers.findAndHookMethod(EuiccManager.class, "isEnabled", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });

        XposedHelpers.findAndHookMethod(DownloadableSubscription.class, "forActivationCode", String.class, new XC_MethodHook() {
            @SuppressLint("DiscouragedPrivateApi")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (application == null) {
                    return;
                }

                DownloadableSubscription subscription = (DownloadableSubscription) param.getResult();
                if (subscription == null) {
                    return;
                }

                ClipboardManager clipboardManager = (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboardManager != null) {
                    ClipData clip = ClipData.newPlainText("Encoded eSIM activation code", subscription.getEncodedActivationCode());
                    clipboardManager.setPrimaryClip(clip);
                }
            }
        });
    }
}
