package com.gdogaru.codecamp.api.model.json;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;


public class ThreeTenModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public ThreeTenModule() {
        super(PackageVersion.VERSION);

        addSerializer(Instant.class, new InstantSerializer());
        addDeserializer(Instant.class, new InstantDeserializer());

        addSerializer(LocalTime.class, new LocalTimeSerializer());
        addDeserializer(LocalTime.class, new LocalTimeDeserializer());

        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

    }
}