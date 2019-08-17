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

package com.gdogaru.codecamp.svc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import timber.log.Timber;

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
public class SpeakerPhotoUtils {
    private static final String FILENAME_MASK = "speaker_%d.jpg";

    public static Bitmap getSpeakerPhoto(Context context, long id, String url) {
        Bitmap b = getLocalSpeakerPhoto(context, id);
        if (b != null) {
            return b;
        }
        try {
            Bitmap imageFromUrl = getImageFromUrl(url);
            savePhoto(context, imageFromUrl, id);
            return imageFromUrl;
        } catch (Exception e) {
            Timber.e(e, "Error saving photo.");
        }
        return null;
    }

    private static Bitmap getImageFromUrl(String urlValue) throws IOException {
        URL url = new URL(urlValue);
        InputStream content = (InputStream) url.getContent();
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.outHeight = 100;
        o.outWidth = 100;
        return BitmapFactory.decodeStream(content, null, o);
    }

    public static Bitmap getLocalSpeakerPhoto(Context context, long id) {
        InputStream i = null;
        try {
            i = context.getApplicationContext().openFileInput(String.format(Locale.US, FILENAME_MASK, id));
        } catch (FileNotFoundException e) {
            return null;
        }
        return BitmapFactory.decodeStream(i);
    }

    public static void savePhoto(Context context, Bitmap bitmap, long id) {
        try (FileOutputStream fos = context.getApplicationContext().openFileOutput(String.format(Locale.US, FILENAME_MASK, id), Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (Exception e) {
            Timber.e(e, "Error saving overview.");
        }
    }

    public static void removeAll(Context context) {
        String[] files = context.getApplicationContext().fileList();
        for (String file : files) {
            context.getApplicationContext().deleteFile(file);
        }
    }
}
