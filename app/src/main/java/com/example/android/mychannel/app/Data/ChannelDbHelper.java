package com.example.android.mychannel.app.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by abdo on 14/04/15.
 */
public class ChannelDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "myChannel.db";

    public ChannelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase) {
        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + ChannelContract.VideosEntry.TABLE_NAME + " (" +
                ChannelContract.VideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ChannelContract.VideosEntry.COLUMN_TITLE + " TEXT NOT NULL,  " +
                ChannelContract.VideosEntry.COLUMN_PUB_DATE + " TEXT NOT NULL, " +
                ChannelContract.VideosEntry.COLUMN_LINK + " TEXT NOT NULL, " +
                ChannelContract.VideosEntry.COLUMN_THUMBNAIL + " BLOB , " +

                // To ensure the application have just one video entry per title
                // per pub_date
                "UNIQUE (" + ChannelContract.VideosEntry.COLUMN_TITLE + ", " +
                ChannelContract.VideosEntry.COLUMN_PUB_DATE + ") ON CONFLICT REPLACE);";
        sqliteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, int oldVersion, int newVersion) {
        // We jest drop the database and create it because the data is online
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS " + ChannelContract.VideosEntry.TABLE_NAME);
        onCreate(sqliteDatabase);
    }
}
