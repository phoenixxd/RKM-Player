package com.example.anshul.rkmstation.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.anshul.rkmstation.R;

import java.util.List;

import static com.example.anshul.rkmstation.GetImage.getImage;

public class MyRecyclerAdapter extends RecyclerView.Adapter<ListRowHolder>{


    private List<Item> itemList;;
    private Context mContext;  /* it's the context of current state of the application/object.
                                  It lets newly-created objects understand what has been going on.
                                  Typically you call it to get information regarding another part of your program*/

    public MyRecyclerAdapter(Context context, List<Item> itemList) {
        this.itemList = itemList;
        this.mContext = context;
//        new ;
    }

    @Override
    public ListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //LayoutInflater takes as input an XML file and builds the View objects from it.
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        ListRowHolder mh = new ListRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ListRowHolder listRowHolder, int i) {
        final Item item = itemList.get(i);

        if(item != null){
            //Handle click event on both title and image click
            listRowHolder.title.setOnClickListener(clickListener);
            listRowHolder.thumbnail.setOnClickListener(clickListener);
            listRowHolder.artist.setOnClickListener(clickListener);

            listRowHolder.title.setTag(listRowHolder);
            listRowHolder.thumbnail.setTag(listRowHolder);
            listRowHolder.artist.setTag(listRowHolder);

            listRowHolder.title.setText(item.getTitle());
            listRowHolder.thumbnail.setImageResource (R.drawable.musi);
            new BitmapWorkerTask(listRowHolder.thumbnail).execute(item.getPath());
            listRowHolder.artist.setText(item.getArtist());

        }

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ListRowHolder holder = (ListRowHolder) view.getTag();
            int position = holder.getAdapterPosition();

            Item item = itemList.get(position);
            Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();
        }
    };

    class BitmapWorkerTask extends AsyncTask<String, String, Bitmap> {
        /*AsyncTask enables proper and easy use of the UI thread.
          This class allows you to perform background operations and publish results on the
          UI thread without having to manipulate threads and/or handlers*/

        //  private final String thisimgid;
        private final ImageView thisimageview;
        // Decode image in background
        BitmapWorkerTask(ImageView view)
        {
            thisimageview = view;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm = getImage(params[0], 60, 60);
            // memoryCache.put(thisimgid, bm);//add to the cache
            return bm;
            // int bytes=bm.getByteCount();
            // Log.d("Bytes for " + itemname[position] + ": ", String.valueOf(bytes));
        }
        //
// boolean imageViewReused(ImageView v, String url){
// String tag=imageViews.get(v);
// return (tag==null || !tag.equals(url));
// }
// Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null && thisimageview != null){
//                imageViews.put(thisimageview, thisimgid);
                thisimageview.setImageBitmap(bitmap);
            }
        }
    }


    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }


}