package conversions

import model.RequestType.RequestType
import model.{RequestType, TranslationRequest, TranslationResponse}
import play.api.libs.json._

object Converters {

  implicit object eventTypeFormat extends Format[RequestType] {
    def reads(json: JsValue) = JsSuccess(RequestType.withName(json.as[String].toLowerCase))
    def writes(status: RequestType) =  JsString(status.toString.toLowerCase)
  }

  implicit val translationRequestFormat = Json.format[TranslationRequest]
  implicit val translationResponseFormat = Json.format[TranslationResponse]

}
