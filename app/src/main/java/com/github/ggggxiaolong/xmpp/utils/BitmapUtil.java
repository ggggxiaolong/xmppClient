package com.github.ggggxiaolong.xmpp.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.math.BigDecimal;

/**
 * bitmap处理类
 */

public final class BitmapUtil {
    /**
     * 缩小图片
     *
     * @param bitmap        源图片
     * @param requestWidth  期望宽度
     * @param requestHeight 期望高度
     * @param isAdjust      是否等比缩放
     * @return 缩小的图片
     */
    public static Bitmap compressBitmap(Bitmap bitmap, float requestWidth, float requestHeight, boolean isAdjust) {
        if (bitmap == null | requestHeight == 0 | requestWidth == 0) return bitmap;

        //判断图片是否需要缩小
        if (bitmap.getHeight() > requestHeight | bitmap.getWidth() > requestWidth) {
            float scaleX = new BigDecimal(requestWidth).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();
            float scaleY = new BigDecimal(requestHeight).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();
            if (isAdjust) {
                scaleX = Math.min(scaleX, scaleY);
                scaleY = scaleX;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(scaleX, scaleY);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }
}
