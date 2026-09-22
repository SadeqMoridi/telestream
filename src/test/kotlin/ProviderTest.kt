package com.telestream

import com.lagradost.cloudstream3.TvType
import com.telestream.providers.AvaMovie
import com.telestream.providers.FaselHD
import com.telestream.providers.KissKH
import com.telestream.providers.ProviderManager
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProviderTest {

    @Test
    fun testProviderRegistration() {
        assertNotNull(ProviderManager.getProvider("KissKH"))
        assertNotNull(ProviderManager.getProvider("AvaMovie (فارسی)"))
        assertNotNull(ProviderManager.getProvider("FaselHD (العربية)"))
        assertEquals(3, ProviderManager.providers.size)
    }

    @Test
    fun testPersianTextDetection() {
        assertTrue(ProviderManager.isPersianText("جوکر"))
        assertTrue(ProviderManager.isPersianText("پوست شیر"))
        assertTrue(ProviderManager.isPersianText("سلام"))
        assertFalse(ProviderManager.isPersianText("Spider-Man"))
        assertFalse(ProviderManager.isPersianText("Dune Part 2"))
    }

    @Test
    fun testProvidersMetadata() {
        val kiss = KissKH()
        assertEquals("en", kiss.lang)
        assertTrue(kiss.supportedTypes.contains(TvType.TvSeries))

        val ava = AvaMovie()
        assertEquals("fa", ava.lang)
        assertTrue(ava.supportedTypes.contains(TvType.Movie))

        val fasel = FaselHD()
        assertEquals("ar", fasel.lang)
    }
}
