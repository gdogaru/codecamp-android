/*
 * Copyright (C) 2008 Gabriel Dogaru (gdogaru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdogaru.codecamp.svc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gdogaru.codecamp.util.IOUtils;

import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class SpeakerPhotoUtils {
    private static final Logger LOG = getLogger(SpeakerPhotoUtils.class);

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
            LOG.error("Error saving photo.", e);
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
        FileOutputStream fos = null;
        try {
            fos = context.getApplicationContext().openFileOutput(String.format(Locale.US, FILENAME_MASK, id), Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (Exception e) {
            LOG.error("Error saving overview.", e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public static void removeAll(Context context) {
        String[] files = context.getApplicationContext().fileList();
        for (String file : files) {
            context.getApplicationContext().deleteFile(file);
        }
    }
}
