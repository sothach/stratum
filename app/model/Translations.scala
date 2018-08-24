package model

import model.RequestType.RequestType

object RequestType extends Enumeration {
  type RequestType = Value
  val TEXT = Value("text")
  val SPEECH = Value("speech")
  val FAIL = Value("fail")
}
case class TranslationRequest(text: String, language: String, requestType: RequestType)
case class TranslationResponse(text: String, language: String, requestType: RequestType)
