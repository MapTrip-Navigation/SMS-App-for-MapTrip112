package de.infoware.smsparser

import de.infoware.smsparser.processor.DefaultSmsProcessor
import de.infoware.smsparser.processor.SmsProcessorFactory
import de.infoware.smsparser.processor.TetraK01Processor
import de.infoware.smsparser.processor.TetraTVPNProcessor
import de.infoware.smsparser.processor.util.STANDARD_SMS
import de.infoware.smsparser.processor.util.TETRA_K01
import de.infoware.smsparser.processor.util.TETRA_TVPN
import org.junit.Assert
import org.junit.Test

class SmsProcessorFactoryTest {

    @Test
    fun defaultProcessor() {
        Assert.assertTrue(SmsProcessorFactory.getSmsProcessor("") is DefaultSmsProcessor)
    }

    @Test
    fun standardProcessor() {
        Assert.assertTrue(SmsProcessorFactory.getSmsProcessor(STANDARD_SMS) is DefaultSmsProcessor)
    }

    @Test
    fun tetraK01() {
        Assert.assertTrue(SmsProcessorFactory.getSmsProcessor(TETRA_K01) is TetraK01Processor)
    }

    @Test
    fun tetraTVPN() {
        Assert.assertTrue(SmsProcessorFactory.getSmsProcessor(TETRA_TVPN) is TetraTVPNProcessor)
    }
}