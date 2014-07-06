package net.cubitum.fortylife.util;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import net.cubitum.fortylife.R;

public class Helpers {
    public static String getOwnerDisplayName(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            String[] columnNames = new String[]{ContactsContract.Profile.DISPLAY_NAME};

            Cursor c = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, columnNames, null, null, null);
            c.moveToFirst();
            String name = c.getString(0);
            c.close();
            return name;
        } else {
            return context.getResources().getString(R.string.pref_default_player_name);
        }
    }
}
