package com.github.rahulsom.ajp

import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.supercsv.cellprocessor.Optional
import org.supercsv.io.CsvBeanWriter
import org.supercsv.prefs.CsvPreference
import java.io.File
import javax.inject.Inject

data class Manifest(
  val pluginName: String,
  val pluginVersion: String,
  val jarName: String,
  val jarSha1: String,
  val jarGroupId: String?,
  val jarArtifactId: String?,
  val jarVersion: String?,
  val jarGAVSource: GAVSource?
)
enum class GAVSource {
  POM, SHA1
}

class ReportWriter @Inject constructor(private val csvBeanWriter: CsvBeanWriter) {
  private val declaredFields = Manifest::class.java.declaredFields
  private val header = declaredFields.map { it.name }.toTypedArray()
  private val processors = declaredFields.map { Optional() }.toTypedArray()

  init {
    csvBeanWriter.writeHeader(*header)
  }
  
  fun write(manifest: Manifest) {
    csvBeanWriter.write(manifest, header, processors)
  }
  
  fun close() {
    csvBeanWriter.flush()
    csvBeanWriter.close()
  }
}

class ReportModule : AbstractModule() {
  @Provides
  fun reportWriter(config: AppConfig) =
    CsvBeanWriter(File("${config.rootDir}/plugin-jars.csv").writer(), CsvPreference.STANDARD_PREFERENCE)

}
