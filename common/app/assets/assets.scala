package common.Assets

import java.net.URL

import common.{Logging, RelativePathEscaper}
import conf.Configuration
import org.apache.commons.io.IOUtils
import play.api.libs.json.{JsObject, JsString, Json}
import play.api.{Mode, Play}

import scala.collection.concurrent.{Map => ConcurrentMap, TrieMap}

case class Asset(path: String) {
  val asModulePath = path.replace(".js", "")
  lazy val md5Key = path.split('/').dropRight(1).last

  override def toString = path
}

class AssetMap(base: String, assetMap: String) {
  def apply(path: String): Asset = {

    // Avoid memoizing the asset map in Dev.
    if (Play.current.mode == Mode.Dev) {
      assets()(path)
    } else {
      memoizedAssets(path)
    }
  }

  private def assets(): Map[String, Asset] = {

    // Use the grunt-generated asset map in Dev.
    val json: String = if (Play.current.mode == Mode.Dev) {
      val assetMapUri = new java.io.File(s"static/hash/" + assetMap).toURI
      IOUtils.toString(assetMapUri)
    } else {
      val url = AssetFinder(assetMap)
      IOUtils.toString(url)
    }
    val js: JsObject = Json.parse(json).asInstanceOf[JsObject]

    val paths = js.fields.toMap mapValues { _.asInstanceOf[JsString].value }

    paths mapValues { path => Asset(base + path) }
  }

  private lazy val memoizedAssets = assets()
}

class Assets(base: String, assetMap: String = "assets/assets.map") extends Logging {
  val lookup = new AssetMap(base, assetMap)
  def apply(path: String): Asset = lookup(path)

  object inlineSvg {

    private val memoizedSvg: ConcurrentMap[String, String] = TrieMap()

    def apply(path: String): String = {

      def loadFromDisk = {
        val url = AssetFinder(s"assets/inline-svgs/$path")
        IOUtils.toString(url)
      }

      memoizedSvg.getOrElseUpdate(path, loadFromDisk)
    }
  }

  object css {

    private val memoizedCss: ConcurrentMap[java.net.URL, String] = TrieMap()

    def projectCss(projectOverride: Option[String] = None) = project(projectOverride.getOrElse(Configuration.environment.projectName))
    def head(projectOverride: Option[String] = None) = css(projectOverride.getOrElse(Configuration.environment.projectName))
    def headOldIE(projectOverride: Option[String] = None) = cssOldIE(projectOverride.getOrElse(Configuration.environment.projectName))
    def headIE9(projectOverride: Option[String] = None) = cssIE9(projectOverride.getOrElse(Configuration.environment.projectName))


    private def css(project: String): String = {

      val suffix = project match {
        case "footballSnaps" => "footballSnaps.css"
        case "facia" => "facia.css"
        case "identity" => "identity.css"
        case "football" => "football.css"
        case "index" => "index.css"
        case "story-package" => "story-package.css"
        case "rich-links" => "rich-links.css"
        case _ => "content.css"
      }
      val url = AssetFinder(s"assets/head.$suffix")

      // Reload head css on every access in DEV
      if (Play.current.mode == Mode.Dev) {
        memoizedCss.remove(url)
      }

      memoizedCss.getOrElseUpdate(url, {
        IOUtils.toString(url)
      })
    }

    private def project(project: String): String = {
      project match {
        case "facia" => "stylesheets/facia.css"
        case _ => "stylesheets/global.css"
      }
    }

    private def cssOldIE(project: String): String = {
      project match {
        case "facia" => "stylesheets/old-ie.head.facia.css"
        case "identity" => "stylesheets/old-ie.head.identity.css"
        case "football" => "stylesheets/old-ie.head.football.css"
        case "index" => "stylesheets/old-ie.head.index.css"
        case _ => "stylesheets/old-ie.head.content.css"
      }
    }
    private def cssIE9(project: String): String = {
      project match {
        case "facia" => "stylesheets/ie9.head.facia.css"
        case "identity" => "stylesheets/ie9.head.identity.css"
        case "football" => "stylesheets/ie9.head.football.css"
        case "index" => "stylesheets/ie9.head.index.css"
        case _ => "stylesheets/ie9.head.content.css"
      }
    }
  }

  object js {

     private def inlineJs(path: String): String = IOUtils.toString(AssetFinder(path))

     val curl: String = RelativePathEscaper.escapeLeadingDotPaths(inlineJs("assets/curl-domReady.js"))

     val es6ModuleLoader: String = inlineJs("assets/es6-module-loader.src.js")

     val systemJs: String = inlineJs("assets/system.src.js")

     val systemJsAppConfig: String = inlineJs("assets/systemjs-config.js")

     val systemJsNormalize: String = inlineJs("assets/systemjs-normalize.js")

     val systemJsBundleConfig: String = inlineJs("assets/systemjs-bundle-config.js")
  }
}

object AssetFinder {
  def apply(assetPath: String): URL = {
    Option(Play.classloader(Play.current).getResource(assetPath)).getOrElse {
      throw AssetNotFoundException(assetPath)
    }
  }
}

case class AssetNotFoundException(assetPath: String) extends Exception {
  override val getMessage: String =
    s"Cannot find asset $assetPath. You probably need to run 'grunt compile'."
}
