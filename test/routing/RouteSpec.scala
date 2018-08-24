package routing

import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._

class RouteSpec extends PlaySpec with ScalaFutures with GuiceOneAppPerSuite {

  override def fakeApplication() = new GuiceApplicationBuilder()
    .loadConfig(Configuration(ConfigFactory.load("application.conf")))
    .configure(Map("play.filters.disabled" -> Seq("play.filters.csrf.CSRFFilter"),
      "play.filters.hosts.allowed" -> Seq("localhost")))
    .build()

  "GET /" should {
    "invoke the index" in {
      val request = FakeRequest("GET", "/")
        .withHeaders(FakeHeaders(Seq(
          "Host" -> "localhost",
          "Authorization" -> "Basic dGVzdDpzZWNyZXQ=",
          "Content-Type" -> "application/json")))

      route(app, request).map(status) mustBe Some(OK)
    }
  }

  "An invalid auth token in request posted /api/translate endpoint" should {
    "result in a Unauthorized (401) status" in {
      val request = FakeRequest("POST", "/api/translate")
        .withBody(validJsonRequest)
        .withHeaders(FakeHeaders(Seq(
          "Host" -> "localhost",
          "Authorization" -> "Basic __________=",
          "Content-Type" -> "application/json")))

      route(app, request).map(status) mustBe Some(UNAUTHORIZED)
    }
  }

  "An translation request posted to the /api/translate endpoint" should {
    "be routed successfully" in {

      val request = FakeRequest("POST", "/api/translate")
        .withBody(validJsonRequest)
        .withHeaders(FakeHeaders(Seq(
          "Host" -> "localhost",
          "Authorization" -> "Basic dGVzdDpzZWNyZXQ=",
          "Content-Type" -> "application/json")))

      route(app, request).map(status) mustBe Some(OK)
    }
  }

  "A badly-formed JSON request posted to the /api/translate endpoint" should {
    "result in a BadRequest (400) status" in {
      val request = FakeRequest("POST", "/api/translate")
        .withBody(invalidTranslationRequest)
        .withHeaders(FakeHeaders(Seq(
          "Host" -> "localhost",
          "Authorization" -> "Basic dGVzdDpzZWNyZXQ=",
          "Content-Type" -> "application/json")))

      route(app, request).map(status) mustBe Some(BAD_REQUEST)
    }
  }

  "A request that cannot be translated" should {
    "result in a NotFound (404) status" in {
      val request = FakeRequest("POST", "/api/translate")
        .withBody(noTranslationRequest)
        .withHeaders(FakeHeaders(Seq(
          "Host" -> "localhost",
          "Authorization" -> "Basic dGVzdDpzZWNyZXQ=",
          "Content-Type" -> "application/json")))

      route(app, request).map(status) mustBe Some(NOT_FOUND)
    }
  }

  "A request with an unsupported request type" should {
    "result in a NotFound (404) status" in {
      val request = FakeRequest("POST", "/api/translate")
        .withBody(unhandledTypeRequest)
        .withHeaders(FakeHeaders(Seq(
          "Host" -> "localhost",
          "Authorization" -> "Basic dGVzdDpzZWNyZXQ=",
          "Content-Type" -> "application/json")))

      route(app, request).map(status) mustBe Some(NOT_FOUND)
    }
  }

  "An fatal error in the server" should {
    "result in a InternalServerError (500) status" in {
      val request = FakeRequest("POST", "/api/translate")
        .withBody(failRequest)
        .withHeaders(FakeHeaders(Seq(
          "Host" -> "localhost",
          "Authorization" -> "Basic dGVzdDpzZWNyZXQ=",
          "Content-Type" -> "application/json")))

      route(app, request).map(status) mustBe Some(INTERNAL_SERVER_ERROR)
    }
  }


  val validJsonRequest ="""{
        |"text": "We strongly advise you to keep your luggage with you at all times. Any unattended luggage in the terminal will be removed by the security services and may be destroyed",
        |"language" : "de", "requestType" : "TEXT"
        |}""".stripMargin

  val invalidTranslationRequest ="""{"text": "not translatable"}""".stripMargin
  val noTranslationRequest ="""{"text": "not translatable", "language" : "de", "requestType" : "TEXT"}""".stripMargin
  val unhandledTypeRequest ="""{"text": "not translatable", "language" : "de", "requestType" : "SPEECH"}""".stripMargin
  val failRequest ="""{"text": "request should fail", "language" : "de", "requestType" : "FAIL"}""".stripMargin

}
