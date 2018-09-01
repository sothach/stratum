package translations

import java.util.concurrent.Executors

import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import javax.inject.{Inject, Singleton}
import model._
import play.api.Environment
import play.api.i18n.MessagesApi

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TranslationService @Inject()(messagesApi: MessagesApi,
                                   environment: Environment) {

  private final val maximumRunningJobs = Runtime.getRuntime.availableProcessors
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(maximumRunningJobs))

  def translate(request: TranslationRequest): Future[Option[TranslationResponse]] = Future {
    implicit val lang = request.language
    val key = request.text.hashCode.toString
    request.requestType match {
      case RequestType.TEXT if messagesApi.isDefinedAt(key) =>
        Some(TranslationResponse(messagesApi(key), request.language, request.requestType))
      case RequestType.FAIL =>
        throw new RuntimeException("FAIL requested")
      case _ =>
        None
    }
  }

  private val testAudioPath = "public/audio/Hello.mp3"

  def speechify(request: SpeechRequest): Future[Option[Source[ByteString, _]]] = Future {
    (request.action, new java.io.File(testAudioPath)) match {
      case (SpeechAction.CONVERT, file) if file.exists =>
        Some(StreamConverters.fromInputStream(() =>
          environment.classLoader.getResourceAsStream(testAudioPath)))
      case (SpeechAction.FAIL, _) =>
        throw new RuntimeException("FAIL requested")
      case _ =>
        None
    }
  }

}