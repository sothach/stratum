package translations

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Source, StreamConverters}
import akka.util.ByteString
import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}
import model.{RequestType, SpeechRequest, TranslationRequest, TranslationResponse}
import play.api.{Environment, Logger, Play}

import scala.concurrent.Future

@Singleton
class TranslationService @Inject()(implicit val system: ActorSystem,
                                   environment: Environment,
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

  def speechify(request: SpeechRequest): Option[Source[ByteString, _]] = {
    val file = new java.io.File("public/audio/Hello.mp3")
    if(request.text != "fail" && file.exists()) {
      val resourceStream = environment.classLoader.getResourceAsStream("public/audio/Hello.mp3")
      Some(StreamConverters.fromInputStream(() => resourceStream))
    } else {
      None
    }
  }

}