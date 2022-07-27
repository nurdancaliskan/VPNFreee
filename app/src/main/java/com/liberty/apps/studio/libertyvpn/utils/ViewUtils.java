package com.liberty.apps.studio.libertyvpn.utils;

import android.content.res.Resources;

public class ViewUtils {

    public static Float convertPxToDP(float value) {
        return  (value / Resources.getSystem().getDisplayMetrics().density);
    }

    public static Float convertDpToPx(float value) {
        return  (value * Resources.getSystem().getDisplayMetrics().density);
    }
}
