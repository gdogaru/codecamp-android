package com.gdogaru.codecamp.api.model.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException

val LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("UTC"))

class LocalTimeSerializer : JsonSerializer<LocalTime>() {


    @Throws(IOException::class)
    override fun serialize(value: LocalTime, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(LOCAL_TIME_FORMATTER.format(value))
    }
}

class LocalTimeDeserializer : JsonDeserializer<LocalTime>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalTime {
        return LocalTime.from(LOCAL_TIME_FORMATTER.parse(p.text.trim()))
    }
}
