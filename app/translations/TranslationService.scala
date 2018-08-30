package translations

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import javax.inject.{Inject, Singleton}
import model.{RequestType, SpeechRequest, TranslationRequest, TranslationResponse}
import play.api.i18n.MessagesApi
import play.api.{Environment, Logger}

import scala.concurrent.Future

@Singleton
class TranslationService @Inject()(implicit val system: ActorSystem,
                                   messagesApi: MessagesApi,
                                   environment: Environment) {

  implicit val ec = system.dispatcher
  val logger = Logger(this.getClass)

  def translate(request: TranslationRequest): Future[Option[TranslationResponse]] = Future {
    implicit val lang = request.language
    val key = request.text.hashCode.toString
    request.requestType match {
      case RequestType.TEXT if messagesApi.isDefinedAt(key) =>
        val translation = messagesApi(key)
        Some(TranslationResponse(translation, request.language, request.requestType))
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