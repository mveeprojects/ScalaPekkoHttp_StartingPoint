package route

import org.apache.pekko.NotUsed
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Directives.{authenticateBasic, complete, concat, get, path, pathSingleSlash}
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.directives.Credentials
import org.apache.pekko.stream.scaladsl.Source
import com.typesafe.scalalogging.LazyLogging
import config.CustomMetrics._
import config.OTELMetrics.randomDelayCounter
import model.Tweet
import model.TweetJsonProtocol._
import service.TweetRepo.getTweets

import scala.concurrent.ExecutionContext

trait AppRoutes extends LazyLogging {
  implicit val system: ActorSystem        = ActorSystem("my-mega-actor-system")
  implicit val executor: ExecutionContext = system.dispatcher

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()

  private def basicAuthenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p @ Credentials.Provided(id) if p.verify("superpassword") =>
        logger.info("auth succeeded")
        Some(id)
      case _ =>
        logger.info("auth failed")
        None
    }

  def route: Route = concat(
    path("tweets") {
      tweetsEndpointCounter.increment
      val tweets: Source[Tweet, NotUsed] = getTweets
      complete(tweets)
    },
    get {
      pathSingleSlash {
        helloWorldEndpointCounter.increment
        complete(StatusCodes.OK -> "Hello, World!")
      }
    },
    get {
      path("authpath") {
        authPathEndpointCounter.increment
        authenticateBasic(realm = "secure auth path", basicAuthenticator) { userName =>
          complete(s"The user is $userName")
        }
      }
    },
    get {
      path("randomdelay") {
        randomDelayCounter.add(1)
        randomDelayEndpointCounter.increment
        def randomDurationToWait: Long = Math.floor(Math.random() * 10 * 100).toLong
        Thread.sleep(randomDurationToWait)
        complete(StatusCodes.OK -> "Waited for a bit then responded")
      }
    }
  )
}
