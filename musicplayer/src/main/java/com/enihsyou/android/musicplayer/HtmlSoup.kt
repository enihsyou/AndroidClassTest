package com.enihsyou.android.musicplayer

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HtmlSoup(htmlBody: String, baseUrl: String) {
    private val document = Jsoup.parse(htmlBody, baseUrl) ?: Document(baseUrl)

    fun extractEntities(): List<EverythingFile> = document.body()
        .select("[class^=trdata]").map {
            val nameSelector = it.selectFirst(".file a")
            val pathSelector = it.select(".pathdata a").last()
            val sizeSelector = it.selectFirst(".sizedata")
            val timeSelector = it.selectFirst(".modifieddata")
            val name = nameSelector.text()
            val path = pathSelector.text()
            val size = sizeSelector.text()
            val time = timeSelector.text()
            val url = nameSelector.attr("abs:href")
            EverythingFile(name, path, size, time, url)
        }
}

