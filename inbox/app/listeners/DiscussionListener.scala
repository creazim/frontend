package listeners

import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSAsyncClient
import common.{Message, JsonQueueWorker, JsonMessageQueue}
import conf.Configuration
import play.api.libs.json.Json

import scala.concurrent.Future

object DiscussionNotification {
  implicit val jsonReads = Json.reads[DiscussionNotification]
}

case class DiscussionNotification(
  comment_id: Long,
  body: String
)

object DiscussionListener extends JsonQueueWorker[DiscussionNotification] {
  override val queue: JsonMessageQueue[DiscussionNotification] = JsonMessageQueue[DiscussionNotification](
    new AmazonSNSAsyncClient().withRegion(Regions.EU_WEST_1),
    Configuration.inbox.discussionQueue.getOrElse("")
  )

  override def process(message: Message[DiscussionNotification]): Future[Unit] = {
    val id = message.get.comment_id

    // TODO process comment

    ???
  }
}