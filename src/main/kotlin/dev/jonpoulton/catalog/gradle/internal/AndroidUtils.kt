package dev.jonpoulton.catalog.gradle.internal

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import javax.xml.parsers.DocumentBuilderFactory

internal fun AndroidSourceSet.readManifestPackageName(): String? {
  val manifestFile = (manifest as DefaultAndroidSourceFile).srcFile
  return if (manifestFile.exists()) {
    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = docBuilder.parse(manifestFile)
    val manifestRoot = doc.getElementsByTagName("manifest").item(0)
    manifestRoot.attributes.getNamedItem("package")?.nodeValue
  } else {
    null
  }
}
