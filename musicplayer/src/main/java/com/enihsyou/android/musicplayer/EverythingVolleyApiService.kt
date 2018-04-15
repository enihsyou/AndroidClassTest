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
    /*创建一个请求队列*/
    private val queue = Volley.newRequestQueue(ctx)

    fun search(
        query: String,
        onSuccess: (List<EverythingFile>) -> Unit,
        onError: (VolleyError) -> Unit
    ): Request<List<EverythingFile>> {
        /*构造URL*/
        val url = BASE_URL.paths().queries("search" to query)
        /*构造自定义请求*/
        val request = EverythingVolleyRequest(url, onSuccess, onError)
        /*添加到请求队列*/
        return queue.add(request)
    }
}

/**自定义的请求体，为了方便*/
class EverythingVolleyRequest(
    method: Int,
    url: String,
    private val onSuccess: (List<EverythingFile>) -> Unit,
    onError: (VolleyError) -> Unit
) : Request<List<EverythingFile>>(method, url, onError) {

    /**快速创建一个[Request.Method.GET]请求*/
    constructor(
        url: String,
        onSuccess: (List<EverythingFile>) -> Unit,
        onError: (VolleyError) -> Unit
    ) : this(Method.GET, url, onSuccess, onError)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<List<EverythingFile>> {
        response ?: return Response.error(VolleyError())

        /*解析返回体 为字符串*/
        val charset = Charset.forName(HttpHeaderParser.parseCharset(headers, Charsets.UTF_8.name()))
        val body = response.run { String(data, charset) }

        /*对response的操作，提取内容*/
        val soup = HtmlSoup(body, BASE_URL)
        val result: List<EverythingFile> = soup.extractEntities()

        /*返回成功的结果*/
        Log.i(this.javaClass.simpleName, "搜索: $url, 返回: ${response.statusCode}, 结果数量: ${result.size}")
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response))
    }

    /**请求成功之后，调用回掉函数*/
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
