package com.github.rahulsom.ajp

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.StringReader
import java.util.*
import java.util.stream.Stream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class PluginAnalyzer {
  fun analyze(plugin: JenkinsPlugin, hpiFile: File): Stream<MutableList<Manifest>>? {
    val hpiZip = ZipFile(hpiFile)
    return hpiZip.entries().toList().stream()
      .filter { it.name.endsWith(".jar") }
      .map { hpiFileEntry ->
        val jarBytes = ByteArrayOutputStream()
        IOUtils.copy(hpiZip.getInputStream(hpiFileEntry), jarBytes)
        val jarFile = ZipInputStream(ByteArrayInputStream(jarBytes.toByteArray()))
        val jarSha1 = DigestUtils.sha1Hex(ByteArrayInputStream(jarBytes.toByteArray()))

        val manifests: MutableList<Manifest> = mutableListOf()
        while (true) {
          val jarEntry = jarFile.nextEntry ?: break
          if (jarEntry.name.endsWith("pom.properties")) {
            val fileContent = readFile(jarFile)
            val pomProps = Properties()
            pomProps.load(StringReader(fileContent))
            manifests.add(buildManifest(plugin, hpiFileEntry, jarSha1, pomProps, GAVSource.POM))
          }
        }
        if (manifests.isEmpty()) {
          manifests.add(buildManifest(plugin, hpiFileEntry, jarSha1, null, GAVSource.POM))
        }
        manifests
      }

  }

  private fun buildManifest(plugin: JenkinsPlugin, hpiFileEntry: ZipEntry, jarSha1: String, pomProps: Properties?, gavSource: GAVSource) =
    Manifest(
      plugin.name,
      plugin.version,
      hpiFileEntry.name,
      jarSha1,
      pomProps?.getProperty("groupId"),
      pomProps?.getProperty("artifactId"),
      pomProps?.getProperty("version"),
      gavSource
    )

  private fun readFile(jarFile: ZipInputStream): String {
    val buffer = ByteArray(2048)
    val fos = ByteArrayOutputStream()
    val bos = BufferedOutputStream(fos, buffer.size)
    while (true) {
      val read = jarFile.read(buffer, 0, buffer.size)
      if (read == -1) {
        break
      }
      bos.write(buffer, 0, read)
    }
    bos.flush()
    return String(fos.toByteArray(), Charsets.UTF_8)
  }

}
