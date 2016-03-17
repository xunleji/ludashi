package com.ludashi.mains;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BookMarkDBHelper extends SQLiteOpenHelper {
	
    public static final String TB_NAME = "tablename";
    public static final String BOOK_NAME = "bookname";
    public static final String MARK_NAME = "markname";
    public static final String POSITION = "_id";
    
    
    public BookMarkDBHelper(Context context, String name, 
            CursorFactory factory,int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        // 创建表countrycode
        db.execSQL("CREATE TABLE IF NOT EXISTS " 
                + TB_NAME + " (" 
        		+ POSITION + " INTEGER PRIMARY KEY," 
        	    + MARK_NAME + " VARCHAR,"
        	    + BOOK_NAME + " VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, 
            int oldVersion, int newVersion) {
        
        //删除以前的旧表，创建一张新的空表
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }
}
