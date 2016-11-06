package com.gdogaru.codecamp.model.json;

import com.gdogaru.codecamp.util.StringUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

/**
 * Created by Gabriel on 11/5/2015.
 */
public class LocalTimeTypeAdapter implements JsonDeserializer<LocalTime>, JsonSerializer<LocalTime> {
    DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");


    @Override
    public LocalTime deserialize(final JsonElement je, final Type type, final JsonDeserializationContext jdc) throws JsonParseException {
        String string = je.getAsString();
        return string.length() == 0 ? null : fmt.parseLocalTime(string);
    }

    @Override
    public JsonElement serialize(final LocalTime src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(src == null ? StringUtils.EMPTY : fmt.print(src));
    }
}