package model

import org.scalatest.{FlatSpec, Matchers}

class ModelSpec extends FlatSpec with Matchers {

  "Valid RequestType strings" should "be deserialized" in {
    RequestType.findOrDefault("text") shouldBe RequestType.TEXT
  }
  "Invalid RequestType strings" should "be deserialized as default" in {
    RequestType.findOrDefault("XXX") shouldBe RequestType.FAIL
  }

  "Valid SpeechAction strings" should "be deserialized" in {
    SpeechAction.findOrDefault("convert") shouldBe SpeechAction.CONVERT
  }
  "Invalid SpeechAction strings" should "be deserialized as default" in {
    SpeechAction.findOrDefault("XXX") shouldBe SpeechAction.FAIL
  }

  "Valid Voice strings" should "be deserialized" in {
    Voice.findOrDefault("useenglishfemale") shouldBe Voice.ENGLISHF
  }
  "Invalid Voice strings" should "be deserialized as default" in {
    Voice.findOrDefault("XXX") shouldBe Voice.ENGLISHF
  }

  "Valid SpeechFormat strings" should "be deserialized" in {
    SpeechFormat.findOrDefault("mp3") shouldBe SpeechFormat.MP3
  }
  "Invalid SpeechFormat strings" should "be deserialized as default" in {
    SpeechFormat.findOrDefault("XXX") shouldBe SpeechFormat.MP3
  }

}
