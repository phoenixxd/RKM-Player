package com.example.anshul.rkmstation.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anshul.rkmstation.R;

public class ListRowHolder extends RecyclerView.ViewHolder{
    protected ImageView thumbnail;
    protected TextView title;
    protected  TextView artist;
//    protected  ImageView imageView;

    public ListRowHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
        this.artist = (TextView) view.findViewById(R.id.artist);
//        this.imageView = (ImageView) view.findViewById(R.id.imageView);
    }

}