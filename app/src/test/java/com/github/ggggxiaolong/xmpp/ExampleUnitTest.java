package com.github.ggggxiaolong.xmpp;

import com.github.ggggxiaolong.xmpp.utils.Common;

import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testTime1() {
        String format = DateFormat.getDateInstance().format(new Date());
        assertEquals("", format);
    }

    @Test
    public void testTime2() {
        Date today = new Date(1475675031299L);
        assertTrue(Common.isToDay(today));
        assertFalse(Common.isTomorrow(today));
        today.setTime(today.getTime() - 24 * 60 * 60 * 1000);
        assertTrue(Common.isTomorrow(today));
    }

    @Test
    public void testTime3() {
        Date today = new Date(1475675031299L);
        assertEquals("", Common.getTime(today));
    }

    @Test
    public void testHashCode() {
        int code = "tanxl2".hashCode();
        assertEquals(code, 1);
    }
}