package com.github.ggggxiaolong.xmpp.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 测量工具类
 * 本类依赖Context变量，在使用前确认
 */

public final class MeasureUtil {

    public static int[] getScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ObjectHolder.context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    public static int dip2px(float dpValue) {
        final float scale = ObjectHolder.context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}