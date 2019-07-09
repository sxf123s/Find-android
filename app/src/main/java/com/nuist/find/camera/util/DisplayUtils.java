package com.nuist.find.camera.util;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayUtils {
    /**
     * 生成屏幕中间的矩形
     * w 目标矩形的宽度，单位px
     * h 目标矩形的高度，单位px
     */
    public static Rect createCenterScreenRect(int viewWidth, int viewHeight, int w, int h) {
        int x1 = viewWidth / 2 - w / 2;
        int y1 = viewHeight / 2 - h / 2;
        int x2 = x1 + w;
        int y2 = y1 + h;
        return new Rect(x1, y1, x2, y2);
    }
     //生成拍照后图片的中间矩形的宽度和高度
    public static Point createCenterPictureRect(float ratio, float cameraRatio, int picSizeX, int picSizeY) {
        int wRectPicture;
        int hRectPicture;
        if (ratio > cameraRatio) {
            hRectPicture = picSizeY;
            wRectPicture = (int) (picSizeY / ratio);
        } else {
            wRectPicture = picSizeX;
            hRectPicture = (int) (picSizeX * ratio);
        }
        return new Point(wRectPicture, hRectPicture);
    }

    //记录屏幕信息
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }

    public static float getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static float getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }
}
