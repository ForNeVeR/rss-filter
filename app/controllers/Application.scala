package controllers

import com.rometools.fetcher.impl.{HashMapFeedInfoCache, HttpURLFeedFetcher}
import com.rometools.rome.io.SyndFeedOutput
import models.Feed
import play.api._
import play.api.mvc._
import play.twirl.api.Xml
import java.net.URL

object Application extends Controller {

  val Some(feeds) = Play.current.configuration.getConfigSeq("rss").map { list =>
    (list map { feedConfig =>
      val feed = Feed(feedConfig)
      (feed.name, feed)
    }).toMap
  }

  val feedInfoCache = HashMapFeedInfoCache.getInstance()
  val fetcher = new HttpURLFeedFetcher(feedInfoCache)
  val writer = new SyndFeedOutput()

  def feed(feedName: String) = Action {
    feeds.get(feedName) match {
      case Some(feed) =>
        Logger.info(s"Processing feed $feedName")

        val url = new URL(feed.url)
        val content = fetcher.retrieveFeed(url)
        val output = writer.outputString(content)
        
        // TODO: Filter the content.
        Ok(Xml(output))
      case None => NotFound("Feed not found.")
    }
  }

}