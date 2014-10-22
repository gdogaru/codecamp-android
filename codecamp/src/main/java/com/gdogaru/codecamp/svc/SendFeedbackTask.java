package com.gdogaru.codecamp.svc;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gdogaru.codecamp.db.DatabaseHelper;
import com.gdogaru.codecamp.model.Feedback;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class SendFeedbackTask extends AsyncTask<Void, Long, Long> {


    private Context context;
    private CodecampClient codecampClient;
    private Feedback feedback;
    private DatabaseHelper dbHelper;
    private ProgressDialog progressDialog;

    public SendFeedbackTask(CodecampClient codecampClient, Feedback feedback, Context context) {
        this.codecampClient = codecampClient;
        this.feedback = feedback;
        this.context = context;
    }


    @Override
    protected Long doInBackground(Void... params) {

//        codecampClient.sendFeedback(feedback);
        return 0L;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Processing...", "Sending feedback...");
    }

    @Override
    protected void onPostExecute(Long aLong) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (aLong == 0L) {
            //success
        } else {
            Toast.makeText(context, "Error sending feedback data.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }
}