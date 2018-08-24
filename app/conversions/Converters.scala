package conversions

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, LocalTime, OffsetDateTime}
import java.util.UUID

import model.RequestType.RequestType
import model.{RequestType, TranslationRequest, TranslationResponse}
import play.api.libs.json._

object Converters {

  implicit val dateTimeReads: Reads[OffsetDateTime] = Reads.of[String] map (OffsetDateTime.parse(_))
  implicit val dateTimeWrites: Writes[OffsetDateTime] = Writes { dt: OffsetDateTime =>
    JsString(dt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))}

  implicit val localDateTimeReads: Reads[LocalDateTime] = Reads.of[String] map { dt =>
    LocalDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(dt))
  }

  implicit val localDateTimeWrites: Writes[LocalDateTime] = Writes { dt: LocalDateTime =>
    JsString(dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))}

  implicit val localTimeReads: Reads[LocalTime] = Reads.of[String] map { lt =>
    LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(lt))
  }

  implicit val localTimeWrites: Writes[LocalTime] = Writes { lt: LocalTime =>
    JsString(lt.format(DateTimeFormatter.ISO_LOCAL_TIME))}

  implicit val uuidReads: Reads[UUID] = Reads.of[String] map UUID.fromString
  implicit val uuidWrites: Writes[UUID] = Writes { uuid: UUID => JsString(uuid.toString)}

  implicit object eventTypeFormat extends Format[RequestType] {
    def reads(json: JsValue) = JsSuccess(RequestType.withName(json.as[String].toLowerCase))
    def writes(status: RequestType) =  JsString(status.toString.toLowerCase)
  }

  implicit val translationRequestFormat = Json.format[TranslationRequest]
  implicit val translationResponseFormat = Json.format[TranslationResponse]

}
