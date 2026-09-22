package com.telestream

import com.telestream.bot.BotRunner
import com.telestream.providers.ProviderManager
import com.telestream.telegram.TelegramClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

fun main(): Unit = runBlocking {
    val logger = LoggerFactory.getLogger("Main")

    val botToken = System.getenv("BOT_TOKEN")?.trim()
    val port = System.getenv("PORT")?.toIntOrNull() ?: 7860

    logger.info("=====================================================")
    logger.info("🎬 Starting TeleStream Telegram Bot (Pure Kotlin JVM)")
    logger.info("👉 Active Providers: ${ProviderManager.providers.joinToString { it.name }}")
    logger.info("👉 Web Health Server: http://0.0.0.0:$port/health")
    logger.info("=====================================================")

    // Start embedded Ktor web server for health checks & container liveness
    val server = embeddedServer(CIO, port = port, host = "0.0.0.0") {
        routing {
            get("/") {
                call.respondText(
                    """{"service":"TeleStream Bot","status":"running","activeProviders":${ProviderManager.providers.size}}""",
                    ContentType.Application.Json
                )
            }
            get("/health") {
                call.respondText(
                    """{"status":"ok","engine":"pure-jvm-cloudstream"}""",
                    ContentType.Application.Json
                )
            }
        }
    }.start(wait = false)

    if (botToken.isNullOrBlank() || botToken == "YOUR_BOT_TOKEN") {
        logger.warn("⚠️ BOT_TOKEN environment variable is not set!")
        logger.warn("⚠️ Bot polling paused. Please set BOT_TOKEN in your environment or .env file.")
        logger.info("⚠️ Embedded web server is running on port $port for container health checks.")

        // Keep process alive for health check
        while (true) {
            delay(10000)
        }
    } else {
        logger.info("Connecting Telegram client...")
        val client = TelegramClient(botToken)
        val runner = BotRunner(client)

        launch {
            runner.startPolling()
        }
    }
}
