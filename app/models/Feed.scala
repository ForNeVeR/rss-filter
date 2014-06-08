package models

import java.net.URL
import play.api.Configuration
import scala.util.matching.Regex

case class Feed(name: String, url: URL, titleFilter: Regex)

object Feed {
  def apply(config: Configuration): Feed = {
    val feed = for {
      name <- config.getString("name")
      url <- config.getString("url")
      titleFilter <- config.getString("titlefilter")
    } yield Feed(name, new URL(url), new Regex(titleFilter))
    val Some(result) = feed
    result
  }
}
