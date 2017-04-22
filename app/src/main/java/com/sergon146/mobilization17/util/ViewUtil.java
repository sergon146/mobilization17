package com.sergon146.mobilization17.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ViewUtil {

    public static void animateClick(Context context, View v, int andimId) {
        Animation rotate = AnimationUtils.loadAnimation(context, andimId);
        v.startAnimation(rotate);
    }
}
