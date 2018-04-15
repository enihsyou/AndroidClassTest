@file:Suppress("unused")

package com.enihsyou.android.musicplayer

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

interface EverythingRetrofitApiService {

    @GET("/")
    fun search(@Query("search") query: String): Call<List<EverythingFile>>

    companion object {
        /**创建service对象，静态方法*/
        fun create(): EverythingRetrofitApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                /*添加自定义的转换器*/
                .addConverterFactory(EverythingResponseConverterFactory()) // todo add gson
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(EverythingRetrofitApiService::class.java)
        }
    }
}

fun main(args: Array<String>) {

}
/**自定义的转换器*/
class EverythingResponseConverterFactory : Converter.Factory() {

    /**支持[type]转换格式就返回一个转换器*/
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit)
        : Converter<ResponseBody, *>? = EverythingHtmlConverter

    /**实现一个[Converter]转换器*/
    private object EverythingHtmlConverter : Converter<ResponseBody, List<EverythingFile>> {

        /**直接抽取数据*/
        override fun convert(value: ResponseBody): List<EverythingFile> =
            HtmlSoup(value.string(), BASE_URL).extractEntities()
    }
}

fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>) = success(response)
        override fun onFailure(call: Call<T>?, t: Throwable) = failure(t)
    })
}
