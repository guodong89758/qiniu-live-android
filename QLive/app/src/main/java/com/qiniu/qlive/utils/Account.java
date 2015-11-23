package com.qiniu.qlive.utils;

import android.content.Context;
import android.util.Log;

import com.qiniu.qlive.config.Config;

import java.io.FileOutputStream;

/**
 * Created by jemy on 11/7/15.
 */
public class Account {
    public static void save(Context context, String phone, String password) {
        Log.d("QLIVE", "save login account");
        try {
            FileOutputStream fos = context.openFileOutput(Config.accountFileName, Context.MODE_PRIVATE);
            StringBuilder data = new StringBuilder();
            data.append("{\"mobile\":\"").append(phone).append("\",\"pwd\":\"").append(password).append("\"}");
            fos.write(data.toString().getBytes());
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("QLIVE", "save login account failed");
        }
    }

}
