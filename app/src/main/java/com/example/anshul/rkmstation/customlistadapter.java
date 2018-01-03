package com.example.anshul.rkmstation;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import static com.example.anshul.rkmstation.GetImage.getImage;

public class customlistadapter extends ArrayAdapter<String> {
    private final String TAG = "customlistadapter";
    private Activity context;
    private ArrayList<String> itemname;
    private long[] imgid;
    private ArrayList<String> artist;
    private ArrayList<String> itemPath;
    private long currsongid = -1;

    //Caching
    MemoryCache memoryCache = new MemoryCache(25);  //25percent to be used for storage
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());


    public customlistadapter(Activity context, ArrayList<String> itemname, ArrayList<String> artist, long[] imgid, ArrayList<String> itemPath) {

        super(context, R.layout.mylist, itemname);
// TODO Auto-generated constructor stub
        this.artist=artist;
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.itemPath=itemPath;

    }

    public void setSongID(long a)
    {
        currsongid = a;
    }


    public View getView(int position,View view,ViewGroup parent) {
        View rowView = view;

        if(view == null) {

            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.mylist, null, true);
            rowView.setDrawingCacheEnabled(false);
        }

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname.get(position));
        extratxt.setText(artist.get(position));
        String imgID = String.valueOf(imgid[position]);
       //check if the thumbnail is in the cache
        if(memoryCache.isSet(imgID))
        {
            Bitmap bitmap = memoryCache.get(imgID);
            if(bitmap!=null)imageView.setImageBitmap(bitmap);
            else imageView.setImageResource(R.drawable.musi);
        }
        else
        {
            Log.d(TAG, "Default Value set for" + imgID);
            imageView.setImageResource(R.drawable.musi);
            //set the thumbnail in a separate thread
            new BitmapWorkerTask(imgID, imageView).execute(itemPath.get(position));
        }
        return rowView;
    }



    class BitmapWorkerTask extends AsyncTask<String, String, Bitmap> {
        private final String thisimgid;
        private final ImageView thisimageview;
        // Decode image in background
        BitmapWorkerTask(String imgid, ImageView view)
        {
            thisimgid = imgid;
            thisimageview = view;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm = getImage(params[0], 60, 60);
            memoryCache.put(thisimgid, bm);//add to the cache
            return bm;
            // int bytes=bm.getByteCount();
            // Log.d("Bytes for " + itemname[position] + ": ", String.valueOf(bytes));
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null && thisimageview != null){
                imageViews.put(thisimageview, thisimgid);
                thisimageview.setImageBitmap(bitmap);
            }
        }
    }

}