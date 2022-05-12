package com.irinamihaila.quizzapp.networking

import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor


fun provideNetworkInterceptor() = run {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
    val client: OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(logging)
        .build()
    client
}