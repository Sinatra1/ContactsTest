package ru.shumilov.vladislav.contactstest.modules.login.injection

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open abstract class BaseModule<Api> {

    companion object {
        const val API_URL = "https://raw.githubusercontent.com/SkbkonturMobile/mobile-test-droid/master/json/"
    }

    fun provideApi(retrofit: Retrofit, apiClassName: Class<Api>): Api {

        return retrofit.create(apiClassName)

    }

    fun provideApiRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {

        val retrofit = Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .callFactory(okHttpClient)
                .client(okHttpClient)
                .build()

        return retrofit

    }

}

