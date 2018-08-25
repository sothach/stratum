package routing

import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Await
import scala.concurrent.duration._

class RouteSpec extends PlaySpec with ScalaFutures with GuiceOneAppPerSuite {

  override def fakeApplication() = new GuiceApplicationBuilder()
    .loadConfig(Configuration(ConfigFactory.load("application.conf")))
    .configure(Map("play.filters.disabled" -> Seq("play.filters.csrf.CSRFFilter"),
      "play.filters.hosts.allowed" -> Seq("localhost")))
    .build()

  "GET /" should {
    "invoke the index" in {
      val request = FakeRequest("GET", "/")
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(OK)
    }
  }

  "An invalid auth token in request posted to the /api/translate endpoint" should {
    "result in an Unauthorized (401) status" in {
      val request = FakeRequest("POST", "/api/translate?apiKey=1234567890")
        .withBody(validJsonRequest)
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(UNAUTHORIZED)
    }
  }

  "An translation request posted to the /api/translate endpoint" should {
    "be routed successfully" in {
      val request = FakeRequest("POST", s"/api/translate?apiKey=$apiKey")
        .withBody(validJsonRequest)
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(OK)
    }
  }

  "A badly-formed JSON request posted to the /api/translate endpoint" should {
    "result in a BadRequest (400) status" in {
      val request = FakeRequest("POST", s"/api/translate?apiKey=$apiKey")
        .withBody(invalidTranslationRequest)
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(BAD_REQUEST)
    }
  }

  "A request that cannot be translated" should {
    "result in a NotFound (404) status" in {
      val request = FakeRequest("POST", s"/api/translate?apiKey=$apiKey")
        .withBody(noTranslationRequest)
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(NOT_FOUND)
    }
  }

  "A request with an unsupported request type" should {
    "result in a NotFound (404) status" in {
      val request = FakeRequest("POST", s"/api/translate?apiKey=$apiKey")
        .withBody(unhandledTypeRequest)
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(NOT_FOUND)
    }
  }

  "An fatal error in the server" should {
    "result in a InternalServerError (500) status" in {
      val request = FakeRequest("POST", s"/api/translate?apiKey=$apiKey")
        .withBody(failRequest)
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(INTERNAL_SERVER_ERROR)
    }
  }

  "A valid call to the speech service" should {
    "return successfully" in {
      val url =
        s"""/api/speech?apiKey=$apiKey
           |&action=convert
           |&text=something
           |&voice=usenglishfemale
           |&format=mp3""".stripMargin.replaceAll("\n", "")
      val request = FakeRequest("GET", url)
        .withBody(failRequest)
        .withHeaders(fakedHeaders)

      route(app, request) foreach { future =>
        val result = Await.result(future, 10 seconds)
        result.header.status mustBe OK
        result.body.contentType mustBe Some("audio/mp3")
        result.body.isKnownEmpty mustBe false
      }
    }
  }

  "A call to the speech service with an invalid api key" should {
    "return Unauthorized" in {
      val url = s"""/api/speech?apiKey=1234567890
                   |&action=convert
                   |&text=something
                   |&voice=usenglishfemale
                   |&format=mp3""".stripMargin.replaceAll("\n","")
      val request = FakeRequest("GET", url)
        .withBody(failRequest)
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(UNAUTHORIZED)
    }
  }

  "A call to the speech service that cannot be converted" should {
    "return not found (404)" in {
      val url = s"""/api/speech?apiKey=$apiKey
                   |&action=convert
                   |&text=fail
                   |&voice=usenglishfemale
                   |&format=mp3""".stripMargin.replaceAll("\n","")
      val request = FakeRequest("GET", url)
        .withBody(failRequest)
        .withHeaders(fakedHeaders)

      route(app, request).map(status) mustBe Some(NOT_FOUND)
    }
  }

  val apiKey = "eabb12404d141ed6e8ee2193688178cb"
  val fakedHeaders = FakeHeaders(Map(
    "Host" -> "localhost",
    "Content-Type" -> "application/json").toSeq)

  val validJsonRequest ="""{
        |"text": "We strongly advise you to keep your luggage with you at all times. Any unattended luggage in the
        | terminal will be removed by the security services and may be destroyed",
        |"language" : "de", "requestType" : "TEXT"}""".stripMargin.replaceAll("\n","")
  val invalidTranslationRequest ="""{"text": "not translatable"}""".stripMargin
  val noTranslationRequest ="""{"text": "not translatable", "language" : "de", "requestType" : "TEXT"}""".stripMargin
  val unhandledTypeRequest ="""{"text": "not translatable", "language" : "de", "requestType" : "SPEECH"}""".stripMargin
  val failRequest ="""{"text": "request should fail", "language" : "de", "requestType" : "FAIL"}""".stripMargin

}
