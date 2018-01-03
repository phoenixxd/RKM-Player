package com.example.anshul.rkmstation;

import android.util.Log;
import android.widget.SeekBar;

public class Utilities {

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
//
//    /**
//     * Function to get Progress percentage
//     * @param currentDuration
//     * @param totalDuration
//     * */
//    public double getProgressPercentage(long currentDuration, long totalDuration){
//        Double percentage = (double) 0;
//
//        long currentSeconds = (int) (currentDuration / 1000);
//        long totalSeconds = (int) (totalDuration / 1000);
//
//        // calculating percentage
//        percentage =(((double)currentSeconds)/totalSeconds)*100;
//
//        // return percentage
//        return percentage;
//    }

    /**
     * Function to change progress to timer
     * returns current duration in milliseconds
     * */
    public double progressToTimer(SeekBar S) {
        Log.e("SEEKPROGRESS", String.valueOf(S.getProgress()));
        Log.e("SEEKMAX", String.valueOf(S.getMax()));
        return S.getProgress();
    }
}