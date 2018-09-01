package controllers

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import model._
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import translations.TranslationService

import scala.concurrent.Future
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject()(implicit system: ActorSystem,
        translationService: TranslationService,
        components: ControllerComponents) extends AbstractController(components) {
  import conversions.Converters._

  implicit val ec = system.dispatcher
  val logger = Logger(this.getClass)

  private val expectKey = ApiKey("eabb12404d141ed6e8ee2193688178cb")
  logger.info("ApiController started")

  def index = Action.async {
    Future.successful(Ok)
  }

  def translate(apiKey: ApiKey): Action[JsValue] = Action.async(parse.json) { request =>
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

  def speech(apiKey: ApiKey, speechRequest: SpeechRequest) = Action.async { request =>
      apiKey match {
        case key if key == expectKey =>
          translationService.speechify(speechRequest) map {
            case Some(source) =>
              Ok.chunked(source).as(s"""audio/${speechRequest.format.getOrElse("audio/wav")}""")
            case None =>
              NotFound
          } recover { case e =>
            InternalServerError(Json.toJson(e.getMessage))
          }
        case _ =>
          logger.warn(s"Missing or invalid API key in $request")
          Future.successful(Unauthorized("Missing or invalid API key"))
      }
  }

}