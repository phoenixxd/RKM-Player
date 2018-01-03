package com.example.anshul.rkmstation;

/*
* created by anshul kishore
*/

import android.app.Activity;
import android.app.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.os.Debug;
import android.os.Handler;

import android.support.v4.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import android.view.MotionEvent;
import java.util.Random;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;

import com.example.anshul.rkmstation.Utilities;

import org.jsoup.select.Evaluator;

import java.io.IOException;

import static com.example.anshul.rkmstation.GetImage.getImage;


public class UI extends Activity implements MediaPlayer.OnCompletionListener,SeekBar.OnSeekBarChangeListener, Runnable {
    private ImageView play, next, prev, forw, backw, back, playlist, lyrics, iv, panel2, panel, shuffle, repeat;
    private static ImageView lyrics_background;
    private EditText customsongname;
    private static MediaPlayer mediaPlayer;
    private static TextView lyrics_text;
    private long startTime = 0;
    private long finalTime = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private int i = 0, flag = 1,repeatValue=0,shuffleValue=0,playValue=0,nextValue=0;
    private float x1, x2, y1, y2,z;
    private SeekBar seekbar;
    private TextView tx1, tx2, tx3;
    private Utilities utils;
    public static boolean MediaPlayerCreated = false;
    private boolean reset = false;
    private boolean isRepeat = false, isShuffle = false;
    private final String TAG = "MEDIAPLAYERUI";
    private GestureDetector gestureDetector;
    static boolean LyricsSet = false;
    private String customsongName;
    final int Id=45639623;
    private String currentplayingsongPath;

    static  NotificationManager nm;
    static NotificationCompat.Builder mBuilder;
    public UI() {
        Initialize();
    }



    /**
     * Function to play a song
     *
     * @param //songIndex - index of song
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("THISUI", "UI Destroyed");
//        Debug.stopMethodTracing();
    }

    int currsongpos;
    long totalsongs;
    ArrayList<String> songPaths;
    ArrayList<String> songTitles;
    ArrayList<String> songArtist;
    long[] sgId;
    Bitmap songimage;


    protected void Initialize() {
        currsongpos = LocalList.getcurrSongPos();
        totalsongs = LocalList.getSongsCount();
        songPaths = LocalList.getSongpaths();
        songTitles = LocalList.getSongTitles();
        songArtist = LocalList.getSongArtist();
//        sgId = LocalList.getSongId();
        // songimage=LocalList.getImage(songPaths);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("THISUI", "onStart");
    }

    @Override
    protected void onResume() {
        Log.d("THISUI", "UI resume");
        super.onResume();


        nm = (NotificationManager) this
                .getSystemService(this.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(this);

        Initialize();

        //set the title
        tx2.setText(songTitles.get(currsongpos));

        if (!MediaPlayerCreated) {
            currentplayingsongPath = songPaths.get(currsongpos);

            File aud = new File(currentplayingsongPath);
            Uri pathUri = Uri.fromFile(aud);
//            Log.d(TAG, "path:" + songPaths.get(currsongpos));
//            Log.d(TAG, "Uri:" + pathUri);

            mediaPlayer = MediaPlayer.create(this, pathUri);
            if (mediaPlayer == null)
                Log.e(TAG, "MEDIAPLAYERCREATEDNULL");

            MediaPlayerCreated = true;
            resetandPlay();
        } else {

            if(!(currentplayingsongPath == songPaths.get(currsongpos)))
            {
                mediaPlayer.stop();
                playValue = 0;
                try {
                    int x = currsongpos;
                    mediaPlayer.setDataSource(songPaths.get(currsongpos));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
                playValue = 1;

        }

        utils = new Utilities();
        seekbar.setOnSeekBarChangeListener(this);

        try {
            mediaPlayer.setOnCompletionListener(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d("YOYO", "MEDIAPLAYERCRASHED");
        }

        if(playValue == 0)resetandPlay();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("THISUI", "UI creatred");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main32);

        //gestureDetector = new GestureDetector(new SwipeGestureDetector());

        next = (ImageView) findViewById(R.id.next);
        next.setImageResource(R.drawable.next);
        //pause = (ImageView) findViewById(R.id.pause);
        //pause.setImageResource(R.drawable.pause);
        play = (ImageView) findViewById(R.id.play);
        play.setImageResource(R.drawable.play);
        prev = (ImageView) findViewById(R.id.prev);
        prev.setImageResource(R.drawable.prev);
        forw = (ImageView) findViewById(R.id.forw);
        forw.setImageResource(R.drawable.forw);
        backw = (ImageView) findViewById(R.id.backw);
        backw.setImageResource(R.drawable.backw);
        iv = (ImageView) findViewById(R.id.imageView);
        //the abovve one is the thumbnail part hence is set when the song is played
        panel2 = (ImageView) findViewById(R.id.panel2);
        panel2.setImageResource(R.drawable.grey4);
        //shuffleg = (ImageView) findViewById(R.id.shuffleg);
        //shuffleg.setImageResource(R.drawable.shuffleg);
        shuffle = (ImageView) findViewById(R.id.shuffle);
        shuffle.setImageResource(R.drawable.shuffle);
        //repeatg = (ImageView) findViewById(R.id.repeatg);
        //repeatg.setImageResource(R.drawable.repeatg);
        repeat = (ImageView) findViewById(R.id.repeat);
        repeat.setImageResource(R.drawable.repeat);
        panel = (ImageView) findViewById(R.id.panel);
        panel.setImageResource(R.drawable.grey);
        back = (ImageView) findViewById(R.id.back);
        back.setImageResource(R.drawable.back);

        playlist = (ImageView) findViewById(R.id.playlist);
        playlist.setImageResource(R.drawable.playlist);
        lyrics = (ImageView) findViewById(R.id.lyrics);
        lyrics.setImageResource(R.drawable.lyrics);


        tx1 = (TextView) findViewById(R.id.tx1);
        tx2 = (TextView) findViewById(R.id.tx2);
        tx3 = (TextView) findViewById(R.id.tx3);
        tx1.setTextColor(Color.parseColor("#2BB3E5"));
        tx3.setTextColor(Color.parseColor("#2BB3E5"));
        tx2.setTextColor(Color.parseColor("#56C8F2"));

        //repeatg.setVisibility(View.INVISIBLE);
        //shuffleg.setVisibility(View.INVISIBLE);

//        bundle.putInt("currsongpos", position);
//        bundle.putInt("totalsongs", l);
//        bundle.putLongArray("songids", imgid);
//        bundle.putStringArrayList("songpaths", path );
//        bundle.putStringArrayList("songtitles", itemname);
//        bundle.putStringArrayList("songartist", artist);

        customsongname = (EditText) findViewById(R.id.customsongname);
        customsongname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    customsongname.setText("");
                } else
                    customsongname.setVisibility(View.INVISIBLE);
            }
        });

        seekbar = (SeekBar) findViewById(R.id.seekBar);

        lyrics_text = (TextView) findViewById(R.id.lyricstext);
        lyrics_text.setMovementMethod(new ScrollingMovementMethod());
        lyrics_background = (ImageView) findViewById(R.id.lyrics_background);

        lyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LyricsSet) {
                    lyrics_text.setText("");
                    lyrics_text.setVisibility(View.INVISIBLE);
                    lyrics_background.setVisibility(View.INVISIBLE);
                    lyrics.setImageResource(R.drawable.lyrics);
                } else {
                    String song_name = customsongName.replaceAll("_", " ").replaceAll("([[:alnum:]]*)([.])([[:alnum:]]*)", "").replaceAll("([(])([^(]*)([)])", "").replaceAll("([^[:alpha:] ]*)", "");
                    Lyrics get = new Lyrics(lyrics_text, lyrics_background, song_name);
                    get.setLyrics();
                    lyrics.setImageResource(R.drawable.lyricsg);
                }

                LyricsSet = !LyricsSet;
            }
        });

        lyrics.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                customsongname.setVisibility(View.VISIBLE);
                return true;
            }
        });

        customsongname.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    customsongName = customsongname.getText().toString();
                    hideLyrics();
                    lyrics.callOnClick();
                    customsongname.setVisibility(View.INVISIBLE);
                    return true;
                }
                return false;
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(playValue==0 || nextValue==1){
                    playSong(songPaths.get(currsongpos), songTitles.get(currsongpos),songArtist.get(currsongpos), reset);
                    play.setImageResource(R.drawable.pause);

                    playValue=1;
                    nextValue=0;
                }
                else {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                    playValue=0;
                }
            }
        });


        /*pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MMMPause", "pausing");
                Toast toast = Toast.makeText(getApplicationContext(), "Pausing Sound", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                mediaPlayer.pause();

                myHandler.removeCallbacks(UpdateSongTime);

                play.setImageResource(R.drawable.pause);
            }
        });
     */
        forw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int temp = (int) startTime;

                if ((temp + forwardTime) <= finalTime) {
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast toast = Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Cannot Jump forward 5 seconds", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        backw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int temp = (int) startTime;

                if ((temp - backwardTime) > 0) {
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast toast = Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextValue=1;
                // check for repeat is ON or OFF
                {
                    if (isRepeat) {
                        // repeat is on play same song again
                    } else if (isShuffle) {
                        // shuffle is on - play a random song
                        Random rand = new Random();
                        currsongpos = rand.nextInt(((int) totalsongs - 1) - 0 + 1) + 0;
                    } else {
                        // no repeat or shuffle ON - play next song
                        if (currsongpos + 1 <= ((int) totalsongs - 1)) currsongpos++;
                        else currsongpos = 0; // play first song
                    }
                    reset = true;
                    if(playValue==0){
                        play.callOnClick();

                        play.setImageResource(R.drawable.play);
                        mediaPlayer.pause();
                        playValue=0;
                    }
                    else play.callOnClick();

                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextValue=1;
                // check for repeat is ON or OFF
                if (isRepeat) {
                    // repeat is on play same song again
                } else if (isShuffle) {
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currsongpos = rand.nextInt(((int) totalsongs - 1) - 0 + 1) + 0;
                } else {
                    // no repeat or shuffle ON - play next song
                    if (currsongpos == 0) currsongpos = (int) totalsongs - 1;
                    else currsongpos--; // play first song
                }
                if(playValue==0){
                    play.callOnClick();

                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                    playValue=0;
                }
                else resetandPlay();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(UI.this, LocalList.class);
                    /*FLAG_ACTIVITY_SINGLE_TOP: If set, the activity will not be launched if it is already running
                      at the top of the history stack.*/

                    /*FLAG_ACTIVITY_CLEAR_TOP: If set, and the activity being launched is already running in the
                      current task, then instead of launching a new instance of that activity,
                      all of the other activities on top of it will be closed and this Intent
                      will be delivered to the (now on top) old activity as a new Intent.*/

                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(UI.this, LocalList.class);
                     /*FLAG_ACTIVITY_SINGLE_TOP: If set, the activity will not be launched if it is already running
                      at the top of the history stack.*/

                    /*FLAG_ACTIVITY_CLEAR_TOP: If set, and the activity being launched is already running in the
                      current task, then instead of launching a new instance of that activity,
                      all of the other activities on top of it will be closed and this Intent
                      will be delivered to the (now on top) old activity as a new Intent.*/

                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(repeatValue==0){
                    repeat.setImageResource(R.drawable.repeatg);
                    isRepeat = true;
                    Toast toast = Toast.makeText(getApplicationContext(), "Repeat mode On", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    repeatValue=1;

                    if(shuffleValue==1){
                        isShuffle = false;
                        shuffle.setImageResource(R.drawable.shuffle);
                        shuffleValue=0;
                    }
                }
                else{
                    repeat.setImageResource(R.drawable.repeat);
                    isRepeat = false;
                    Toast toast = Toast.makeText(getApplicationContext(), "Repeat mode Off", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    repeatValue=0;
                }
                //  if (isShuffle) shuffleg.callOnClick();

            }
        });

       /* repeatg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRepeat = false;
                Toast toast = Toast.makeText(getApplicationContext(), "Repeat mode Off", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                repeat.setImageResource(R.drawable.repeat);

            }
        });
       */
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleValue==0) {
                    isShuffle = true;
                    Toast toast = Toast.makeText(getApplicationContext(), "Shuffle mode On", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    shuffle.setImageResource(R.drawable.shuffleg);
                    shuffleValue=1;

                    if(repeatValue==1){
                        repeat.setImageResource(R.drawable.repeat);
                        isRepeat = false;
                        repeatValue=0;
                    }
                }
                else {
                    isShuffle = false;
                    Toast toast = Toast.makeText(getApplicationContext(), "Shuffle mode Off", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    shuffle.setImageResource(R.drawable.shuffle);
                    shuffleValue=0;
                }
                // shuffleg.setVisibility(View.VISIBLE);
                //if (isRepeat) repeatg.callOnClick();

            }
        });

        /* shuffleg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShuffle = false;
                Toast toast = Toast.makeText(getApplicationContext(), "Shuffle mode Off", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                shuffleg.setVisibility(View.INVISIBLE);
                shuffle.setVisibility(View.VISIBLE);

            }
        });
        */

    }

    public static void hideLyrics() {
        lyrics_text.setText("");
        lyrics_text.setVisibility(View.INVISIBLE);
        lyrics_background.setVisibility(View.INVISIBLE);
        LyricsSet = false;
    }

    public static void showLyrics() {
        lyrics_text.setVisibility(View.VISIBLE);
        lyrics_background.setVisibility(View.VISIBLE);
        lyrics_text.scrollTo(0, 0);
    }

    public void resetandPlay() {
        reset = true;
        play.callOnClick();
        reset = false;
    }

    public void playSong(String Path, String Title, String Artist, boolean reset) {

        hideLyrics();

        currentplayingsongPath = Path;

        tx2.setSelected(true);
        customsongName = Title;


        songimage = getImage(songPaths.get(currsongpos));
        if(songimage == null) iv.setImageResource(R.drawable.back2);
        else iv.setImageBitmap(songimage);
        songimage = null;

        // Play song
        Intent it= new Intent(this,UI.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        /*By giving a PendingIntent to another application, you are granting it the right to perform
          the operation you have specified as if the other application was yourself (with the same permissions and identity).*/
        PendingIntent pi=PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_CANCEL_CURRENT);
        /*FLAG_CANCEL_CURRENT: Flag indicating that if the described PendingIntent already exists,
          the current one should be canceled before generating a new one.*/

        NotificationCompat.Builder notification = mBuilder.setSmallIcon(R.drawable.musi).setWhen(0)
                .setContentTitle(Title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Title))
                .setAutoCancel(false)
                .setContentIntent(pi)
                .setContentText(Artist)
                .setSmallIcon(R.drawable.musi);
        nm.notify(Id,notification.build());
        // finish();

        try {
            if (reset) mediaPlayer.reset();
            mediaPlayer.setDataSource(Path);

            mediaPlayer.prepare();

            mediaPlayer.start();
            // Displaying Song title
            tx2.setText(Title);


        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mediaPlayer.start();

        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        tx3.setText(String.format("%02d:%02d ",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
        );
        int MINU = (int) TimeUnit.MILLISECONDS.toMinutes((long) startTime);
        int SECS = (int) TimeUnit.MILLISECONDS.toSeconds((long) startTime);
        tx1.setText(String.format(" %02d:%02d", MINU, SECS - TimeUnit.MINUTES.toSeconds(MINU)));

        seekbar.setMax((int) finalTime);
        seekbar.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime, 1000);

    }

    public void updateProgressBar() {
        myHandler.postDelayed(UpdateSongTime, 1000);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            int totalDuration = mediaPlayer.getDuration();
            tx1.setText(String.format(" %02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime))));
            // Updating progress bar
            seekbar.setProgress((int) startTime);
//            Log.d("SEEK", (int) (progress * totalDuration / 100) + "\t" + String.valueOf(startTime));
            updateProgressBar();
           // myHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {

    }

    @Override
    //onStartTrackingTouch(): Notification that the user has started a touch gesture
    public void onStartTrackingTouch(SeekBar seekbar) {
        //removeCallbacks(): Remove any pending posts of Runnable r that are in the message queue.
        //myHandler.removeCallbacks(UpdateSongTime);
        myHandler.removeCallbacks(UpdateSongTime);
    }

    @Override
    //onStopTrackingTouch(): Notification that the user has finished a touch gesture
    public void onStopTrackingTouch(SeekBar seekbar) {
        //myHandler.removeCallbacks(UpdateSongTime);
        // forward or backward to certain seconds
        mediaPlayer.seekTo((int) utils.progressToTimer(seekbar));
        // update and re-register timer progress again
        updateProgressBar();
        //myHandler.postDelayed(UpdateSongTime, 1000);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next.callOnClick();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        nextValue=1;
        switch (touchevent.getAction()) {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN: {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = touchevent.getX();
                y2 = touchevent.getY();

                z=y1-y2;
                if(z<0) z*=-1;
                //if left to right sweep event on screen
                if (x1 < x2 && (x2-x1)>=150) {
                    if(z<(x2-x1))
                        prev.callOnClick();
                }
                // if right to left sweep event on screen

                if (x1 > x2 && (x1-x2)>=150) {
                    if(z<(x1-x2))
                        next.callOnClick();
                }
            }

        }
        return false;

    }

    @Override
    protected void onPause() {
        Log.d("THISUI", "UI paused");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("THISUI", "UI stop");
        super.onStop();
    }

    @Override
    public void run() {

    }
}