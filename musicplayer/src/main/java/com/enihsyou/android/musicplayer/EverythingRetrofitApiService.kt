@file:Suppress("unused")

package com.enihsyou.android.musicplayer

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

interface EverythingRetrofitApiService {
    @GET("/")
    fun search(@Query("search") query: String): Call<List<EverythingFile>>

    companion object {
        fun create(): EverythingRetrofitApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(EverythingResponseConverterFactory())
                .build()
            return retrofit.create(EverythingRetrofitApiService::class.java)
        }
    }
}

class EverythingResponseConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? = EverythingHtmlConverter

    private object EverythingHtmlConverter : Converter<ResponseBody, List<EverythingFile>> {
        override fun convert(value: ResponseBody): List<EverythingFile> {
            val soup = HtmlSoup(value.string(), BASE_URL)

            return soup.extractEntities()
        }
    }
}

fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>) = success(response)

        override fun onFailure(call: Call<T>?, t: Throwable) = failure(t)
    })
}
