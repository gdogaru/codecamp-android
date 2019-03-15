package com.gdogaru.codecamp.api.model.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException

val LOCAL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        .withZone(ZoneId.of("UTC"))

class LocalDateTimeSerializer : JsonSerializer<LocalDateTime>() {


    @Throws(IOException::class)
    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(LOCAL_DATETIME_FORMATTER.format(value))
    }
}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalDateTime {
        return LocalDateTime.from(LOCAL_DATETIME_FORMATTER.parse(p.text.trim()))
    }
}
