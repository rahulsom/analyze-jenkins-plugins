package com.github.rahulsom.ajp

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.net.URL
import java.util.*
import javax.inject.Inject

class DownloadManager @Inject constructor(private val config: AppConfig) {
  
  init {
    File("${config.rootDir}/hpis").mkdirs()
  }
  
  fun download(plugin: JenkinsPlugin): File {
    var needDownload = true
    val pluginFile = File("${config.rootDir}/hpis/${plugin.getFilename()}")
    if (pluginFile.exists()) {
      val sha256 = DigestUtils.sha256(pluginFile.readBytes())
      val base64 = Base64.getEncoder().encodeToString(sha256)
      if (base64 == plugin.sha256) {
        needDownload = false
      }
    }
    if (needDownload) {
      log("Downloading ${plugin.name} ${plugin.version} from ${plugin.url}")
      IOUtils.copy(URL(plugin.url).openStream(), pluginFile.outputStream())
    }
    return pluginFile
  }
}
