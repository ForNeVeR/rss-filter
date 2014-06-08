package controllers

import play.api._
import play.api.mvc._
import models.Feed

object Application extends Controller {

  val Some(feeds) = Play.current.configuration.getConfigSeq("rss").map { list =>
    (list map { feedConfig =>
      val feed = Feed(feedConfig)
      (feed.name, feed)
    }).toMap
  }

  def feed(feedName: String) = Action {
    feeds.get(feedName) match {
      case Some(feed) => Ok("ok")
      case None => NotFound("not found")
    }
  }

}