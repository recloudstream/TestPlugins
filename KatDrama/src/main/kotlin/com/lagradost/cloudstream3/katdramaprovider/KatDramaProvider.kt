package com.lagradost.cloudstream3.katdramaprovider

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.nodes.Element

class KatDramaProvider : MainAPI() {
    override var mainUrl = "https://katdrama.org"
    override var name = "KatDrama"
    override val hasMainPage = true
    override var lang = "hi"
    override val supportedTypes = setOf(TvType.TvSeries, TvType.Movie, TvType.AsianDrama)

    override val mainPage = mainPageOf(
        "$mainUrl/" to "Latest",
        "$mainUrl/category/korean-drama-hindi-dubbed/" to "Korean Drama",
        "$mainUrl/category/movies/" to "Movies",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page <= 1) request.data else "${request.data}page/$page/"
        val document = app.get(url).document
        val home = document.select("article, .post, .item").mapNotNull {
            val title = it.selectFirst("h2 a, .entry-title a")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("h2 a, .entry-title a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src")
            newTvSeriesSearchResponse(title, href, TvType.TvSeries) {
                this.posterUrl = poster
            }
        }
        return newHomePageResponse(request.name, home)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/?s=${query.replace(" ", "+")}").document
        return document.select("article").mapNotNull {
            val title = it.selectFirst("h2 a")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("h2 a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src")
            newTvSeriesSearchResponse(title, href, TvType.TvSeries) {
                this.posterUrl = poster
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text() ?: ""
        val poster = document.selectFirst("img.wp-post-image")?.attr("src")
        val description = document.select(".entry-content").text()
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = description
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        document.select("a").forEach { element ->
            val href = element.attr("href")
            if (href.startsWith("http") && !href.contains(mainUrl)) {
                loadExtractor(href, data, subtitleCallback, callback)
            }
        }
        document.select("iframe").forEach { element ->
            val src = element.attr("src")
            if (src.startsWith("http")) {
                loadExtractor(src, data, subtitleCallback, callback)
            }
        }
        return true
    }
}
