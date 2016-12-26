package com.and2long.mplayer.util;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by L on 2016/12/3.
 */

public class CursorUtil {

    private static final String TAG = "CursorUtil";

    public static void printCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        Log.i(TAG, "printCursor: " + "共" + cursor.getCount() + "条记录");
        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                String columnValue = cursor.getString(i);
                Log.i(TAG, columnName + ":" + columnValue);
            }
            Log.i(TAG, "===============");
        }

    }
}
