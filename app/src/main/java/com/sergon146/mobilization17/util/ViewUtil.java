package com.sergon146.mobilization17.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ViewUtil {

    /**
     * Animate View by click
     *
     * @param view   animated view
     * @param animId animation resource id
     */
    public static void animateClick(View view, int animId) {
        Animation rotate = AnimationUtils.loadAnimation(view.getContext(), animId);
        view.startAnimation(rotate);
    }
}
