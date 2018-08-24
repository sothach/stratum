package translations

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import model.{RequestType, TranslationRequest, TranslationResponse}
import play.api.Logger

import scala.concurrent.Future

@Singleton
class TranslationService @Inject()(implicit val system: ActorSystem,
                                   config: Config) {

  private val translations = Map(
    (-841391678, "de") -> """Lassen Sie Ihr Gepäck nicht unbeaufsichtigt.
                            |Unbeaufsichtigtes Gepäck im Terminal wird vom
                            |Sicherheitsdienst entfernt und kann zerstört werden"""
  )
  implicit val ec = system.dispatcher
  val logger = Logger(this.getClass)

  def translate(request: TranslationRequest): Future[Option[TranslationResponse]] = Future {
    request.requestType match {
      case RequestType.TEXT =>
        translations.get((request.text.hashCode, request.language)) map { result =>
          TranslationResponse(result, request.language, request.requestType)
        }
      case RequestType.FAIL =>
        throw new RuntimeException("FAIL requested")
      case _ =>
        None
    }
  }

}