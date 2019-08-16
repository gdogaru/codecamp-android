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

package com.gdogaru.codecamp.util;

import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
public class Throwables {

    private Throwables() {
    }

    public static <X extends Throwable> void propagateIfInstanceOf(@Nullable Throwable throwable, Class<X> declaredType) throws X {
        if (throwable != null && declaredType.isInstance(throwable)) {
            throw (X) declaredType.cast(throwable);
        }
    }

    public static void propagateIfPossible(@Nullable Throwable throwable) {
        propagateIfInstanceOf(throwable, Error.class);
        propagateIfInstanceOf(throwable, RuntimeException.class);
    }

    public static <X extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X> declaredType) throws X {
        propagateIfInstanceOf(throwable, declaredType);
        propagateIfPossible(throwable);
    }

    public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(@Nullable Throwable throwable, Class<X1> declaredType1, Class<X2> declaredType2) throws X1, X2 {
        Preconditions.checkNotNull(declaredType2);
        propagateIfInstanceOf(throwable, declaredType1);
        propagateIfPossible(throwable, declaredType2);
    }

    public static RuntimeException propagate(Throwable throwable) {
        propagateIfPossible((Throwable) Preconditions.checkNotNull(throwable));
        throw new RuntimeException(throwable);
    }

    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause;
        while ((cause = throwable.getCause()) != null) {
            throwable = cause;
        }

        return throwable;
    }

    public static List<Throwable> getCausalChain(Throwable throwable) {
        Preconditions.checkNotNull(throwable);

        ArrayList causes;
        for (causes = new ArrayList(4); throwable != null; throwable = throwable.getCause()) {
            causes.add(throwable);
        }

        return Collections.unmodifiableList(causes);
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
