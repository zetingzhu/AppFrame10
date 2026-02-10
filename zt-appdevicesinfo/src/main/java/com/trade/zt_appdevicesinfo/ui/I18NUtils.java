package com.trade.zt_appdevicesinfo.ui;

/**
 * Created by dufangzhu on 2017/6/20.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;


import java.util.Locale;
import java.util.TimeZone;

/**
 * 时区语言工具类
 * <p>
 * app的语言和默认选中的是当前手机语言
 * 时间格式默认手机时区一样  GMT时间  时间统一使用long时间转换就没问题
 * <p>
 * mark:
 * Android7.0 优化了对多语言的支持
 * 废弃了Resources 的updateConfiguration()方法，
 * 替代方法：Context 的createConfigurationContext(),返回的是Context。
 * 也就是语言需要植入到Context中，每个Context都植入一遍 每个页面的Context 包括Application
 * <p>
 * activity 中 attachBaseContext
 * Application 中 attachBaseContext
 * activity中对收到切换语言的事件recreate
 */
public class I18NUtils {
    protected static final String TAG = "I18NUtils";
    private static final String SETTING_KEY = "SETTING_KEY_I18NUtils";
    private static final String DISPLAY_NAME_KEY = "DISPLAY_NAME_KEY";

    public static final String SPLIT = "-";

    /*没有在列表里找到对应的语言，默认使用的语言*/
    public static final String DFT_LANGUAGE_COUNTRY = "en-US";

    public static final String LANGUAGE_ZH_HK = "zh-HK";

    public static final String LANGUAGE_ZH_TW = "zh-TW";
    // 华为手机越南语叫vi-CN 和android不同
    public static final String LANGUAGE_VI_VN = "vi-VN";
    public static final String LANGUAGE_RU_RU = "ru-RU";
    public static final String LANGUAGE_VI_VN_HUAWEI = "vi";

    // 印度尼西亚 就 iso 代码转新代码 id-ID -> in-ID
    public static final String LANGUAGE_ID_ID = "id-ID";
    public static final String LANGUAGE_IN_ID = "in-ID";
    public static final String LANGUAGE_AR = "ar";
    public static final String LANGUAGE_AR_AR = "ar-AR";
    public static final String LANGUAGE_PT = "pt";
    public static final String LANGUAGE_PT_PT = "pt-PT";
    public static final String LANGUAGE_KO = "ko";
    public static final String LANGUAGE_KO_KR = "ko-KR";

    public static final String LANGUAGE_TH = "th";
    public static final String LANGUAGE_TH_TH = "th-TH";

    public static final String LANGUAGE_ES = "es";
    public static final String LANGUAGE_ES_ES = "es-ES";


    public static final String LANGUAGE_VI = "vi";

    public static final String LANGUAGE_MS = "ms";
    public static final String LANGUAGE_MS_MY = "ms-MY";

    public static final String LANGUAGE_ZH_CN = "zh-CN";
    public static final String LANGUAGE_JA = "ja";
    public static final String LANGUAGE_JA_JP = "ja-JP";


    /**
     * 获取当前手机的时区
     *
     * @return
     */
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String strTz = tz.getDisplayName(false, TimeZone.SHORT);
        return strTz;

    }

    /**
     * 得到手机系统的语言设置
     *
     * @param mContext
     * @return zh-CN
     */
    public static String getSystemLC(Context mContext) {
        //locale = Locale.getDefault();
        Locale locale = mContext.getResources().getConfiguration().locale;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0的手机是通过LocaleList来管理的
                locale = LocaleList.getDefault().get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String script = locale.getScript();
        String lc = language + SPLIT + country;
        Log.e(TAG, "sysLC=" + lc + "script=" + script);
        return lc;
    }

    public static String getSystemFixScript(Context mContext) {
        //locale = Locale.getDefault();
        Locale locale = mContext.getResources().getConfiguration().locale;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0的手机是通过LocaleList来管理的
                locale = LocaleList.getDefault().get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String script = locale.getScript();
//        String lc = language + SPLIT + country;
//        Log.e(TAG, "sysLC=" + lc + "script="+script);
        Log.e(TAG, "sysLC=" + script + "script=" + script);
        return script;
    }

    /**
     * 获取手机room语言
     *
     * @param mContext
     * @return
     */
    public static String getPhoneRoomLocal(Context mContext) {
        Resources resources = Resources.getSystem();
        Configuration configuration = resources.getConfiguration();
        if (configuration != null) {
            Locale locale = null;
            LocaleList localeList = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                localeList = configuration.getLocales();
                if (localeList.size() > 0) {
                    locale = localeList.get(0);
                }
                Log.e(TAG, "sysLC localeList=" + localeList);
            } else {
                locale = configuration.locale;
            }
            Log.e(TAG, "sysLC locale=" + locale);
            if (locale != null) {
                String language = locale.getLanguage();
                String country = locale.getCountry();
                String lc = language + SPLIT + country;
                Log.e(TAG, "room sysLC=" + lc);
                return lc;
            }
        }
        return getSystemLC(mContext);
    }

    /**
     * 获取app设置的Preference的语言
     *
     * @param mContext
     * @return zh-CN
     */
    public static String getCacheLC(Context mContext) {
        String cacheLanguage = PreferenceSetting.getString(mContext, SETTING_KEY);
        return cacheLanguage;
    }

    /**
     * LanguageCountry  --> LC
     *
     * @param mContext
     * @param code
     */
    public static void setCacheLC(Context mContext, String code) {
        PreferenceSetting.setString(mContext, SETTING_KEY, code);
    }

    /**
     * 获取当前app的语言格式
     * 先获取用户已经选择的项目
     * 默认使用手机系统
     *
     * @param mContext
     * @return language-country;  zh-CN
     */
    public static String getLanguageCountry(Context mContext) {
        String cacheLanguage = getCacheLC(mContext);
        if (!CLStringUtil.isEmpty(cacheLanguage))
            return cacheLanguage;
        //使用系统的
        return getSystemLC(mContext);
    }

    /**
     * 请求接口使用，  in-ID  -> id-ID 特殊转换
     *
     * @param mContext
     * @return
     */
    public static String getLanguageCountryNet(Context mContext) {
        String cacheLanguage = getCacheLC(mContext);
        if (LANGUAGE_IN_ID.equals(cacheLanguage)) {
            // in-ID  -> id-ID
            cacheLanguage = LANGUAGE_ID_ID;
        }
        if (!CLStringUtil.isEmpty(cacheLanguage))
            return cacheLanguage;
        //使用系统的
        return getSystemLC(mContext);
    }

    /**
     * 获取缓存的显示的语言 简体中文 english
     *
     * @param mContext
     * @return
     */
    public static String getDisplayName(Context mContext) {
        String cacheLanguage = PreferenceSetting.getString(mContext, DISPLAY_NAME_KEY);
        if (!CLStringUtil.isEmpty(cacheLanguage))
            return cacheLanguage;
        return "";
    }

    /**
     * @param mContext
     * @param name     简体中文 english
     */
    public static void setDisplayName(Context mContext, String name) {
        PreferenceSetting.setString(mContext, DISPLAY_NAME_KEY, name);
    }

    /**
     * 切换语言
     *
     * @param mContext
     * @param locale   new Locale(String language, String country)
     */
    private static Context updateLocal(Context mContext, Locale locale) {
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        /**
         * 屏幕适配，保持所有屏幕显示统一
         * TODO: 字体不会跟随系统设置变大变小，解决方案
         * TODO: float targetScaledDensity = targetDensity * (appDisplayMetrics.scaledDensity / appDisplayMetrics.density)
         * TODO: 为了防止文字放不下，没有采取此解决方案，强制用户使用统一大小，可根据后续需求改动
         */
        dm = mContext.getResources().getDisplayMetrics();
        Log.d(TAG, "修改之前的密度 dm.density= " + dm.density);
        Log.d(TAG, "修改之前的密度 dm.densityDpi= " + dm.densityDpi);
        Log.d(TAG, "修改之前的密度 dm.scaledDensity= " + dm.scaledDensity);

        if (isEnableUpdateDensity()) {
            dm.density = dm.scaledDensity = ((float) (dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels)) / 411;
            Log.e(TAG, "dm.density===" + dm.density);
            dm.densityDpi = (int) (dm.density * 160);
            Log.e(TAG, "dm.density===" + dm.densityDpi);
            config.densityDpi = (int) (dm.density * 160);

            Log.w(TAG, "修改之后的密度 dm.density= " + dm.density);
            Log.w(TAG, "修改之后的密度 dm.densityDpi= " + dm.densityDpi);
            Log.w(TAG, "修改之后的密度 dm.scaledDensity= " + dm.scaledDensity);
        }

        //让手机设置超大字体无效
        config.fontScale = 1;//为什么是1，从config.setToDefaults();找出的。千万不要使用config.setToDefaults()
        //        Log.i(TAG, "修改语言前：" + config.locale + " 修改修改：" + locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            mContext = mContext.createConfigurationContext(config);
            //            Log.i(TAG, "修改方法1-0：" + config.getLocales());
            //            Log.i(TAG, "修改方法1-1：" + mContext.getResources().getConfiguration().getLocales());
            try {
                // 解决部分低版本手机，多次重复设置语言会失败
                if (!config.getLocales().equals(mContext.getResources().getConfiguration().getLocales())) {
                    Log.i(TAG, "修改方法1-2 某些手机没有修改成功，在用老方法设置一遍");
                    config.setLocale(locale);
                    mContext.getResources().updateConfiguration(config, dm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 应用用户选择语言
            //对版本进行兼容处理
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            resources.updateConfiguration(config, dm);
        }

        return mContext;
    }

    /**
     * 当前设备是否支持修改密度
     */
    public static boolean isEnableUpdateDensity() {
        if (RomUtils.isHonor() && Build.VERSION.SDK_INT >= 36) {
            // 荣耀 android 16 就不修改了，
            return false;
        }
        return true;
    }

    /**
     * 修改暗黑模式系统配置，并且确保国际化可以正常使用
     */
    public static void setSystemUiModeConfiguration(Context mContext, Configuration newConfig) {
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (newConfig != null) {
            config.uiMode = newConfig.uiMode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mContext.createConfigurationContext(config);
            } else {
                resources.updateConfiguration(config, dm);
            }
        }
    }

    /**
     * 修改暗黑模式后，需要调整屏幕密度
     */
    public static void setScreenDensity(Resources resources) {
        if (isEnableUpdateDensity()) {
            DisplayMetrics dm = resources.getDisplayMetrics();
            dm.density = dm.scaledDensity = ((float) (dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels)) / 411;
            dm.densityDpi = (int) (dm.density * 160);
            Log.e(TAG, "设置密度 dm.density===" + dm.density + "  dm.densityDpi===" + dm.densityDpi);
        }
    }

    /**
     * 更新app资源的语言
     *
     * @param mContext
     */
    public static Context updateLocal(Context mContext) {
        String lc = getLanguageCountry(mContext);
        /*zh-CN*/
        if (lc.contains(SPLIT)) {
            String str[] = lc.split(SPLIT);
            if (str.length < 2)
                return mContext;
            Log.v(TAG, "language=" + str[0] + " country=" + str[1]);
            return updateLocal(mContext, new Locale(str[0], str[1]));
        }
        return mContext;
    }

    public static Locale getLocale(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String lc = getLanguageCountry(context);
        if (lc.contains(SPLIT)) {
            String str[] = lc.split(SPLIT);
            if (str.length < 1)
                return locale;
            locale = new Locale(str[0], str[1]);
        }
        return locale;
    }

}