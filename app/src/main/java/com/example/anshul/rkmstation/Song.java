package com.example.anshul.rkmstation;

/**
 * Created by abhishek on 29/9/16.
 */

public class Song
{

    private String title;
    private String artist;
    private String path;
    private long id;

    public Song(long songID, String songTitle, String songArtist, String p) {
//            count++;
        id=songID;
        title=songTitle;
        artist=songArtist;
        path = p;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getPath(){return path;}

}