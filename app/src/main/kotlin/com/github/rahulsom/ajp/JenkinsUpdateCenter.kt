package com.github.rahulsom.ajp

import com.google.gson.Gson
import com.google.inject.AbstractModule
import com.google.inject.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Qualifier

data class JenkinsPlugin(
  val name: String,
  val version: String,
  val url: String,
  val scm: String,
  val sha256: String,
  val releaseTimestamp: String,
) {
  fun getFilename() = "${name}-${version}.${url.split(".").last()}"
}

data class UpdateCenterJson(
  val plugins: Map<String, JenkinsPlugin>
)

interface JenkinsUpdateCenterApi {
  @GET("/current/update-center.json")
  fun updateCenter(): Call<String>
}

class PluginProvider @Inject constructor(private val api: JenkinsUpdateCenterApi) {
  fun getPlugins() = api.updateCenter().execute().body()
    ?.replace(Regex("^updateCenter\\.post\\("), "")
    ?.replace(Regex("\\);?$"), "").let {
      Gson().fromJson(it, UpdateCenterJson::class.java).plugins.values
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class Jenkins

class JenkinsModule : AbstractModule() {
  override fun configure() {

  }

  @Provides
  @Jenkins
  fun cache() =
    buildCache(JenkinsUpdateCenterApi::class.java)

  @Provides
  @Jenkins
  fun httpLoggingInterceptor() =
    buildHttpLoggingInterceptor(JenkinsUpdateCenterApi::class.java, HttpLoggingInterceptor.Level.HEADERS)

  @Provides
  @Jenkins
  fun client(@Jenkins cache: Cache, @Jenkins interceptor: HttpLoggingInterceptor) =
    buildClient(cache, interceptor, 60 * 60)

  @Provides
  fun api(@Jenkins client: OkHttpClient) =
    buildTypedClient("https://updates.jenkins.io/", JenkinsUpdateCenterApi::class.java, client)
}
