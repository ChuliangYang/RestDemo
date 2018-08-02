package com.me.cl.restdemo

import com.me.cl.restdemo.download.DownloadProgressInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by CL on 8/1/18.
 */
class RetrofitBuilder(var type: Int) {
    init {
        when (type) {
            JSON_SERVER -> if (retrofitJsonBuilder != null) {
            } else {
                if (okhttpJsonBuilder == null) {
                    okhttpJsonBuilder = OkHttpClient.Builder().apply {
                        addNetworkInterceptor {
                            val original = it.request()

                            // Request customization: add request headers
                            val requestBuilder = original.newBuilder()
                                    .header("Authorization", "Bearer FW_IUZfXnpAAAAAAAAAAUQiEsRgQfye3IUlzcigbFed8p_-oe27d2QotMAnAhaO4")

                            val request = requestBuilder.build()
                            it.proceed(request)
                        }
                        addNetworkInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        })
                        addNetworkInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                    }

                    retrofitJsonBuilder = Retrofit.Builder()
                            .baseUrl(base_url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                }
            }
            BINARY_SERVER -> {
                    okhttpBinaryBuilder = OkHttpClient.Builder()
                            .addNetworkInterceptor { chain -> chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer FW_IUZfXnpAAAAAAAAAAUQiEsRgQfye3IUlzcigbFed8p_-oe27d2QotMAnAhaO4").build()) }
                            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BASIC
                            })
                            .retryOnConnectionFailure(true)
                            .connectTimeout(1000, TimeUnit.SECONDS)


                    retrofitBinaryBuilder = Retrofit.Builder()
                            .baseUrl(file_server_url)
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                }
        }
    }

    fun setDownloadProgressListener(listener: DownloadProgressInterceptor.DownloadProgressListener): RetrofitBuilder {
        okhttpBinaryBuilder?.addNetworkInterceptor(DownloadProgressInterceptor(listener))
        return this
    }

    fun Build(): Retrofit? {
        when (type) {
            JSON_SERVER -> return retrofitJsonBuilder?.client(okhttpJsonBuilder?.build())?.build()
            BINARY_SERVER -> return retrofitBinaryBuilder?.client(okhttpBinaryBuilder?.build())?.build()
        }
        return null
    }

    companion object {
        val JSON_SERVER = 0
        val BINARY_SERVER = 1
        var okhttpJsonBuilder: OkHttpClient.Builder? = null
        var okhttpBinaryBuilder: OkHttpClient.Builder? = null
        var retrofitJsonBuilder: Retrofit.Builder? = null
        var retrofitBinaryBuilder: Retrofit.Builder? = null
    }
}
