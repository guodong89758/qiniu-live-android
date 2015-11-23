package com.qiniu.qlive.utils;

/**
 * Created by jemy on 11/1/15.
 */

import android.os.Handler;
import android.os.Looper;

/**
 * Created by bailong on 14/10/22.
 */
public final class AsyncRun {
    public static void run(Runnable r) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(r);
    }
}