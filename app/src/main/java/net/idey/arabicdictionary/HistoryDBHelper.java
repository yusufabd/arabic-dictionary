package net.idey.arabicdictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *   3/6/2016.
 */
public class HistoryDBHelper extends SQLiteOpenHelper{

    /*
    Database for viewed words history.
     */

    Context context;
    SQLiteDatabase database;

    private static final String DB_Name = "history";
    private static final int DB_Version = 1;
    private static final String TABLE_LIST = "list";
    private static final String TABLE_BOOKMARKS = "bookmarks";
    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE list (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "eng TEXT," +
            "arab TEXT);";
    private static final String CREATE_TABLE_BOOKMARKS = "CREATE TABLE bookmarks (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "eng TEXT," +
            "arab TEXT);";

    public HistoryDBHelper (Context c){
        super(c, DB_Name, null, DB_Version);
        database = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HISTORY);
        db.execSQL(CREATE_TABLE_BOOKMARKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insertWord(String eng, String arab){
        /*
        Inserting words to table via content values
         */

        ContentValues cv = new ContentValues();
        cv.put("eng", eng);
        cv.put("arab", arab);
        database.insertWithOnConflict(TABLE_LIST, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Cursor showHistory(){
        /*
        Showing all records in table
         */
        Cursor cursor = null;
        String query = "SELECT * FROM list";
        cursor = database.rawQuery(query, null);
        return cursor;
    }

    public void cleanHistory(){
        database.execSQL("DELETE FROM list");
    }

    public Cursor showBookmarks(){
        /*
        Showing all records in table
         */
        Cursor c = null;
        String query = "SELECT * FROM bookmarks";
        c = database.rawQuery(query, null);
        return c;
    }

    public void insertBookmark(String eng, String arab){
        ContentValues cv = new ContentValues();
        cv.put("eng", eng);
        cv.put("arab", arab);
        database.insertWithOnConflict(TABLE_BOOKMARKS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean checkIfBookmarkExists(String eng){
        String query = "SELECT _id as _id, eng, arab FROM bookmarks WHERE eng LIKE '" + eng + "';";
        Cursor cur = database.rawQuery(query, null);
        if (cur.getCount()<=0){
            cur.close();
            return false;
        }
        cur.close();
        return true;
    }
    public void deleteBookmark(String eng){
        database.execSQL("DELETE FROM bookmarks WHERE eng LIKE '" + eng + "';");
    }
    public void cleanBookmarks(){
        database.execSQL("DELETE FROM bookmarks");
    }

}
