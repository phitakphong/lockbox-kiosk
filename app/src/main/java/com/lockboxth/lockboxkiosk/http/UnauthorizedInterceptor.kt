package com.lockboxth.lockboxkiosk.http

import androidx.annotation.NonNull
import okhttp3.Interceptor
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import java.io.IOException


internal class UnauthorizedInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        if (response.code == 401) {
            EventBus.getDefault().post("UNAUTH");
        }
        return response
    }
}