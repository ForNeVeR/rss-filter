package models

import play.api.Configuration

case class Feed(name: String, url: String, titleFilter: String)

object Feed {
  def apply(config: Configuration): Feed = {
    val feed = for {
      name <- config.getString("name")
      url <- config.getString("url")
      titleFilter <- config.getString("titlefilter")
    } yield Feed(name, url, titleFilter)
    val Some(result) = feed
    result
  }
}
