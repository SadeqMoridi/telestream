package com.telestream.providers

import com.lagradost.cloudstream3.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap

object ProviderManager {
    val providers = mutableListOf<MainAPI>()

    // Simple thread-safe in-memory cache
    private val searchCache = ConcurrentHashMap<String, List<SearchResponse>>()
    private val loadCache = ConcurrentHashMap<String, LoadResponse>()

    init {
        register(KissKH())
        register(AvaMovie())
        register(FaselHD())
    }

    fun register(provider: MainAPI) {
        providers.add(provider)
    }

    fun getProvider(name: String): MainAPI? {
        return providers.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    fun isPersianText(text: String): Boolean {
        return text.any { it in '\u0600'..'\u06FF' || it in '\uFB50'..'\uFDFF' || it in '\uFE70'..'\uFEFF' }
    }

    suspend fun search(query: String): List<SearchResponse> = coroutineScope {
        val cacheKey = query.trim().lowercase()
        searchCache[cacheKey]?.let { return@coroutineScope it }

        val isFa = isPersianText(query)
        val targetProviders = if (isFa) {
            providers.filter { it.lang == "fa" || it.lang == "multi" }.ifEmpty { providers }
        } else {
            providers.filter { it.lang == "en" || it.lang == "multi" }.ifEmpty { providers }
        }

        val deferreds = targetProviders.map { provider ->
            async {
                try {
                    provider.search(query)
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }

        val results = deferreds.awaitAll().flatten()
        searchCache[cacheKey] = results
        results
    }

    suspend fun load(providerName: String, url: String): LoadResponse? {
        val cacheKey = "$providerName:$url"
        loadCache[cacheKey]?.let { return it }

        val provider = getProvider(providerName) ?: return null
        val response = try {
            provider.load(url)
        } catch (e: Exception) {
            null
        }

        if (response != null) {
            loadCache[cacheKey] = response
        }
        return response
    }

    suspend fun loadLinks(providerName: String, data: String): List<ExtractorLink> {
        val provider = getProvider(providerName) ?: return emptyList()
        val links = mutableListOf<ExtractorLink>()
        try {
            provider.loadLinks(data) { link ->
                links.add(link)
            }
        } catch (e: Exception) {
            // Log or ignore
        }
        return links
    }
}
