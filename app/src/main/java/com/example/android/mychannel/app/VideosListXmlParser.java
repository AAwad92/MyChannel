package com.example.android.mychannel.app;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdo on 13/04/15.
 */
public class VideosListXmlParser {
    private static final String ns = null;

    public List parse (InputStream inputStream) throws XmlPullParserException , IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            // Set namespace feature off
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,false);
            parser.setInput(inputStream , null);
            parser.nextTag();
            return readRss(parser);
        }finally {
            inputStream.close();
        }
    }



    private List readRss(XmlPullParser parser) throws XmlPullParserException , IOException {
        parser.require(XmlPullParser.START_TAG , ns, "rss");
        parser.nextTag();
        return readChannel(parser);
    }

    private List readChannel(XmlPullParser parser) throws XmlPullParserException , IOException {
        List items = new ArrayList();

        parser.require(XmlPullParser.START_TAG , ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String name = parser.getName();
            // Starts by looking for item tag
            if (name.equals("item")){
                items.add(readItem(parser));
            }else {
                skip(parser);
            }
        }
        return items;
    }

    private Object readItem(XmlPullParser parser) throws XmlPullParserException , IOException{
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String pubDate = null;
        String title = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("pubDate")){
                pubDate = readPubDate(parser);
            } else if (name.equals("title")){
                title = readTitle(parser);
            } else if (name.equals("link")){
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new Item(pubDate,title,link);
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException , IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()){
                case XmlPullParser.END_TAG:
                    depth --;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String readPubDate(XmlPullParser parser) throws XmlPullParserException ,IOException{
        parser.require(XmlPullParser.START_TAG ,ns, "pubDate");
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG ,ns ,"pubDate");
        return pubDate;
    }

    private String readTitle(XmlPullParser parser) throws XmlPullParserException , IOException{
        parser.require(XmlPullParser.START_TAG ,ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG ,ns ,"title");
        return title;
    }

    private String readLink(XmlPullParser parser) throws XmlPullParserException , IOException{
        parser.require(XmlPullParser.START_TAG ,ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG ,ns ,"link");
        return link;
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException , IOException{
        String result ="";
        if(parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


    public static class Item {
        public final String pubDate;
        public final String title;
        public final String link;

        private Item(String pubDate, String title, String link){
            this.pubDate = pubDate;
            this.title = title;
            this.link = link;
        }
    }

}
