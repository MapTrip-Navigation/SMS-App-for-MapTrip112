package de.infoware.smsparser

import de.infoware.smsparser.processor.TetraK01Processor
import de.infoware.smsparser.processor.TetraTVPNProcessor
import org.junit.Assert
import org.junit.Test

class TetraTVPNProcessorTest {

    @Test
    fun smokeTest() {
        val info = TetraTVPNProcessor()
            .execute("TVPN03216774E00CC9C78")
            .blockingGet()
        Assert.assertTrue(Math.abs(52.52082 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(13.40940 - info.lon) < 1e-4)
        Assert.assertEquals("TVPN", info.reason)
    }

    @Test
    fun soSiWithoutFreeTextTest() {
        val info = TetraTVPNProcessor()
            .execute("TVPN03216774E00CC9C78;SoSi")
            .blockingGet()
        Assert.assertTrue(Math.abs(52.52082 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(13.40940 - info.lon) < 1e-4)
        Assert.assertEquals(true, info.blueLightRouting)
        Assert.assertEquals("TVPN", info.reason)
    }

    @Test
    fun freeTextWithoutSoSiTest() {
        val info = TetraTVPNProcessor()
            .execute("TVPN03216774E00CC9C78;Zimmerbrand")
            .blockingGet()
        Assert.assertTrue(Math.abs(52.52082 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(13.40940 - info.lon) < 1e-4)
        Assert.assertEquals(false, info.blueLightRouting)
        Assert.assertEquals("Zimmerbrand", info.reason)
    }

    @Test
    fun freeTextWithSoSiTest() {
        val info = TetraTVPNProcessor()
            .execute("TVPN03216774E00CC9C780;SoSi;Zimmerbrand")
            .blockingGet()
        Assert.assertTrue(Math.abs(52.52082 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(13.40940 - info.lon) < 1e-4)
        Assert.assertEquals(true, info.blueLightRouting)
        Assert.assertEquals("Zimmerbrand", info.reason)
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectStructure() {
        TetraTVPNProcessor()
            .execute("#TVPN0321677400CC9C78")
            .blockingGet()
    }
}