package com.example.anshul.rkmstation;

import android.content.Intent;
import android.os.Bundle;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.example.anshul.rkmstation.FetchSongs;
import java.util.ArrayList;


public class MainList extends Activity{

    private ListView list;
    private String[] itemname;
    private Integer[] imgid;
    private FetchSongs songRetriever = new FetchSongs(this);
    private ArrayList<Playlist> playlists;
    private int playlistcount;
    private final int constantlistcount = 2;
    private int count = 0, totalplaylistdrawables = 6;
    //private int[] drawables = {R.drawable.p0,R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5 };
    private int[] drawables = {R.drawable.playlist4, R.drawable.playlist3, R.drawable.details };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlist);

        playlists = songRetriever.getPlaylists();
        playlistcount = playlists.size();

        itemname = new String[constantlistcount + playlistcount + 1];
        itemname[0] = "All Songs"; itemname[1] = "Favourite";

        for (int i = constantlistcount; i < constantlistcount + playlistcount; ++i)
            itemname[i] = playlists.get(i - constantlistcount).getTitle();

        imgid = new Integer[constantlistcount + playlistcount + 1];
        imgid[0] = R.drawable.local_music; imgid[1] = R.drawable.fav;

        int i = constantlistcount;
        for (i = constantlistcount; i < constantlistcount + playlistcount; ++i)
        {
            //imgid[i] = drawables[i % constantlistcount];//(int)playlists.get(i - constantlistcount).getID();
            imgid[i] = drawables[1];
        }

        //imgid[i] = drawables[i % constantlistcount];//(int)playlists.get(i - constantlistcount).getID();
        imgid[i] = drawables[2];
        itemname[i] = "Details";



        custom adapter = new custom(this, itemname, imgid);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,
                                    long id) {

                String Slecteditem = itemname[+position];

                if(position == 0) {
                    try {

                        Intent intent = new Intent(MainList.this,LocalList.class);
                        Bundle datatopass = new Bundle();
                        datatopass.putInt("playlistid", -1);
                        datatopass.putString("playlistname", itemname[0]);
                        intent.putExtras(datatopass);

                        startActivity(intent);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else if(position == 1)
                {
                    Toast.makeText(getApplicationContext(), "No Items Added yet..!", Toast.LENGTH_SHORT).show();

                }
                else if(position == constantlistcount + playlistcount )
                {
                    try {

                        Intent intent = new Intent(MainList.this,Details.class);
                        startActivity(intent);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {

                        Intent intent = new Intent(MainList.this,LocalList.class);
                        Bundle datatopass = new Bundle();
                        datatopass.putInt("playlistid", (int)playlists.get(position - constantlistcount).getID());
                        datatopass.putString("playlistname", playlists.get(position - constantlistcount).getTitle());

                        intent.putExtras(datatopass);
                        startActivity(intent);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }});

//        list.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                 TODO Auto-generated method stub
//                String Slecteditem= itemname[+position];
//                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

    }
};


