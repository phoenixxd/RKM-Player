package com.example.anshul.rkmstation;

public class Playlist {
    private String title;
    private long id;

    public Playlist(long songID, String songTitle) {
//            count++;
        id=songID;
        title=songTitle;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
}
