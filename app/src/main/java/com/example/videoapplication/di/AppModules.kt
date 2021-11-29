package com.example.videoapplication.di

import com.example.videoapplication.BuildConfig
import com.example.videoapplication.network.ApiService
import com.example.videoapplication.network.TrustAllVerifier
import com.example.videoapplication.repository.MainRepository
import com.example.videoapplication.ui.main.PageViewModel
import com.example.videoapplication.ui.model.SharedViewModel
import com.example.videoapplication.util.AppExecutors
import com.google.gson.Gson
import kz.laccent.network.UrlProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single { AppExecutors() }
    single { Gson() }
}

val networkModule = module {
    single {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .hostnameVerifier(TrustAllVerifier())
        clientBuilder.build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(UrlProvider.testUrl)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

val mainModule = module {
    single { MainRepository(get()) }
    viewModel { PageViewModel(get(), get()) }
    viewModel { SharedViewModel(get()) }
}



