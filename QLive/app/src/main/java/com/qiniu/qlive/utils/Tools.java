package com.qiniu.qlive.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.Toast;

import com.qiniu.qlive.config.Config;
import com.qiniu.qlive.config.Session;

import java.security.MessageDigest;

/**
 * Created by jemy on 11/2/15.
 */
public class Tools {
    public static String md5Hash(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createAccessToken(String sessionId) {
        long timestamp = System.currentTimeMillis() / 1000;
        String ts = String.format("%d", timestamp);
        String encodedTs = Base64.encodeToString(ts.getBytes(), Base64.URL_SAFE);
        String toSign = String.format("%s:%s:%s", sessionId, ts, sessionId);
        String accessToken = String.format("%s:%s", Tools.md5Hash(toSign), encodedTs);
        return accessToken;
    }

    public static void writeSession(Context context, String sessionId, String sessionUserName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("qlive", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Config.SESSION_ID, sessionId);
        editor.putString(Config.SESSION_USER_NAME, sessionUserName);
        editor.commit();
    }

    public static Session getSession(Context context) {
        Session session = null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("qlive", Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Config.SESSION_ID, "");
        String sessionUserName = sharedPreferences.getString(Config.SESSION_USER_NAME, "");
        if (!sessionId.isEmpty() && !sessionUserName.isEmpty()) {
            session = new Session(sessionId, sessionUserName);
        }
        return session;
    }

    public static void showToast(final Context context, final String msg) {
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
