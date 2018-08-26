package model

import model.RequestType.RequestType
import model.SpeechAction.{SpeechAction, Value}
import model.SpeechFormat.SpeechFormat
import model.Voice.Voice
import play.api.mvc.QueryStringBindable

trait DefaultEnum extends Enumeration {
  def default: Value
  def findOrDefault(v: String) =
    this.values.find(_.toString == v.trim).getOrElse(default)
}

object RequestType extends DefaultEnum {
  type RequestType = Value
  override def default = FAIL
  val TEXT = Value("text")
  val SPEECH = Value("speech")
  val FAIL = Value("fail")
}
case class TranslationRequest(text: String, language: String, requestType: RequestType)
case class TranslationResponse(text: String, language: String, requestType: RequestType)

object SpeechAction extends DefaultEnum {
  type SpeechAction = Value
  override def default = FAIL
  val CONVERT = Value("convert")
  val FAIL = Value("fail")
  implicit object speechActionQueryStringBinder
    extends QueryStringBindable.Parsing[SpeechAction.SpeechAction](
      withName,
      _.toString,
      (k: String, e: Exception) => "Cannot parse %s as SpeechAction: %s".format(k, e.getMessage)
    )
}
object Voice extends DefaultEnum {
  type Voice = Value
  override def default = ENGLISHF
  val ENGLISHF = Value("usenglishfemale")
}
object SpeechFormat extends DefaultEnum {
  type SpeechFormat = Value
  override def default = MP3
  val MP3 = Value("mp3")
}
case class SpeechRequest(text: String, voice: Voice, action: SpeechAction, format: Option[SpeechFormat])

