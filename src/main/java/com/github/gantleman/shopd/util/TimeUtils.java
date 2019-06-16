package com.github.gantleman.shopd.util;

public class TimeUtils {
    
    static final long ONE_SECOND = 1000L;

    public static int getTimeWhitInt(){
        long rest=System.currentTimeMillis()/ONE_SECOND;
        return (int)rest;
    }

    public static long getTimeWhitLong(){
        return System.currentTimeMillis()/ONE_SECOND;
    }

}