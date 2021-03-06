/**
 * 
 */

package com.linlin.thiefdefender;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author Tim.Lian
 */
public class ThiefDefenderStore {

    private static final String TAG = ThiefDefenderStore.class.getSimpleName();

    // schema
    private static final String C_ID = BaseColumns._ID;

    private static final String C_START_TIME = "start_time";

    private static final String C_END_TIME = "end_time";

    private static final String C_DURATION = "duration";

    private static final String C_TRIGGER = "trigger";

    private static final int DB_VERSION = 1;

    private static final String DB_NAME = "thief_defender.db";

    private static final String TABLE_NAME = "alert";

    private final DbHelper dbHelper;

    private static ThiefDefenderStore defaultStore;

    public static ThiefDefenderStore getDefaultStore(Context context) {
        if (defaultStore == null) {
            defaultStore = new ThiefDefenderStore(context);
        }
        return defaultStore;
    }

    private ThiefDefenderStore(Context context) {
        dbHelper = new DbHelper(context);
        Log.i(TAG, "Initialized data");
    }

    public long insertOrIgnore(ContentValues values) {
        Log.d(TAG, "insertOrIgnore on " + values);
        long retID = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            retID = db.insertWithOnConflict(TABLE_NAME, null, values,
                    SQLiteDatabase.CONFLICT_IGNORE);
        } catch (Exception e) {
            Log.d(TAG, "Insertion failed: " + e.getLocalizedMessage());
        } finally {
            db.close();
        }
        return retID;
    }

    public boolean update(ContentValues values, long id) {
    	 Log.d(TAG, "update on " + values + " where _id=" + id);
         int affectedRows = -1;
         SQLiteDatabase db = dbHelper.getWritableDatabase();
         try {
             String whereClause = C_ID + "=" + id;
             affectedRows = db.update(TABLE_NAME, values, whereClause, null);
         } catch (Exception e) {
             Log.d(TAG, "Update failed: " + e.getLocalizedMessage());
         } finally {
             db.close();
         }
         return ((affectedRows == 1)? true : false);
    }
    
    public String getAlertStartDate(long id) {
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	try {
            Cursor cursor = db.query(TABLE_NAME, new String[]{C_START_TIME}, C_ID + "=" + id, null, null, null, null);
            try {
                return cursor.moveToNext() ? cursor.getString(0) : null;
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }
    
    class DbHelper extends SQLiteOpenHelper {
        static final String TAG = "DbHelper";

        Context context;

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context = context;
        }

        // Called only once, first time the DB is created
        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + TABLE_NAME + " (" + C_ID + " integer primary key, "
                    + C_START_TIME + " text, " + C_END_TIME + " text, " + C_DURATION + " text, "
                    + C_TRIGGER + " text)";
            db.execSQL(sql);
            Log.d(TAG, "onCreated sql: " + sql);
        }

        // Called whenever newVersion != oldVersion
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME); // drops the old
            // database
            Log.d(TAG, "onUpgrade");
            onCreate(db);
        }
    }
}
