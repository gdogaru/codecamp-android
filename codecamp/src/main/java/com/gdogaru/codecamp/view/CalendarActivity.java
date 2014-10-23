package com.gdogaru.codecamp.view;

import android.os.Bundle;

import com.gdogaru.codecamp.R;

/**
 * Created by Gabriel on 10/23/2014.
 */
public class CalendarActivity extends CodecampActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);
        setChildActionBar(R.string.calendar);
    }

}
