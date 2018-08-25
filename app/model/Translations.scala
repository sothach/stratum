package model

import model.RequestType.RequestType
import model.SpeechAction.{SpeechAction, Value}
import model.SpeechFormat.SpeechFormat
import model.Voice.Voice
import play.api.mvc.QueryStringBindable

object RequestType extends Enumeration {
  type RequestType = Value
  val TEXT = Value("text")
  val SPEECH = Value("speech")
  val FAIL = Value("fail")
}
case class TranslationRequest(text: String, language: String, requestType: RequestType)
case class TranslationResponse(text: String, language: String, requestType: RequestType)

object SpeechAction extends Enumeration {
  type SpeechAction = Value
  val CONVERT = Value("convert")
  val FAIL = Value("fail")
  implicit object speechActionQueryStringBinder
    extends QueryStringBindable.Parsing[SpeechAction.SpeechAction](
      withName,
      _.toString,
      (k: String, e: Exception) => "Cannot parse %s as SpeechAction: %s".format(k, e.getMessage)
    )
}
object Voice extends Enumeration {
  type Voice = Value
  val ENGLISH_F = Value("usenglishfemale")
}
object SpeechFormat extends Enumeration {
  type SpeechFormat = Value
  val MP3 = Value("mp3")
}
case class SpeechRequest(text: String, voice: Voice, action: SpeechAction, format: Option[SpeechFormat])

