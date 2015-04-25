package com.example.android.mychannel.app.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abdo on 14/04/15.
 */
public class ChannelContract {
    // Content Authority used to access the uri
    public static final String CONTENT_AUTHORITY = "com.example.android.mychannel.app";

    // Base Uri used to access the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // In the app there is only one path
    public static final String PATH_VIDEOS = "videos";

    public static final class VideosEntry implements BaseColumns {

        // This Uri used to access the videos
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        // Define the type if we return an item or dir
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;


        public final static String TABLE_NAME = "videos";
        // THis column store the publish date for each video
        public final static String COLUMN_PUB_DATE = "pub_date";
        // THis column store the title of video
        public final static String COLUMN_TITLE = "title";
        // THis column store the link to access the video
        public final static String COLUMN_LINK = "link";
        // THis column store the thumbnail image for the image
        public final static String COLUMN_THUMBNAIL = "thumbnail";

        public static Uri buildVideosUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //
       public static Uri buildChannelVideos(){
           return CONTENT_URI;
       }

        //
        public static Uri buildVideosFromPubDateAndTitle(String pubDate, String title) {
            return CONTENT_URI.buildUpon().appendPath(pubDate).appendPath(title).build();
        }

        //
        public static String getPubDateFromUri(Uri uri) {
            return (String) uri.getPathSegments().get(1);
        }

        //
        public static String getTitleFromUri(Uri uri) {
            return (String) uri.getPathSegments().get(2);
        }
    }

//    public static long normalizeDate(long startDate) {
//        // normalize the start date to the beginning of the (UTC) day
//        Time time = new Time();
//        time.set(startDate);
//        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
//        return time.setJulianDay(julianDay);
//    }
}

