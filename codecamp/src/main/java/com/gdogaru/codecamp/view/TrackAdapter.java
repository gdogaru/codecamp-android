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

package com.gdogaru.codecamp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gdogaru.codecamp.R;

import java.util.List;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class TrackAdapter extends BaseAdapter {

    private final Context context;
    private List<com.gdogaru.codecamp.model.Track> tracks;
    private LayoutInflater mInflater;

    public TrackAdapter(Context context, List<com.gdogaru.codecamp.model.Track> tracks) {
        this.context = context;

        this.tracks = tracks;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tracks.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView == null ? mInflater.inflate(R.layout.spinner_item, parent, false) : convertView;
        String text = tracks.get(position).getName();
        ((TextView) view).setText(text);
        return view;
    }
}
