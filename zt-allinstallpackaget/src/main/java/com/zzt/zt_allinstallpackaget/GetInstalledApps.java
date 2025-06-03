package com.zzt.zt_allinstallpackaget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GetInstalledApps {
    private static final String TAG = GetInstalledApps.class.getSimpleName();

    public static void getApps(Context context) {
        List<AppInfo> installedApps = getInstalledApps(context);

        for (AppInfo installedApp : installedApps) {
            Log.w(TAG, "APPS> name:" + installedApp.appName + " pk:" + installedApp.packageName);

        }

        List<MobileAppInfo> apps2 = getAppInfo(context);

        for (MobileAppInfo app : apps2) {
            Log.d(TAG, "APPS> name:" + app.getName() + " pk:" + app.getPageName());

        }
    }

    public static List<MobileAppInfo> getAppInfo(Context context) {
        if (context == null) {
            return null;
        }
        List<MobileAppInfo> appObjList = new ArrayList<>();
        //得到应用packageManager
        PackageManager packageManager = context.getPackageManager();
        //创建一个主界面intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        //得到包含应用信息列表
        List<ResolveInfo> ResolveInfos = packageManager.queryIntentActivities(intent, 0);
        //遍历
        for (ResolveInfo ri : ResolveInfos) {
            ComponentInfo componentInfo = getComponentInfo(ri);
            // 获取包名
            String appPK = "";
            if (componentInfo != null) {
                appPK = componentInfo.packageName;
            }
            // 得到应用名称
            String appName = ri.loadLabel(packageManager).toString();
            if (TextUtils.isEmpty(appPK)) {
                break;
            }
            try {
                Log.w(TAG, "应用 ：" + appPK);
                PackageInfo mPackageInfo = packageManager.getPackageInfo(appPK, 0);
                if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    Log.w(TAG, "应用 第三方 ：" + appPK);
                    //第三方应用
                    appObjList.add(new MobileAppInfo(appName, appPK));
                } else {
                    //系统应用
                    Log.w(TAG, "应用 系统 ：" + appPK);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return appObjList;
    }

    /**
     * 获取应用名称
     *
     * @param ri
     * @return
     */
    private static ComponentInfo getComponentInfo(ResolveInfo ri) {
        if (ri.activityInfo != null) return ri.activityInfo;
        if (ri.serviceInfo != null) return ri.serviceInfo;
        if (ri.providerInfo != null) return ri.providerInfo;
        return null;
    }

    public static List<AppInfo> getInstalledApps(Context context) {
        List<AppInfo> appList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

        for (PackageInfo packageInfo : installedPackages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                String appName = (String) packageManager.getApplicationLabel(applicationInfo);
                String packageName = packageInfo.packageName;
                Drawable appIcon = packageManager.getApplicationIcon(applicationInfo);
                appList.add(new AppInfo(appName, packageName, appIcon));
            }
        }
        return appList;
    }

    public static class AppInfo {
        private String appName;
        private String packageName;
        private Drawable appIcon;

        public AppInfo(String appName, String packageName, Drawable appIcon) {
            this.appName = appName;
            this.packageName = packageName;
            this.appIcon = appIcon;
        }

        public String getAppName() {
            return appName;
        }

        public String getPackageName() {
            return packageName;
        }

        public Drawable getAppIcon() {
            return appIcon;
        }
    }
}    