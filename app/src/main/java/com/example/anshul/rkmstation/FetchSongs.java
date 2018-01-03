package com.example.anshul.rkmstation;


import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class FetchSongs {

    private final String TAG = "FetchSongs";

    private Context mContext;
    FetchSongs(Context mContext)
    {
        this.mContext = mContext;
    }

    public ArrayList<Song> getLocalSongs(){
        //A Uniform Resource Identifier (URI) is a compact sequence of characters that identifies an abstract or physical resource.
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return getSongList(musicUri);
    }

    public ArrayList<Song> getSongsfromPlaylist(int playlist_id) {
        //ContentResolver is a class which provides applications access to the content model.
        final ContentResolver resolver = mContext.getContentResolver();
        final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);

        return getSongList(uri);
    }


    public ArrayList<Song> getSongList(Uri musicUri) {

        ArrayList<Song> songList = new ArrayList<>();

        ContentResolver musicResolver = mContext.getContentResolver();
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };
        try {
            //cursor provides random read-write access to the result set returned by a database query.
            Cursor musicCursor = musicResolver.query(musicUri, projection, MediaStore.Audio.Media.IS_MUSIC +" != 0", null, null);
            //MediaStore.Audio.Media.IS_MUSIC +" != 0 "

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
                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    String thisPath = musicCursor.getString(pathColumn);
                    songList.add(new Song(thisId, thisTitle, thisArtist, thisPath ));
                }
                while (musicCursor.moveToNext());
            }
            musicCursor.close();


            return songList;

        }catch (SecurityException s)
        {
            s.printStackTrace();
            Toast.makeText(mContext, "Storage Permission is denied. Program will exit.", Toast.LENGTH_SHORT).show();
            System.exit(1);
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(mContext, "Exception Occured:" + e.toString(), Toast.LENGTH_SHORT).show();
            System.exit(1);
        }

        return null;
    }

    public ArrayList<Playlist> getPlaylists() {

        ArrayList<Playlist> PlayLists = new ArrayList<>();
        ContentResolver musicResolver = mContext.getContentResolver(); //This class provides applications access to the content model.
        Uri musicUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        //A Uniform Resource Identifier (URI) is a compact sequence of characters that identifies an abstract or physical resource.
        String[] projection = {
                MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists._ID
        };
        try {
            Cursor musicCursor = musicResolver.query(musicUri, projection, null, null, null);
            //This interface provides random read-write access to the result set returned by a database query.

            if(musicCursor!=null && musicCursor.moveToFirst()){
                //get columns
                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Playlists.NAME);
                int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Playlists._ID);
                //add songs to list
                do {
                    long thisId = musicCursor.getLong(idColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    PlayLists.add(new Playlist(thisId, thisTitle));
                }
                while (musicCursor.moveToNext());
            }
            musicCursor.close();

        }catch (SecurityException s)
        {
            s.printStackTrace();
            Toast.makeText(mContext, "Storage Permission is denied. Program will exit.", Toast.LENGTH_SHORT).show();
            System.exit(1);
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(mContext, "Exception Occured:" + e.toString(), Toast.LENGTH_SHORT).show();
            System.exit(1);
        }

        return PlayLists;
    }

    public static long getPlaylistIDfromName(ContentResolver resolver, String name) {
        long id = -1;

        Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                                       new String[] { MediaStore.Audio.Playlists._ID },
                                       MediaStore.Audio.Playlists.NAME + "=?",
                                       new String[] { name }, null);

        if (cursor != null) {
            if (cursor.moveToNext())
                id = cursor.getLong(0);
            cursor.close();
        }

        return id;
    }

    /**
     * Create a new playlist with the given name. If a playlist with the given
     * name already exists, it will be overwritten.
     *
     * @param resolver A ContentResolver to use.
     * @param name The name of the playlist.
     * @return The id of the new playlist.
     */
    public static long createPlaylist(ContentResolver resolver, String name) {

        long id = getPlaylistIDfromName(resolver, name);
        if (id == -1) {
            // We need to create a new playlist.
            ContentValues values = new ContentValues(1); //ContentValues class is used to store a set of values
                                                         //that the ContentResolver can process.
            values.put(MediaStore.Audio.Playlists.NAME, name);
            Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            id = Long.parseLong(uri.getLastPathSegment());
        } else {
            // We are overwriting an existing playlist. Clear existing songs.
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
            resolver.delete(uri, null, null);
        }

        return id;
    }

    /**
     * Run the given query and add the results to the given playlist. Should be
     * run on a background thread.
     *
     * @param resolver A ContentResolver to use.
     * @param Name The MediaStore.Audio.Playlist id of the playlist to
     * modify.
     * @param AudID The cursor pointing to songs to be added. The audio id should be the first column.
     * @return The number of songs that were added to the playlist.
     */
    public static int addToPlaylist(ContentResolver resolver, String Name, long[] AudID, int count) {

        long playlistId = getPlaylistIDfromName(resolver, Name);

        if (playlistId == -1)
            playlistId = createPlaylist(resolver, Name);
        if (playlistId == -1)
            return 0;

        // Find the greatest PLAY_ORDER in the playlist
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        String[] projection = new String[] { MediaStore.Audio.Playlists.Members.PLAY_ORDER }; //PLAY_ORDER is order of the songs in the playlist
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        int base = 0;
        if (cursor.moveToLast())
            base = cursor.getInt(0) + 1;
        cursor.close();

        if (count > 0) {
            ContentValues[] values = new ContentValues[count];
            for (int i = 0; i != count; ++i) {
                ContentValues value = new ContentValues(2);
                value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + i));
                value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, AudID[i]);
                values[i] = value;
            }
            resolver.bulkInsert(uri, values);
        }

        return count;
    }

    /**
     * Delete the playlist with the given id.
     *
     * @param resolver A ContentResolver to use.
     * @param id The Media.Audio.Playlists id of the playlist.
     */
    public static void deletePlaylist(ContentResolver resolver, long id)
    {
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
        resolver.delete(uri, null, null);
    }

    /**
     * Rename the playlist with the given id.
     *
     * @param resolver A ContentResolver to use.
     * @param id The Media.Audio.Playlists id of the playlist.
     * @param newName The new name for the playlist.
     */
    public static void renamePlaylist(ContentResolver resolver, long id, String newName) {

        long existingId = getPlaylistIDfromName(resolver, newName);
        // We are already called the requested name; nothing to do.
        if (existingId == id)
            return;
        // There is already a playlist with this name. Kill it.
        if (existingId != -1)
            deletePlaylist(resolver, existingId);

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Audio.Playlists.NAME, newName);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);
    }



    // Projection to get high water mark of PLAY_ORDER in a particular playlist
    public static final String[] PROJECTION_PLAYLISTMEMBERS_PLAYORDER = new String[] {
            MediaStore.Audio.Playlists.Members._ID,
            MediaStore.Audio.Playlists.Members.PLAY_ORDER
    };
}
