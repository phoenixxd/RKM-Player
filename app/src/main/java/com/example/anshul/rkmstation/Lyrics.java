package com.example.anshul.rkmstation;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lyrics {

    private TextView lyricsview;
    private String SongName;
    private boolean exceptionoccured = false;
    Lyrics(TextView view, ImageView bg, String songName) {
        lyricsview = view;
        SongName = songName;
    }

    public void setLyrics()
    {
        new fetchlyrics().execute(SongName);
    }
    public class fetchlyrics extends AsyncTask<String, String, String>{

        StringBuilder res_ = new StringBuilder();   //StringBuilder is faster than StringBuffer
        protected String doInBackground(String... params) {

            params[0] = "https://www.google.co.in/search?q="+ params[0].toLowerCase().replace(" ", "+") +
                    "+(site%3Aazlyrics.com+OR+site%3Alyricsmint.com)" ;
            Log.e("Querylinks", params[0]);

            try {
                //The Document interface represents the entire HTML document
                Document doc = Jsoup
                        .connect(params[0]) //Creates a new Connection to a URL. Use to fetch and parse a HTML page.
                        .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                        .timeout(5000)
                        .maxBodySize(0)  //Set the maximum bytes to read from the (uncompressed) connection into the body. 0 is treated as infinite.
                        .execute().parse();   //Execute the request. //Parse the body of the response as a Document.

                if(doc == null)
                {
                    return null;
                }

                Elements links = doc.select("a[href]");  //finds links 'a' tags with 'ref' attributes.

                if(links.size() <= 0)
                {
                    return null;
                }

                for (Element link : links) {

                    String temp = link.attr("href");  //href attribute specifies the URL of the page the link goes to.
                    Log.e("discardedlinks", temp);
                    if(temp.startsWith("/url?q=")){
                        //use regex to get domain name
                        String txt = temp.replaceAll("(.*)(http[s]?.*?\\.htm[l]?)(.*)", "$2");

                        Log.e("links", txt);
                        //now txt is the link to the lyricsmint actual site
                        Document lyp = Jsoup
                                .connect(txt)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .referrer("http://www.google.com")
                                .timeout(5000).get();

                        if(lyp == null)
                            return null;

                        if(txt.contains("lyricsmint")) {
//                        Elements titles = lyp.select("h1[class=post-title entry-title]");

                            ///The below for loop is for checking the equality of the song found
                            ///For in-Sensitive search we trust google and go with the first site found

//                        for(Element title:  titles)
//                        {
//                                Log.e("Title", title.text().toLowerCase() + "~" + params[1]);
//                            if(title.text().toLowerCase().contains(params[1]))
//                            {
                            //its a match
                            //get the lyrics and break the loop
                            Element lyrics = lyp.select("div#lyric").first();
                            res_.append(lyrics.html());

                            return lyrics.html();
//                            }
//                        }
                        }
                        else if(txt.contains("azlyrics"))
                        {
                            try
                            {
                                Element container = lyp.select("div[class=container main-page]").first();
                                //clean up the fetched data
//                                (?s).*?(<h2>.*?</h2>).*
//                                (?s).*?(<b>.*?</b>).*
//                                (?s).*(<b>.*?</b>).*

                                StringBuilder ret = new StringBuilder();

                                String Regex = "(?s).*?(<h2>.*?</h2>).*";
                                String Replacement = "$1<br />";
                                String out;
                                out = container.html().replaceAll(Regex, Replacement);
                                ret.append(out); //Main Heading
                                Log.e("HTMLMain", out);

                                Regex = "(?s).*(<b>.*?</b>).*";
                                Replacement = "$1<br />";
                                out = container.html().replaceAll(Regex, Replacement);
                                ret.append(out); //Title
                                Log.e("HTMLTitle", out);


                                Regex = "(?s).*?(<div>.*?</div>).*";
                                Replacement = "$1<br />";
                                out = container.html().replaceAll(Regex, Replacement);
                                ret.append(out); //Lyrics
                                Log.e("HTMLLyrics", out);

                                return ret.toString();
                            }
                            catch (Exception e)
                            {

                                e.printStackTrace();
                                exceptionoccured = true;
                                if(e instanceof SecurityException)
                                    return "Internet Connectivity not found or permissions for Internet has been denied.";
                                else if(e instanceof SocketTimeoutException)
                                    return "Slow Connection detected try again after some time.";
                                else if(e instanceof UnknownHostException)
                                    return "No Internet Connectivity.";
                                else
                                    return e.toString();
                            }
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                exceptionoccured = true;
                if(e instanceof SecurityException)
                    return "Internet Connectivity not found or permissions for Internet has been denied.";
                else if(e instanceof SocketTimeoutException)
                    return "Slow Connection detected try again after some time.";
                else if(e instanceof UnknownHostException)
                    return "No Internet Connectivity.";
                else
                    return e.toString();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null && !exceptionoccured)
            {
                if(lyricsview != null){
                    lyricsview.setText(Html.fromHtml(s));
                    UI.showLyrics();
                }

            }
            else if(s!= null && exceptionoccured)
            {
                //its an exception show it in a toast
                Toast newmsg = Toast.makeText(lyricsview.getContext(), s, Toast.LENGTH_SHORT);
                newmsg.setGravity(Gravity.CENTER, 0, 0);
                newmsg.show();
            }
            else
            {
                s = "Lyrics Not found for <b>" + SongName + "</b>. Try with custom Song Name,<br /> <i>Song Name   Movie/Album Name</i> <br />long press the Lyrics button to type a custom name";
                if(lyricsview != null) {
                    lyricsview.setText(Html.fromHtml(s));
                    UI.showLyrics();
                }
            }

        }


    }


    public void logLargeString(String TAG, String str) {
        if(str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(TAG, str.substring(3000));
        } else {
            Log.e(TAG, str); // continuation
        }
    }
}
