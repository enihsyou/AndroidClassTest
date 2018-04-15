package com.enihsyou.android.musicplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset

class EverythingVolleyApiService(ctx: Context) {
    private val queue = Volley.newRequestQueue(ctx)

    fun search(
        query: String,
        onSuccess: (List<EverythingFile>) -> Unit,
        onError: (VolleyError) -> Unit
    ): Request<List<EverythingFile>> {
        val url = BASE_URL.paths().queries("search" to query)
        val request = EverythingVolleyRequest(url, onSuccess, onError)
        return queue.add(request)
    }
}

class EverythingVolleyRequest(
    method: Int,
    url: String,
    private val onSuccess: (List<EverythingFile>) -> Unit,
    onError: (VolleyError) -> Unit
) : Request<List<EverythingFile>>(method, url, onError) {

    constructor(
        url: String,
        onSuccess: (List<EverythingFile>) -> Unit,
        onError: (VolleyError) -> Unit
    ) : this(Method.GET, url, onSuccess, onError)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<List<EverythingFile>> {
        response ?: return Response.error(VolleyError())

        val body = response.run { String(data, Charset.forName(HttpHeaderParser.parseCharset(headers, Charsets.UTF_8.name()))) }
        val soup = HtmlSoup(body, BASE_URL)
        val result: List<EverythingFile> = soup.extractEntities()

        Log.i(this.javaClass.simpleName, "搜索: $url, 返回: ${response.statusCode}, 结果数量: ${result.size}")
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: List<EverythingFile>) {
        onSuccess(response)
    }
}

private fun String.paths(vararg paths: String): Uri.Builder {
    val builder = Uri.parse(this).buildUpon()
    paths.forEach { builder.appendPath(it) }
    return builder
}

private fun Uri.Builder.queries(vararg queries: Pair<String, String>): String {
    queries.forEach { (key, value) -> appendQueryParameter(key, value) }
    return toString()
}
