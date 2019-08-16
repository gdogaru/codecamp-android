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