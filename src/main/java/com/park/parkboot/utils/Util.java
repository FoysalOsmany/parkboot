package com.park.parkboot.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Util {
    public static long getTimeDiff(Date toDate, Date fromDate, TimeUnit timeUnit) {
        long diffInMillies = toDate.getTime() - fromDate.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}