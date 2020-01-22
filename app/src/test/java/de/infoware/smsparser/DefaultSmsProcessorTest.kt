package de.infoware.smsparser

import de.infoware.smsparser.processor.DefaultSmsProcessor
import org.junit.Assert
import org.junit.Test

class DefaultSmsProcessorTest {
    @Test
    fun smokeTest() {
        val info = DefaultSmsProcessor()
            .execute("51.2123544, 6.12548543;Zimmerbrand Musterstrasse 26 3:OG")
            .blockingGet()
        Assert.assertTrue(Math.abs(51.2123544 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(6.12548543 - info.lon) < 1e-4)
        Assert.assertEquals("Zimmerbrand Musterstrasse 26 3:OG", info.reason)
    }

    @Test
    fun soSiWithoutFreeTextTest() {
        val info = DefaultSmsProcessor()
            .execute("51.2123544, 6.12548543;SoSi")
            .blockingGet()
        Assert.assertTrue(Math.abs(51.2123544 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(6.12548543 - info.lon) < 1e-4)
        Assert.assertEquals(true, info.blueLightRouting)
    }

    @Test
    fun soSiWithFreeTextTest() {
        val info = DefaultSmsProcessor()
            .execute("51.2123544, 6.12548543;SoSi;Zimmerbrand Musterstrasse")
            .blockingGet()
        Assert.assertTrue(Math.abs(51.2123544 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(6.12548543 - info.lon) < 1e-4)
        Assert.assertEquals(true, info.blueLightRouting)
        Assert.assertEquals("Zimmerbrand Musterstrasse", info.reason)
    }

    @Test
    fun correctTextTest() {
        val info = DefaultSmsProcessor()
            .execute("51.2123544, 6.12548543;")
            .blockingGet()
        Assert.assertTrue(Math.abs(51.2123544 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(6.12548543 - info.lon) < 1e-4)
        Assert.assertEquals("", info.reason)
    }

    @Test
    fun correctTextWithoutWhitespacesTest() {
        val info = DefaultSmsProcessor()
            .execute("51.2123544,6.12548543;")
            .blockingGet()
        Assert.assertTrue(Math.abs(51.2123544 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(6.12548543 - info.lon) < 1e-4)
        Assert.assertEquals("", info.reason)
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectEmptyTextTest() {
        DefaultSmsProcessor()
            .execute("")
            .blockingGet()
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectFormattedTextTest() {
        DefaultSmsProcessor()
            .execute("Zimmerbrand Musterstrasse 26 3:OG;51.2123544, 6.12548543")
            .blockingGet()
    }
}