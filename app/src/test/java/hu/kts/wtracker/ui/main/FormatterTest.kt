package hu.kts.wtracker.ui.main

import org.junit.Assert
import org.junit.Test

class FormatterTest {

    @Test
    fun test() {
        Assert.assertEquals(0.toTimeString(), "0:00:00")
        Assert.assertEquals(5.toTimeString(), "0:00:05")
        Assert.assertEquals(60.toTimeString(), "0:01:00")
        Assert.assertEquals(3600.toTimeString(), "1:00:00")
    }
}