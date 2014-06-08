package controllers

import com.rometools.fetcher.impl.{HashMapFeedInfoCache, HttpURLFeedFetcher}
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedOutput
import models.Feed
import play.api._
import play.api.mvc._
import play.twirl.api.Xml
import scala.collection.JavaConversions._
import scala.util.matching.Regex

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
      case Some(feedInfo) =>
        Logger.info(s"Processing feed $feedName")

        Logger.info(s"Downloading feed ${feedInfo.url}")
        val feed = fetcher.retrieveFeed(feedInfo.url)

        Logger.info(s"Filtering feed $feedName")
        filterFeed(feed, feedInfo.titleFilter)

        Logger.info(s"Returning processed feed $feedName to client")
        val output = writer.outputString(feed)
        Ok(Xml(output))
      case None => NotFound("Feed not found.")
    }
  }

  private def filterFeed(feed: SyndFeed, titleFilter: Regex) {
    val entries = feed.getEntries
    Logger.info(s"Entries before filtering: ${entries.size}")

    val filtered = entries.filter(entry => titleFilter.findFirstIn(entry.getTitle).isDefined)

    Logger.info(s"Entries after filtering: ${filtered.size}")
    feed.setEntries(filtered)
  }

}