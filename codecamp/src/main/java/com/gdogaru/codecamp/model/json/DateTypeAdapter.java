package com.gdogaru.codecamp.model.json;

import com.gdogaru.codecamp.util.StringUtils;
import com.gdogaru.codecamp.util.Strings;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Gabriel on 11/5/2015.
 */
public class DateTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
    public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    final DateFormat dateFormat;

    public DateTypeAdapter() {
        dateFormat = new SimpleDateFormat(FORMAT, Locale.getDefault());
    }

    @Override
    public Date deserialize(final JsonElement je, final Type type, final JsonDeserializationContext jdc) throws JsonParseException {
        try {
            String string = je.getAsString();
            return Strings.isNullOrEmpty(string) ? null : dateFormat.parse(string);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonElement serialize(final Date src, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(src == null ? StringUtils.EMPTY : dateFormat.format(src));
    }
}