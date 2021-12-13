package com.github.rahulsom.ajp

import com.google.inject.AbstractModule
import com.google.inject.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Qualifier

data class SearchResponse(val response: Response)
data class Response(val docs: List<Doc>, val numFound: Int)
data class Doc(val g: String, val a: String, val v: String, val p: String)

interface MavenCentralApi {
  @GET("/solrsearch/select")
  fun search(@Query("q") query: String): Call<SearchResponse>
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class MavenCentral

class MavenCentralModule : AbstractModule() {
  override fun configure() {}

  @Provides @MavenCentral fun cache() =
    buildCache(MavenCentralApi::class.java)
  @Provides @MavenCentral fun httpLoggingInterceptor() =
    buildHttpLoggingInterceptor(MavenCentralApi::class.java, HttpLoggingInterceptor.Level.BODY)
  @Provides @MavenCentral fun client(@MavenCentral cache: Cache, @MavenCentral interceptor: HttpLoggingInterceptor) =
    buildClient(cache, interceptor, 60 * 60 * 24 * 7)
  @Provides fun api(@MavenCentral client: OkHttpClient) =
    buildTypedClient("https://search.maven.org", MavenCentralApi::class.java, client)
}
