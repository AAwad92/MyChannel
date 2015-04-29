package com.example.android.mychannel.app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.mychannel.app.Data.ChannelContract;
import com.google.android.youtube.player.YouTubeStandalonePlayer;


/**
 * Created by abdo on 23/04/15.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Tag used with the fragment bundle
    static final String DETAIL_URI = "URI";
    static final String LOADING_FLAG = "LOADING";
    // Loader Id
    private final static int DETAIL_LOADER = 0;

    private static final String LOG_TAG = com.example.android.mychannel.app.DetailFragment.class.getSimpleName();
    private static final String CHANNEL_SHARE_HASHTAG = " #Tai5er";

    private ShareActionProvider mShareActionProvider;
    private Uri mUri;

    // Video Id that we need to play
    private String mVideoID;
    // Detail fragments items
    private ImageView imageView;
    private TextView titleView;
    private TextView pubDateView;
    private String mChannelStr;

    // These indices are tied to CHANNEL_COLUMNS
    static final int COL_VIDEO_ID = 0;
    static final int COL_LINK = 1;
    static final int COL_PUB_DATE = 2;
    static final int COL_TITLE = 3;
    static final int COL_THUMBNAIL_LINK = 4;

    private static final String[] CHANNEL_COLUMNS = {
            ChannelContract.VideosEntry._ID,
            ChannelContract.VideosEntry.COLUMN_LINK,
            ChannelContract.VideosEntry.COLUMN_PUB_DATE,
            ChannelContract.VideosEntry.COLUMN_TITLE,
            ChannelContract.VideosEntry.COLUMN_THUMBNAIL
    };



    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get the Uri that used to get the cursor to fil the detail fragment
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        } else {
            mUri = getActivity().getIntent().getData();
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.detail_image);
        titleView = (TextView) rootView.findViewById(R.id.detail_title);
        pubDateView = (TextView) rootView.findViewById(R.id.detail_pub_date);

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your code here
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                        "AIzaSyAhrMm64poWRw-37u_7uormSjl_zjEy7k0", mVideoID);
                startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this add items to the action bar
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(creteShareVideoIntent());
//        }
    }

    private Intent creteShareVideoIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mChannelStr + CHANNEL_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    CHANNEL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");


        if (!data.moveToFirst()) {
            return;
        }


        String titleStr = data.getString(COL_TITLE);
        titleView.setText(titleStr);

        String pubDateStr = data.getString(COL_PUB_DATE);
        pubDateView.setText(pubDateStr);

        String linkStr = data.getString(COL_LINK);
        // Set videoID

        mVideoID = linkStr.substring(31, 42);
        ;
        byte[] imageByte = data.getBlob(VideoListFragment.COL_THUMBNAIL);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(bitmap);

        mChannelStr = titleStr + " - " + pubDateStr + " - " + linkStr;
        TextView tv = (TextView) getView().findViewById(R.id.detail_description);
        tv.setText(mChannelStr);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(creteShareVideoIntent());
        }

    }

    private String getVideoIdFromLink(String link) {
        return link.substring(31, 42);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
