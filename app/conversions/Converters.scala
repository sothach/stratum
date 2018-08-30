package conversions

import model.RequestType.RequestType
import model._
import play.api.i18n.Lang
import play.api.libs.json._

object Converters {

  implicit object requestTypeFormat extends Format[RequestType] {
    def reads(json: JsValue) = JsSuccess(RequestType.findOrDefault(json.as[String].toLowerCase))
    def writes(status: RequestType) =  JsString(status.toString.toLowerCase)
  }
  implicit object langFormat extends Format[Lang] {
    def reads(json: JsValue) = JsSuccess(Lang(json.as[String].toLowerCase))
    def writes(lang: Lang) =  JsString(lang.toString.toLowerCase)
  }

  implicit val translationRequestFormat = Json.format[TranslationRequest]
  implicit val translationResponseFormat = Json.format[TranslationResponse]

  val serializeJsErrors = (errors: Seq[(JsPath, Seq[JsonValidationError])]) => {
    val items = errors map { case (path, description) =>
      val message = description.headOption.map(_.messages.headOption.getOrElse("(unknown)")).getOrElse("(unknown)")
      s"""{"$message" : "$path"}"""
    }
    s"""{"jsonErrors" : [${items.mkString(",")}]}"""
  }
}
