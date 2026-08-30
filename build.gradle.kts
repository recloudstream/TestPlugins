package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Document

class MultiMoviesProvider : MainAPI() {
    override var mainUrl = "https://multimovies.beer"
    override var name = "MultiMovies"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    override var hasMainPage = true

    override suspend fun getMainPage(page: Int, request: HomePageRequest): HomePageResponse? {
        val html = app.get(mainUrl).text
        val doc = org.jsoup.Jsoup.parse(html)
        val homePages = mutableListOf<HomePageList>()

        doc.select("div.items-section, section.featured-box, div.movies-widget").forEach { section ->
            val headerTitle = section.selectFirst("h2")?.text() ?: "New Additions"
            val extractedItems = section.select("div.item, div.movie-card").mapNotNull { item ->
                val title = item.selectFirst("h3")?.text() ?: return@mapNotNull null
                val itemUrl = item.selectFirst("a")?.attr("href") ?: return@mapNotNull null
                val posterUrl = item.selectFirst("img")?.attr("src") ?: ""
                
                MovieSearchResponse(
                    name = title,
                    url = itemUrl,
                    apiName = this.name,
                    type = TvType.Movie,
                    posterUrl = posterUrl
                )
            }
            if (extractedItems.isNotEmpty()) {
                homePages.add(HomePageList(headerTitle, extractedItems))
            }
        }
        return HomePageResponse(homePages)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val searchUrl = "$mainUrl/?s=${query.replace(" ", "+")}"
        val html = app.get(searchUrl).text
        val doc = org.jsoup.Jsoup.parse(html)

        return doc.select("div.result-item, div.search-result a").mapNotNull { element ->
            val title = element.selectFirst("h3, div.title")?.text() ?: element.text()
            val url = element.attr("href") ?: element.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = element.selectFirst("img")?.attr("src") ?: ""

            MovieSearchResponse(
                name = title,
                url = url,
                apiName = this.name,
                type = TvType.Movie,
                posterUrl = poster
            )
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (Video) -> Unit
    ): Boolean {
        val html = app.get(data).text
        val doc = org.jsoup.Jsoup.parse(html)

        doc.select("iframe, video source").forEach { element ->
            val videoUrl = element.attr("src").takeIf { it.isNotEmpty() } ?: element.attr("src")
            if (videoUrl.contains("m3u8") || videoUrl.contains("mp4")) {
                callback.invoke(Video(url = videoUrl, name = "Mirror Stream", referer = mainUrl))
            }
        }
        return true
    }
}
