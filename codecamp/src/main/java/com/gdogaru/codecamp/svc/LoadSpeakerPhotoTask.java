package com.gdogaru.codecamp.svc;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.gdogaru.codecamp.model.Speaker;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class LoadSpeakerPhotoTask extends AsyncTask<Void, Long, Long> {

    private ImageView imageView;
    private Speaker speaker;
    private Context context;
    private Bitmap bitmap;

    public LoadSpeakerPhotoTask(ImageView imageView, Speaker speaker, Context context) {
        this.imageView = imageView;
        this.speaker = speaker;
        this.context = context.getApplicationContext();
    }

    @Override
    protected Long doInBackground(Void... params) {
        try {
            bitmap = SpeakerPhotoUtils.getSpeakerPhoto(context, speaker.getId(), speaker.getPortraitImageUrl());
        } catch (Exception ignored) {

        }
        return 0L;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        if (bitmap != null && imageView.isShown()) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
