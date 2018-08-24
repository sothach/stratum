package controllers

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import javax.inject.{Inject, Singleton}
import model.TranslationRequest
import play.api.Logger
import play.api.libs.json.{JsError, Json}
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

  private val expectAuth = "Basic dGVzdDpzZWNyZXQ=" // test:secret
  logger.info("ApiController started")

  def translate = Action.async(parse.json) { implicit request =>
    request.headers.get("Authorization") match {
      case Some(basicAuth) if basicAuth == expectAuth =>
        request.body.validate[TranslationRequest].map {
          msg: TranslationRequest =>
            logger.debug(s"received request: $msg")
            translationService.translate(msg) map {
              case Some(response) =>
                Ok(Json.toJson(response))
              case None =>
                NotFound
            }
        }.recoverTotal { e =>
          Future.successful(BadRequest(s"Detected error: ${JsError.toJson(e)}"))
        }
      case _ =>
        logger.warn(s"Missing or invalid Auth in $request")
        Future.successful(Unauthorized("Missing or invalid Auth"))
    }
  }

}