package com.gdogaru.codecamp.model.json;

import com.gdogaru.codecamp.util.StringUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

/**
 * "local" date/time with or without time zone offset
 * (in the format "yyyy-dd-mmThh:mm:ss[.fff]").
 */
public class LocalDateTimeTypeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    private static final DateTimeParser FRACTION_PARSER = new DateTimeFormatterBuilder()
            .appendLiteral('.')
            .appendFractionOfSecond(3, 9)
            .toParser();
    private static final DateTimeParser TZONE_PARSER = new DateTimeFormatterBuilder()
            .appendTimeZoneOffset("Z", true, 2, 4)
            .toParser();
    private static final DateTimeParser TIME_PARSER = new DateTimeFormatterBuilder()
            .appendLiteral('T')
            .append(ISODateTimeFormat.hourMinuteSecond())
            .toParser();
    private static final DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .append(ISODateTimeFormat.date())
            .appendOptional(TIME_PARSER)
            .appendOptional(FRACTION_PARSER)
            .appendOptional(TZONE_PARSER)
            .toFormatter()
            .withZone(DateTimeZone.UTC);

    @Override
    public LocalDateTime deserialize(final JsonElement je, final Type type, final JsonDeserializationContext jdc) throws JsonParseException {
        String string = je.getAsString();
        return string.length() == 0 ? null : fmt.parseLocalDateTime(string);
    }

    @Override
    public JsonElement serialize(final LocalDateTime src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(src == null ? StringUtils.EMPTY : fmt.print(src));
    }
}