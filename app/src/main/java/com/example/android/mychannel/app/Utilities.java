package com.example.android.mychannel.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by abdo on 29/04/15.
 */
public class Utilities {

    public static byte[] getBitmapAsByteArray (String thumbnailLink)throws IOException {
        URL url = null;
        try {
            url = new URL(thumbnailLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        if (url != null) {
            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }


    public static String getThumbnailLinkFromBaseLink(String link) {
        String thumbnailLink = "http://img.youtube.com/vi/" + link.substring(31, 42) + "/0.jpg";
        return thumbnailLink;
    }

    private Bitmap getBitmapFroURL(String thumbnailLink) throws IOException {
        URL url = null;
        try {
            url = new URL(thumbnailLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }
}
