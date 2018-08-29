package controllers

import model._
import play.api.mvc.QueryStringBindable

object Binders {

  implicit def apiKeyBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[ApiKey] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ApiKey]] = {
      val result = stringBinder.bind("apiKey", params) match {
        case Some(v) if v.isRight =>
          v.map(ApiKey(_))
        case _ =>
          Left("Unable to bind ApKey")
      }
      Some(result)
    }

    override def unbind(key: String, apiKey: ApiKey): String = {
      stringBinder.unbind("apiKey", apiKey.value)
    }
  }

  implicit def speechRequestBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[SpeechRequest] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SpeechRequest]] = {
      val action = stringBinder.bind("action", params)
      val text = stringBinder.bind("text", params)
      val voice = stringBinder.bind("voice", params)
      val format = stringBinder.bind("format", params)
      val result = (action, text, voice, format) match {
        case (Some(Right(a)), Some(Right(t)), Some(Right(v)), f: Option[Either[String, String]]) =>
          val fmt = f.flatMap(zzz => zzz.toOption.map(yyy => SpeechFormat.findOrDefault(yyy)))
          Right(SpeechRequest(t, Voice.findOrDefault(v), SpeechAction.findOrDefault(a), fmt))
        case _ =>
          Left("Unable to bind Speech request (1)")
      }
      Some(result)
    }

    override def unbind(key: String, request: SpeechRequest): String = {
      val ubr = stringBinder.unbind("action", request.action.toString) + "&" +
        stringBinder.unbind("text", request.text) + "&" +
        stringBinder.unbind("voice", request.voice.toString)
      request.format match {
        case Some(f) =>
          ubr + "&" + stringBinder.unbind("format", f.toString)
        case None =>
          ubr
      }
    }
  }

}
