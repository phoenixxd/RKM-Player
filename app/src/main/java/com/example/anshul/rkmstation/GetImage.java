package com.example.anshul.rkmstation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.annotation.Nullable;

public class GetImage {
    public static Bitmap getImage(String path, int width, int height) {

        try{
            Bitmap bmp = getImage(path);
            if(bmp != null) return Bitmap.createScaledBitmap(bmp, width, height, true);
            return null;
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static Bitmap getImage(String path ) {
        try{
            //meta data is a set of data that describes and gives information about other data.
            //MediaMetadataRetriever class provides a unified interface for retrieving frame and meta data from an input media file.
            byte[] art;
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            art = mmr.getEmbeddedPicture();

            /*BitmapFactory attempts to allocate memory for the constructed bitmap and therefore
              can easily result in an OutOfMemory exception. Each type of decode method
              (decodeByteArray(), decodeFile(), decodeResource(), etc.) has additional
              signatures that let you specify decoding options via the " BitmapFactory.Options " class.
              Setting the inJustDecodeBounds property to true while decoding avoids memory allocation,
              returning null for the bitmap object but setting outWidth, outHeight and outMimeType.
              This technique allows you to read the dimensions and type of the image data prior to
              construction (and memory allocation) of the bitmap.*/

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            if(art == null) return null;
            Bitmap bmp = BitmapFactory.decodeByteArray(art, 0, art.length, options);
            //Bitmap bmp = BitmapFactory.decodeByteArray(art, 0, art.length);

            mmr.release();
            return bmp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
