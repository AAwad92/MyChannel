package com.example.android.mychannel.app.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by abdo on 19/04/15.
 */
public class ChannelProvider extends ContentProvider {

    static final int VIDEOS = 100;
    static final int VIDEO_WITH_PUB_DATE_AND_TITLE = 101;
    // Uri Matcher used by the content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sVideosQueryBuilder;

    static {
        sVideosQueryBuilder = new SQLiteQueryBuilder();
        sVideosQueryBuilder.setTables(ChannelContract.VideosEntry.TABLE_NAME);

    }

    // Videos
    private static final String sVideosTable =
            ChannelContract.VideosEntry.TABLE_NAME;
    // Videos.pubDate = ? and title = ?
    private static final String sPubDateAndTitleSelection =
            ChannelContract.VideosEntry.TABLE_NAME +
                    "." + ChannelContract.VideosEntry.COLUMN_PUB_DATE + " = ? AND " +
                    ChannelContract.VideosEntry.COLUMN_TITLE + " = ? ";
    private ChannelDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ChannelContract.CONTENT_AUTHORITY;

        //Add uris to my UriMatcher
        matcher.addURI(authority, ChannelContract.PATH_VIDEOS, VIDEOS);
        matcher.addURI(authority, ChannelContract.PATH_VIDEOS + "/*/*", VIDEO_WITH_PUB_DATE_AND_TITLE);

        return matcher;
    }

    private Cursor getVideosByPubDateAndTitle(
            Uri uri, String[] projection, String sortOrder) {
        String pubDate = ChannelContract.VideosEntry.getPubDateFromUri(uri);
        String title = ChannelContract.VideosEntry.getTitleFromUri(uri);

        return sVideosQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sPubDateAndTitleSelection,
                new String[]{pubDate, title},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ChannelDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case VIDEOS:
                return ChannelContract.VideosEntry.CONTENT_TYPE;
            case VIDEO_WITH_PUB_DATE_AND_TITLE:
                return ChannelContract.VideosEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case VIDEO_WITH_PUB_DATE_AND_TITLE: {
                retCursor = getVideosByPubDateAndTitle(uri, projection, sortOrder);
                break;
            }
            case VIDEOS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ChannelContract.VideosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;
        if (match == VIDEOS) {
             _id = db.insert(ChannelContract.VideosEntry.TABLE_NAME, null, values);
            if (_id > 0) {
                returnUri = ChannelContract.VideosEntry.buildVideosUri(_id);
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        final int match = sUriMatcher.match(uri);
        int rowDeleted;
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.
        if (null == selection) selection = "1";
        if (match == VIDEOS) {
            rowDeleted = db.delete(
                    ChannelContract.VideosEntry.TABLE_NAME, selection, selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final  int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if (match == VIDEOS){
            rowsUpdated = db.update(ChannelContract.VideosEntry.TABLE_NAME ,
                    values,selection,selectionArgs);
        } else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if (match == VIDEOS){
            db.beginTransaction();
            int returnCount = 0;
            try{
                for (ContentValues value : values){
                    long _id = db.insert(ChannelContract.VideosEntry.TABLE_NAME,null,value);
                    if (_id != -1){
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            }finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri,null);
            return returnCount;
        } else
            return super.bulkInsert(uri, values);
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
