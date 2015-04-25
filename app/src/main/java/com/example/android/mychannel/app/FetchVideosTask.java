package com.example.android.mychannel.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.mychannel.app.Data.ChannelContract;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

/**
 * Created by abdo on 20/04/15.
 */
public class FetchVideosTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchVideosTask.class.getSimpleName();

    private ChannelAdapter mVideosAdapter;
    private final Context mContext;

    public FetchVideosTask(Context context){
        mContext = context;
    }
    private boolean DEBUG = true;


    /**
     * Helper method to handle insertion of a new video in the channel database
    */
    long addVideo(String pubDate, String title, String thumbnailLink ){
        long videoId;

        // Check if the video with this title and pubDate exists int db
        Cursor videoCursor = mContext.getContentResolver().query(
                ChannelContract.VideosEntry.CONTENT_URI,
                new String[]{ChannelContract.VideosEntry._ID},
                ChannelContract.VideosEntry.COLUMN_PUB_DATE+" =? " +
                        ChannelContract.VideosEntry.COLUMN_TITLE + " =? ",
                new String []{pubDate,title},
                null
        );
        if(videoCursor.moveToFirst()){
            int videoIdIndex = videoCursor.getColumnIndex(ChannelContract.VideosEntry._ID);
            videoId = videoCursor.getLong(videoIdIndex);
        } else {
            // If not exist we will create ContentValues to add new row to the Db
            ContentValues videoValues = new ContentValues();

            // Fill the ContentValues Obj with the corresponding name of the data type
            videoValues.put(ChannelContract.VideosEntry.COLUMN_PUB_DATE, pubDate);
            videoValues.put(ChannelContract.VideosEntry.COLUMN_TITLE, title);
            videoValues.put(ChannelContract.VideosEntry.COLUMN_THUMBNAIL, thumbnailLink);

            Uri insertedUri = mContext.getContentResolver().insert(
                    ChannelContract.VideosEntry.CONTENT_URI,videoValues
            );
            videoId = ContentUris.parseId(insertedUri);
        }
        videoCursor.close();
        return videoId;
    }



    /**
     * Take the String representing the complete Channel in XML Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     */
    private void getChannelDataFromXML(InputStream inputStream)
            throws XmlPullParserException , IOException{
        // parse xml file then add the values to the database
        VideosListXmlParser parser = new VideosListXmlParser();
        List <VideosListXmlParser.Item> channelXmlItems = parser.parse(inputStream);

        // Insert the new videos into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(channelXmlItems.size());
        for (VideosListXmlParser.Item item : channelXmlItems) {
            String link;
            String pubDate;
            String title;
            byte[]  thumbnailByte;

            link = item.link;
            pubDate = getPubDate( item.pubDate);
            title = item.title;
            thumbnailByte = getBitmapAsByteArray(getThumbnailLinkFromBaseLink(link));

            ContentValues videoValues = new ContentValues();
            videoValues.put(ChannelContract.VideosEntry.COLUMN_LINK,link);
            videoValues.put(ChannelContract.VideosEntry.COLUMN_PUB_DATE,pubDate);
            videoValues.put(ChannelContract.VideosEntry.COLUMN_TITLE,title);
            videoValues.put(ChannelContract.VideosEntry.COLUMN_THUMBNAIL,thumbnailByte);

            cVVector.add(videoValues);

        }
        int inserted = 0;
        // add to the database
        if (cVVector.size() > 0){
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(ChannelContract.VideosEntry.CONTENT_URI,cvArray);
        }

        Log.d(LOG_TAG, "FetchWeatherTask Complete. " + inserted + " Inserted");
    }

    private String getPubDate(String pubDate) {
        return pubDate = pubDate.substring(0,25);
    }

    private String getThumbnailLinkFromBaseLink(String link) {
        String thumbnailLink = "http://img.youtube.com/vi/"+link.substring(31,42)+"/0.jpg";
        Log.v(LOG_TAG , "this is the thumbnail Link" + thumbnailLink);
        return thumbnailLink;
    }

    private Bitmap getBitmapFroURL(String thumbnailLink) throws IOException {
        URL url = null;
        try {
            url = new URL(thumbnailLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        return bmp;
    }

    public static byte[] getBitmapAsByteArray( String thumbnailLink) throws IOException {
        URL url = null;
        try {
            url = new URL(thumbnailLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

//    public void insertImg(int id , Bitmap img ) {
//
//
//        byte[] data = getBitmapAsByteArray(img); // this is a function
//
//    }


    @Override
    protected Void doInBackground(Void... params) {
        Log.d(LOG_TAG, "Do InBack");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //String contain the response as string
        String videosXmlStr = null;
        List<VideosListXmlParser.Item>items = null;

        try {
            //Construct the URL for the YouTube Videos
            URL url = new URL("http://gdata.youtube.com/feeds/base/users/Tai5eer/uploads?alt=rss&v=2&orderby=published&client=ytapi-youtube-profile");

            //Create the connection to the Youtube, and open it
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            VideosListXmlParser parser = new VideosListXmlParser();
            try {
                getChannelDataFromXML(inputStream);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the videos data
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }
        return null;
    }
}
