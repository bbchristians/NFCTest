package dev.benchristians.nfctest;

import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Util {

    public static long getChildbirthTime() {
        Random random = new java.util.Random();
        long delay = System.currentTimeMillis() + random.nextInt(30000) + 15000;
        long eventTime = delay;
        new Timer().schedule(new TimedEvent(), delay);
        return eventTime;
    }

    public static class TimedEvent extends TimerTask {

        @Override
        public void run() {
            Log.i("tag?","This is when the mating happens");
        }
    }
}