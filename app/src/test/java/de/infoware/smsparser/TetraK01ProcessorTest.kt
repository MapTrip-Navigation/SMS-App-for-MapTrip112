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

    @Test(expected = IllegalArgumentException::class)
    fun incorrectStructure() {
        TetraK01Processor()
            .execute("#K01N5252082E1340940")
            .blockingGet()
    }
}