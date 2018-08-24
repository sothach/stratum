package model

import model.RequestType.RequestType

object RequestType extends Enumeration {
  type RequestType = Value
  val TEXT = Value("text")
  val SPEECH = Value("speech")
}
case class TranslationRequest(text: String, language: String, requestType: RequestType)
case class TranslationResponse(text: String, language: String)
