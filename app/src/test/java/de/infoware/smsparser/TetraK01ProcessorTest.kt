package de.infoware.smsparser

import de.infoware.smsparser.processor.TetraK01Processor
import org.junit.Assert
import org.junit.Test

class TetraK01ProcessorTest {

    @Test
    fun smokeTest() {
        val info = TetraK01Processor()
            .execute("#K01;N5252082E1340940")
            .blockingGet()
        Assert.assertTrue(Math.abs(52.52082 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(13.40940 - info.lon) < 1e-4)
        Assert.assertEquals("#K01", info.reason)
    }

    @Test
    fun soSiWithoutFreeTextTest() {
        val info = TetraK01Processor()
            .execute("#K01;N5252082E1340940;SoSi")
            .blockingGet()
        Assert.assertTrue(Math.abs(52.52082 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(13.40940 - info.lon) < 1e-4)
        Assert.assertEquals(true, info.blueLightRouting)
        Assert.assertEquals("#K01", info.reason)
    }

    @Test
    fun freeTextWithoutSoSiTest() {
        val info = TetraK01Processor()
            .execute("#K01;N5252082E1340940;Zimmerbrand")
            .blockingGet()
        Assert.assertTrue(Math.abs(52.52082 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(13.40940 - info.lon) < 1e-4)
        Assert.assertEquals(false, info.blueLightRouting)
        Assert.assertEquals("Zimmerbrand", info.reason)
    }

    @Test
    fun freeTextWithSoSiTest() {
        val info = TetraK01Processor()
            .execute("#K01;N5252082E1340940;SoSi;Zimmerbrand")
            .blockingGet()
        Assert.assertTrue(Math.abs(52.52082 - info.lat) < 1e-4)
        Assert.assertTrue(Math.abs(13.40940 - info.lon) < 1e-4)
        Assert.assertEquals(true, info.blueLightRouting)
        Assert.assertEquals("Zimmerbrand", info.reason)
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectStructure() {
        TetraK01Processor()
            .execute("#K01N5252082E1340940")
            .blockingGet()
    }
}