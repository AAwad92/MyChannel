package com.example.android.mychannel.app;

/**
 * Created by abdo on 12/04/15.
 */


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.mychannel.app.Data.ChannelContract;
import com.example.android.mychannel.app.service.MychaneelService;

/**
 * A placeholder fragment containing a simple view.
 */
public class VideoListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // Tag used to log messages
    private final String LOG_TAG = VideoListFragment.class.getSimpleName();

    // ChannelAdapter obj
    private ChannelAdapter mVideosAdapter;

    //Loader ID
    private static final int VIDEOS_LOADER = 0;

    private ListView mListView;
    // save the listItem position
    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";


    // Specify the columns
    private static final String[] CHANNEL_COLUMNS = {
            ChannelContract.VideosEntry._ID,
            ChannelContract.VideosEntry.COLUMN_TITLE,
            ChannelContract.VideosEntry.COLUMN_PUB_DATE,
            ChannelContract.VideosEntry.COLUMN_LINK,
            ChannelContract.VideosEntry.COLUMN_THUMBNAIL
    };

    // These indices are tied to CHANNEL_COLUMNS
    static final int COL_VIDEO_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_PUB_DATE = 2;
    static final int COL_LINK = 3;
    static final int COL_THUMBNAIL = 4;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }


    public VideoListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Query database using Uri and get the result in a cursor
//        Uri videosUri = ChannelContract.VideosEntry.buildChannelVideos();
//        Cursor cur = getActivity().getContentResolver().query(videosUri,null,null,null,null);

        // Crete a ChannelAdapter obj using the query cursor
        mVideosAdapter = new ChannelAdapter(getActivity(),null,0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //get reference to the listView, and attach adapter
        mListView = (ListView) rootView.findViewById(R.id.list_view_channel_videos);
        mListView.setAdapter(mVideosAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null){
                    ((Callback) getActivity())
                            .onItemSelected(ChannelContract.VideosEntry.buildVideosFromPubDateAndTitle(
                                    cursor.getString(COL_PUB_DATE),cursor.getString(COL_TITLE)
                            ));
                }
                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
//        // Start Service
//        Intent serviceIntent = new Intent(getActivity(), MychaneelService.class);
//        getActivity().startService(serviceIntent);


        // Use Alarm manger to set alarm
        Intent alarmIntent = new Intent(getActivity(), MychaneelService.AlarmReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0 , alarmIntent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000,pi);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(VIDEOS_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateVideos(){
        FetchVideosTask task = new FetchVideosTask(getActivity());
        task.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateVideos();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition !=ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri videosUri = ChannelContract.VideosEntry.buildChannelVideos();
        return new CursorLoader(getActivity(),
                videosUri,
                CHANNEL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mVideosAdapter.swapCursor(cursor);
        if (mPosition != ListView.INVALID_POSITION){
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mVideosAdapter.swapCursor(null);

    }
}