package com.example.anshul.rkmstation;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;


public class LocalList extends AppCompatActivity {

    private  static ArrayList<Song> songList;
    ListView list;
    private static ArrayList<String>itemname ;
    private  static long songID[];
    private  static ArrayList<String> artist;
    private  static ArrayList<String> path;
    private static int cSongPos, songsCount, PlaylistID;
    private FetchSongs songRetriever = new FetchSongs(this);
    private boolean longpress = false;
    private boolean addvis = false, delvis = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar...Retrieve a reference to this activity's ActionBar.
        ActionBar ab = getSupportActionBar();

        String title = getIntent().getExtras().getString("playlistname");//retrieves the data from bundle passed from main list intent
        if(title!=null)ab.setTitle(title);

        // Enable the Up button
        //actionBar.setDisplayHomeAsUpEnabled(true) will make the icon clickable and add the < at the left of the icon.
        ab.setDisplayHomeAsUpEnabled(true);


        PlaylistID = getIntent().getExtras().getInt("playlistid");
        if(PlaylistID == -1)
            songList = songRetriever.getLocalSongs();
        else
            songList = songRetriever.getSongsfromPlaylist(PlaylistID);

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        songsCount = songList.size();
        cSongPos = 0;
        artist = new ArrayList<String>(songsCount);
        path = new ArrayList<String>(songsCount);
        itemname = new ArrayList<String>(songsCount);
        songID = new long[songsCount];

        int i;
        for(i=0;i<songsCount;i++) {
            //itemname.add(songList.get(i).getTitle().replaceAll("_", " ").replaceAll("([[:alnum:]]*)([.])([[:alnum:]]*)", "").replaceAll("([(])([^(]*)([)])", "").replaceAll("([^[:alpha:] ]*)", ""));
            itemname.add(songList.get(i).getTitle());
            artist.add(songList.get(i).getArtist());
            path.add(songList.get(i).getPath());
            songID[i] = songList.get(i).getID();
        }

        final customlistadapter adapter = new customlistadapter(this, itemname,artist, songID, path);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);  //In order to display items in the list,
                                   // call setAdapter(ListAdapter) to associate an adapter with the list

        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if(longpress)
                {
//                    list.setItemsCanFocus(false);
//                    SparseBooleanArray isChecked = list.getCheckedItemPositions();
//                    int newx = isChecked.indexOfKey(position);
//                    boolean state = (newx == -1)? false : true;
//                    list.setItemChecked(position, !state);
//                    list.setItemsCanFocus(true);
                }
                else
                {
                    list.setItemChecked(position, false);
                    cSongPos = position;
                    adapter.setSongID(LocalList.getSongID()[cSongPos]);
                    Intent intent = new Intent(LocalList.this,UI.class);
                    startActivity(intent);
                }

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!longpress){
                    longpress = true;
                    list.setItemChecked(position, true);
                    showSelButtons();
                    return true;
                }
                else return false;

            }
        });

        songList = null;
    }

    public void unselectAllItems() {

        SparseBooleanArray pos =  list.getCheckedItemPositions();
        long count = pos.size();

        for(int i = 0; i < count; ++i)
            list.setItemChecked(pos.keyAt(i), false);
    }

    @Override
    // when long press in list to do multiple selection.
    //Then have to show add and delete button on menu
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();   ///MenuInflater class is used to instantiate menu XML files into Menu objects.
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem add = menu.getItem(0);
        add.setVisible(addvis);

        MenuItem del = menu.getItem(1);
        del.setVisible(delvis);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.addtoplaylist) {
//            final EditText input = (EditText)findViewById(R.id.inputtext);
//            input.setVisibility(View.VISIBLE);
//
//            input.setOnKeyListener(new View.OnKeyListener() {
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    // If the event is a key-down event on the "enter" button
//                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                        // Perform action on key press
//
//                        SparseBooleanArray pos =  list.getCheckedItemPositions();
//                        long count = pos.size();
//                        long[] songIDs = new long[(int)count];
//
//                        for(int i = 0; i < count; ++i)
//                            songIDs[i] = songID[pos.keyAt(i)];
//
//                        FetchSongs.addToPlaylist(getContentResolver(), input.getText().toString(), songIDs, (int)count);
//                        unselectAllItems();
//
//                        longpress = false;
//                        hideSelButtons();
//
//                        input.setVisibility(View.INVISIBLE);
//                        return true;
//                    }
//                    return false;
//                }
//            });

            SparseBooleanArray pos =  list.getCheckedItemPositions();
            long count = pos.size();
            long[] songIDs = new long[(int)count];

            for(int i = 0; i < count; ++i)
                songIDs[i] = songID[pos.keyAt(i)];

            FetchSongs.addToPlaylist(getContentResolver(), "Favourite", songIDs, (int)count);
            unselectAllItems();

            longpress = false;
            hideSelButtons();

//            input.setVisibility(View.INVISIBLE);


            return true;
        }
        else if(id == R.id.deleteplaylist)
        {
            return true;
        }
        else if(id == R.id.playingnow){
            Intent intent = new Intent(LocalList.this,UI.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(longpress)
        {
            unselectAllItems();

            longpress = false;
            hideSelButtons();
            return;
        }
        else {
            super.onBackPressed();
        }
    }

    public void hideSelButtons()
    {
        addvis = delvis = false;
        invalidateOptionsMenu();
    }

    public void showSelButtons()
    {
        addvis = true;
        delvis = (true);
        invalidateOptionsMenu();
    }

    public static int getcurrSongPos() {return cSongPos;}
    public static ArrayList<String> getSongpaths(){return path;}
    public static ArrayList<String> getSongTitles(){return itemname;}
    public static ArrayList<String> getSongArtist(){return artist;}
    public static long[] getSongID(){return songID;}
    public static long getSongsCount(){return songsCount;}
    public static int getPlaylistID(){return PlaylistID;}

}


