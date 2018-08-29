package model

case class ApiKey private (value: String)

object ApiKey {
  val pattern = "([a-f0-9]{32})".r
  def apply(value: String): ApiKey =
    value.trim.toLowerCase match {
      case pattern(key) =>
        new ApiKey(key)
      case _ =>
        new ApiKey("")
    }
}
