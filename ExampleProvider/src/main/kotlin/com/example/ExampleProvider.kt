package com.example
 
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class ExampleProvider : MainAPI() {
    override var mainUrl = "https://www.erome.com"
    override var name = "EroMe"
    override val supportedTypes = setOf(TvType.NSFW)
    override var lang = "en"
    override val hasMainPage = true

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/search?q=$query").document
        val results = ArrayList<SearchResponse>()
        
        document.select("div.album-card, div.col-sm-6").forEach { element ->
            val title = element.select("a.album-link, h4").text().trim()
            val href = element.select("a").attr("href")
            val poster = element.select("img").attr("src")
            
            if (href.isNotEmpty()) {
                results.add(newMovieSearchResponse(title, href, TvType.NSFW) {
                    this.posterUrl = poster
                })
            }
        }
        return results
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.select("h1").text().trim()
        val poster = document.select("meta[property=og:image]").attr("content")

        val episodes = ArrayList<Episode>()
        
        document.select("video source, div.media-group").forEachIndexed { index, element ->
            val videoUrl = element.attr("src")
            if (videoUrl.isNotEmpty()) {
                episodes.add(
                    Episode(
                        data = videoUrl,
                        name = "Part ${index + 1}",
                        episode = index + 1
                    )
                )
            }
        }

        return newMovieLoadResponse(title, url, TvType.NSFW, episodes) {
            this.posterUrl = poster
        }
    }

    
    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        callback.invoke(
            ExtractorLink(
                source = name,
                name = name,
                url = data,
                referer = mainUrl,
                quality = Qualities.P720.value,
                isM3u8 = data.contains(".m3u8")
            )
        )
        return true
    }
}
