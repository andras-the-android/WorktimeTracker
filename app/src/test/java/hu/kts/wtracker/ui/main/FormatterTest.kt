package hu.kts.wtracker.ui.main

import org.junit.Assert
import org.junit.Test

class FormatterTest {

    @Test
    fun test() {
        Assert.assertEquals("0:00:00", 0.hMmSsFormat())
        Assert.assertEquals("0:00:05", 5.hMmSsFormat(), )
        Assert.assertEquals("0:01:00", 60.hMmSsFormat())
        Assert.assertEquals("1:00:00", 3600.hMmSsFormat())
        Assert.assertEquals("10:00:00", 36000.hMmSsFormat())
    }
}
