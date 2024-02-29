package com.lockboxth.lockboxkiosk.http

import com.lockboxth.lockboxkiosk.helpers.Contextor
import com.lockboxth.lockboxkiosk.helpers.MyPrefs
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by layer on 8/25/2017.
 */
class NetService private constructor() {

    companion object {

//        const val baseUrl = "https://api.lockbox-th.com/api/"
//        const val serviceToken = "f2c4a142b3fe39d3fb5a428250e8b2008228e297"

        const val baseUrl = "https://api-dev-staging.lockbox-th.com/api/"
        const val serviceToken = "6e03bc8215098bfd56b17d034f4f9fb854d1a1ad"

        private var instance: Retrofit? = null
        private val myPrefs: MyPrefs by lazy { MyPrefs(Contextor.getInstance().context) }

        @JvmStatic
        fun getRetrofit(): Retrofit {
            if (instance == null) {
                val interceptor = HttpLoggingInterceptor()
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                val client = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .apply {
                        networkInterceptors().add(Interceptor { chain ->
                            val original = chain.request()
                            val request = original.newBuilder()
//                            if (myPrefs.userToken != null && !myPrefs.userToken!!.token.isNullOrEmpty()) {
                            request.addHeader(
                                "Authorization",
                                "Token $serviceToken"
                            )
//                            }
                            chain.proceed(
                                request.method(original.method, original.body).build()
                            )
                        })
                        addInterceptor(interceptor)
                        addInterceptor(UnauthorizedInterceptor())
                    }
                instance = Retrofit.Builder().baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(createGsonConverter())
                    .client(client.build())
                    .build()
            }
            return instance!!
        }

        private fun createGsonConverter(): GsonConverterFactory {
            val builder = GsonBuilder().serializeNulls()
            builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            return GsonConverterFactory.create(builder.create())
        }
    }
}