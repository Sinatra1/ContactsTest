package ru.vshumilov.agroholdingapp.modules.login.injection

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers

open abstract class BaseModule<Api> {

    val API_URL = "https://raw.githubusercontent.com/SkbkonturMobile/mobile-test-droid/master/json";

    fun provideApi(retrofit: Retrofit, apiClassName: Class<Api>): Api {

        return retrofit.create(apiClassName)

    }

    fun provideApiRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {

        var retrofit = Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .callFactory(okHttpClient)
                .client(okHttpClient)
                .build()

        return retrofit

    }

}

