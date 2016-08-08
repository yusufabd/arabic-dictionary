package net.idey.arabicdictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 *   2/23/2016.
 */
public class MyDatabase extends SQLiteAssetHelper{

    /*
    Class extends SQLiteAssetHelper (not SQLiteOpenHelper). It's library for reading already existing database
     */

    SQLiteDatabase db;

    private static final String DATABASE_NAME = "dict.sqlite";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DATA = "data";
    private static final String TABLE_PROPERTIES = "properties";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_SHORT = "short";



    public MyDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }


    public Cursor getWordsList(String word){
        /*
        Showing all words that start with entered text
         */
        Cursor cursorWord = null;
        String query = "SELECT idx as _id, word, short FROM data WHERE word LIKE '" + word + "%' LIMIT 100";
        cursorWord = db.rawQuery(query, null);
        return cursorWord;
    }

}
