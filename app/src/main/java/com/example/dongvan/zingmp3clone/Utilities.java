package com.example.dongvan.zingmp3clone;

/**
 * Created by VoNga on 05-May-17.
 */

public class Utilities {
    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     * */
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String minutesString="";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);

        // Add hours if there
        if(hours > 0 ){
            if(hours<10){
                finalTimerString = "0"+hours+ ":";
            } else {
                finalTimerString = hours + ":";
            }
        }

        // Add minutes if there
        if(minutes > 0 ){
            if(minutes<10){
                minutesString = "0"+minutes+ ":";
            } else {
                minutesString = minutes + ":";
            }
        }else{
            minutesString = "00:";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutesString + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static int getProgressPercentage(int currentDuration, int totalDuration) {
        Double percentage = (double) 0;
        int currentSeconds = (currentDuration / 1000);
        int totalSeconds = (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }
}
