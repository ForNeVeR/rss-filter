package controllers

import play.api._
import play.api.mvc._
import models.Feed
import com.rometools.fetcher.impl.{HttpURLFeedFetcher, HashMapFeedInfoCache}
import java.net.URL
import play.twirl.api.Xml

object Application extends Controller {

  val Some(feeds) = Play.current.configuration.getConfigSeq("rss").map { list =>
    (list map { feedConfig =>
      val feed = Feed(feedConfig)
      (feed.name, feed)
    }).toMap
  }

  val feedInfoCache = HashMapFeedInfoCache.getInstance()
  val fetcher = new HttpURLFeedFetcher(feedInfoCache)

  def feed(feedName: String) = Action {
    feeds.get(feedName) match {
      case Some(feed) =>
        val url = new URL(feed.url)
        val content = fetcher.retrieveFeed(url)
        // TODO: Filter the content.
        // TODO: Serve the content as proper XML.
        Ok(Xml(content.toString))
      case None => NotFound("Feed not found.")
    }
  }

}