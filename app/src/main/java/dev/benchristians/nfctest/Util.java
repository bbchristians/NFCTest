package dev.benchristians.nfctest;

import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Util {

    public static long getChildbirthTime() {
        Random random = new java.util.Random();
        long eventTime = System.currentTimeMillis() + random.nextInt(30000) + 15000;
        new Timer().schedule(new TimedEvent(), eventTime);
        return eventTime;
    }

    public static class TimedEvent extends TimerTask {

        @Override
        public void run() {
            Log.i("tag?","This is when the mating happens");
        }
    }
}