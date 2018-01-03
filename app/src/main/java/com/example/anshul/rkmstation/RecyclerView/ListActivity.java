package com.example.anshul.rkmstation.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.anshul.rkmstation.R;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {

    private static final String TAG = "RecyclerViewExample";

    private List<Item> itemList = new ArrayList<Item>();

    private RecyclerView mRecyclerView;/*A view previously used to display data for a specific
    adapter position may be placed in a cache for later reuse to display the same type of data again
    later.This can drastically improve performance by skipping initial layout inflation or construction.*/

    private MyRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_list);

        /* Initialize recyclerview */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSongList();

        adapter = new MyRecyclerAdapter(this, itemList);
        mRecyclerView.setAdapter(adapter);
    }

    public void getSongList() {
        ContentResolver musicResolver = this.getContentResolver(); /*ContentResolver provides applications access to the
                                                                      content model. */

        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  /*A Uniform Resource Identifier (URI)
                                                                       is a compact sequence of characters that
                                                                       identifies an abstract or physical resource.*/
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };
        try {
            // cursor provides random read-write access to the result set returned by a database query.
            Cursor musicCursor = musicResolver.query(musicUri, projection, MediaStore.Audio.Media.IS_MUSIC +" != 0 ", null, null);

            if(musicCursor!=null && musicCursor.moveToFirst()){
                //get columns
                int titleColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.ARTIST);
                int pathColumn = musicCursor.getColumnIndex
                        (android.provider.MediaStore.Audio.Media.DATA);
                //add songs to list
                do {
                    Item newitem = new Item();
                    newitem.setID((int)musicCursor.getLong(idColumn));
                    newitem.setTitle(musicCursor.getString(titleColumn));
                    newitem.setArtist(musicCursor.getString(artistColumn));
                    newitem.setPath(musicCursor.getString(pathColumn));
                    itemList.add(newitem);
                }
                while (musicCursor.moveToNext());
            }
            musicCursor.close();

        }catch (SecurityException s)
        {
            s.printStackTrace();
            Toast.makeText(this, "Storage Permission is denied. Program will exit.", Toast.LENGTH_SHORT).show();
            System.exit(1);
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Exception Occured:" + e.toString(), Toast.LENGTH_SHORT).show();
            System.exit(1);
        }
    }

}