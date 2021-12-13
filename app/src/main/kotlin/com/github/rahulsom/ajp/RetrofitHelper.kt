package com.github.rahulsom.ajp

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File

fun <T> buildTypedClient(baseUrl: String, clazz: Class<T>, client: OkHttpClient) = Retrofit.Builder()
  .client(client)
  .baseUrl(baseUrl)
  .addConverterFactory(ScalarsConverterFactory.create())
  .addConverterFactory(GsonConverterFactory.create())
  .build()
  .create(clazz)

fun <T> buildHttpLoggingInterceptor(clazz: Class<T>, level: HttpLoggingInterceptor.Level) =
  HttpLoggingInterceptor { message -> log(message, clazz) }.also { it.level = level }

private fun <T> log(message: String, clazz: Class<T>) {
  when {
    message.startsWith("<--") || message.startsWith("-->") -> LoggerFactory.getLogger(clazz).info(message)
    else -> LoggerFactory.getLogger(clazz).debug(message)
  }
}

fun buildClient(cache: Cache, httpLoggingInterceptor: HttpLoggingInterceptor, cacheAge: Int) =
  OkHttpClient.Builder()
    .addInterceptor(httpLoggingInterceptor)
    .addInterceptor { chain ->
      chain.proceed(chain.request().newBuilder()
        .header("Cache-Control", "public, max-age=" + cacheAge)
        .build())
    }
    .cache(cache)
    .build()

fun <T> buildCache(clazz: Class<T>) =
  Cache(File("${System.getProperty("user.home")}/jenkins-plugins/cache/${clazz.simpleName}"), 20000)
