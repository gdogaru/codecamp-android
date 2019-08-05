/*
 * Copyright (c) 2019 Gabriel Dogaru - gdogaru@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
