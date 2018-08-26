package conversions

import model.RequestType.RequestType
import model._
import play.api.libs.json._

object Converters {

  implicit object requestTypeFormat extends Format[RequestType] {
    def reads(json: JsValue) = JsSuccess(RequestType.withName(json.as[String].toLowerCase))
    def writes(status: RequestType) =  JsString(status.toString.toLowerCase)
  }

  implicit val translationRequestFormat = Json.format[TranslationRequest]
  implicit val translationResponseFormat = Json.format[TranslationResponse]

  val serializeJsErrors = (errors: Seq[(JsPath, Seq[JsonValidationError])]) => {
    val items = errors map { case (path, description) =>
      val message = description.head.messages.headOption.getOrElse("(unknown")
      s"""{"$message" : "$path"}"""
    }
    s"""{"jsonErrors" : [${items.mkString(",")}]}"""
  }
}
