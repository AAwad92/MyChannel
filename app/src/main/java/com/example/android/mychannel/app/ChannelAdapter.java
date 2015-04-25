package com.example.android.mychannel.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by abdo on 22/04/15.
 */
public class ChannelAdapter  extends CursorAdapter{

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView thumbnailView;
        public final TextView titleView;
        public final TextView pubDateView;


        public ViewHolder(View view) {
            thumbnailView = (ImageView) view.findViewById(R.id.list_item_image);
            titleView = (TextView) view.findViewById(R.id.list_item_title);
            pubDateView = (TextView) view.findViewById(R.id.list_item_pub_date);
        }
    }

    public ChannelAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String convertCursorRowToUxFormat(Cursor cur){

        return cur.getString(VideoListFragment.COL_TITLE) + "   " + cur.getString(VideoListFragment.COL_PUB_DATE);
    }
    private String convertCursorRowToUXFormat(Cursor cursor){
        return null;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_channel_videos, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        byte[] imgByte = cursor.getBlob(VideoListFragment.COL_THUMBNAIL);
        Bitmap bmb = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);

        viewHolder.thumbnailView.setImageBitmap(bmb);

        viewHolder.titleView.setText(cursor.getString(VideoListFragment.COL_TITLE));

        viewHolder.pubDateView.setText(cursor.getString(VideoListFragment.COL_PUB_DATE));

    }
}
