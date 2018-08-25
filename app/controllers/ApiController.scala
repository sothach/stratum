package controllers

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import javax.inject.{Inject, Singleton}
import model._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import translations.TranslationService

import scala.concurrent.Future

@Singleton
class ApiController @Inject()(implicit system: ActorSystem,
                              translationService: TranslationService,
                              components: ControllerComponents) extends AbstractController(components) {
  import conversions.Converters._

  implicit val ec = system.dispatcher
  val logger = Logger(this.getClass)
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  private val expectKey = "eabb12404d141ed6e8ee2193688178cb"
  logger.info("ApiController started")

  def index = Action.async {
    Future.successful(Ok)
  }

  def translate(apiKey: String) = Action.async(parse.json) {
    implicit request =>
      apiKey match {
        case key if key == expectKey =>
          request.body.validate[TranslationRequest].fold(
            errors => {
              val errorResponse = serializeJsErrors(errors)
              logger.warn(s"Invalid Json in request: $errorResponse")
              Future.successful(BadRequest(Json.toJson(errorResponse)))
            }, translationRequest => {
              translationService.translate(translationRequest) map {
                case Some(response) =>
                  Ok(Json.toJson(response))
                case None =>
                  NotFound
              }
            }) recover { case e =>
              InternalServerError(Json.toJson(e.getMessage))
            }
        case _ =>
          logger.warn(s"Missing or invalid Auth in $request")
          Future.successful(Unauthorized("Missing or invalid Auth"))
      }
  }

  def speech(apiKey: String, action: String, text: String, voice: String, format: Option[String]) = Action.async {
    implicit request =>
      apiKey match {
        case key if key == expectKey =>
          val request = SpeechRequest(text, Voice.withName(voice),
            SpeechAction.withName(action), format.map(f => SpeechFormat.withName(f)))
          translationService.speech(request) map { byteArray =>
            Ok(byteArray).as(s"""audio/${request.format.getOrElse("mp3")}""")
          }
        case _ =>
          logger.warn(s"Missing or invalid Auth in $request")
          Future.successful(Unauthorized("Missing or invalid Auth"))
      }
  }


}