package com.example.anshul.rkmstation;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCache {

    private static final String TAG = "MemoryCache";

    /*LINKED HASHMAP: Hash table and linked list implementation of the Map interface,
      with predictable iteration order. This implementation differs from HashMap in that it
      maintains a doubly-linked list running through all of its entries. This linked list
      defines the iteration ordering, which is normally the order in which keys were inserted
      into the map (insertion-order).*/

    private Map<String, Bitmap> cache = new LinkedHashMap(0, 0.75f, true);//Last argument true for LRU ordering.
                                                                          // If false, then insertion order.
    private long size=0;//current allocated size
    private long limit=1000000;//max memory in bytes

    public MemoryCache(int percentage){
        //use 25% of available heap size

        /*Runtime: Every Java application has a single instance of class
          <code>Runtime</code> that allows the application to interface with
          the environment in which the application is running. The current
          runtime can be obtained from the <code>getRuntime</code> method.*/

        /*maxMemory(): Returns the maximum amount of memory that the Java virtual machine will
          attempt to use*/

        setLimit((long)(Runtime.getRuntime().maxMemory() * percentage / 100.0)); //25% of the maximum amount of memory
                                                                                 //that the JVM will attempt to use
    }

    public void setLimit(long new_limit){
        limit = new_limit;
        Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
    }

    public Bitmap get(String id){
        try{
            if(!cache.containsKey(id))
                return null;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            return cache.get(id);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public boolean isSet(String id)
    {
        try{
            if(!cache.containsKey(id))
                return false;
            else return true;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void put(String id, Bitmap bitmap){
        try{
            if(cache.containsKey(id))
                size -= getSizeInBytes(cache.get(id));
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }

    private void checkSize() {
        Log.i(TAG, "cache size="+size+" length="+cache.size());
        if(size>limit){
            Iterator<Map.Entry<String, Bitmap>> iter = cache.entrySet().iterator();//least recently accessed item will be the first one iterated
            while(iter.hasNext()){
                Map.Entry<String, Bitmap> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit)
                    break;
            }
            Log.i(TAG, "Clean cache. New size "+cache.size() + "   " + size);
        }
    }

    public void clear() {
        try{
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
            cache.clear();
            size=0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }

    long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}
