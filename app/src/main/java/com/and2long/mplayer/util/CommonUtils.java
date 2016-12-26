package com.and2long.mplayer.util;

import com.and2long.mplayer.base.App;

/**
 * Created by L on 2016/12/3.
 */

public class CommonUtils {

    public static void runOnUIThread(Runnable runnable) {
        App.getMainHandler().post(runnable);
    }


}
