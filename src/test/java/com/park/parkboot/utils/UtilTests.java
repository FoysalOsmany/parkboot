package com.park.parkboot.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UtilTests {
    @Test
    public void testGetTimeDiff() {
        assertEquals(0, Util.getTimeDiff(new Date(), new Date(), TimeUnit.MINUTES));
    }
}