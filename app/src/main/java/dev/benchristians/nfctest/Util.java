package dev.benchristians.nfctest;

import java.util.Random;

public class Util {

    public static long getChildbirthTime() {
        Random random = new java.util.Random();
        return System.currentTimeMillis() + random.nextInt(30000) + 15000;
    }
}