package com.github.rahulsom.ajp

import com.google.gson.Gson
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions

class AppConfig(
  val rootDir: String,
)

class ConfigModule : AbstractModule() {
  override fun configure() {}

  @Provides
  fun appConfig(): AppConfig {
    val config = ConfigFactory.load()
    val default = ConfigFactory.parseString(Gson().toJson(defaultConfig()))
    val json = config.withFallback(default).root().render(ConfigRenderOptions.concise())
    return Gson().fromJson(json, AppConfig::class.java)
  }

  private fun defaultConfig() = AppConfig(
    "${System.getProperty("user.home")}/jenkins-plugins"
  )
}
