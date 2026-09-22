package com.telestream

import com.telestream.i18n.I18n
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class I18nTest {

    @Test
    fun testTranslations() {
        val welcomeEn = I18n.t("welcome", "en")
        assertTrue(welcomeEn.contains("TeleStream"))

        val welcomeFa = I18n.t("welcome", "fa")
        assertTrue(welcomeFa.contains("تله‌استریم"))

        val searchPromptEn = I18n.t("search_prompt", "en")
        assertTrue(searchPromptEn.contains("type the name"))

        val searchPromptFa = I18n.t("search_prompt", "fa")
        assertTrue(searchPromptFa.contains("نام فیلم"))
    }
}
