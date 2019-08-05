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

package com.gdogaru.codecamp.view.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.api.model.Schedule;
import com.gdogaru.codecamp.util.DateUtil;

import java.util.List;

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

public class SchedulesAdapter extends RecyclerView.Adapter<SchedulesAdapter.ScheduleHolder> {
    private final LayoutInflater layoutInflater;
    @NonNull
    private final Listener<Pair<MainViewItem, Integer>> listener;
    private final List<MainViewItem> schedules;

    SchedulesAdapter(Context context, List<MainViewItem> schedules, @NonNull Listener<Pair<MainViewItem, Integer>> listener) {
        this.schedules = schedules;
        this.listener = listener;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ScheduleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduleHolder(layoutInflater.inflate(R.layout.main_schedule_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ScheduleHolder holder, int position) {
        MainViewItem item = schedules.get(position);
        if (item instanceof MainViewItem.AgendaItem) {
            Schedule schedule = ((MainViewItem.AgendaItem) item).getSchedule();
            holder.title.setText(R.string.event_schedule);
            holder.date.setText(DateUtil.formatDayOfYear(schedule.getDate()));
            holder.date.setVisibility(View.VISIBLE);
        } else if (item instanceof MainViewItem.SpeakersItem) {
            holder.title.setText(R.string.speakers);
            holder.date.setVisibility(View.GONE);
        } else if (item instanceof MainViewItem.SponsorsItem) {
            holder.title.setText(R.string.sponsors);
            holder.date.setVisibility(View.GONE);
        } else {
            throw new IllegalStateException("Could not treat " + item);
        }
        holder.itemView.setOnClickListener(v -> listener.apply(new Pair<>(item, position)));
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public MainViewItem getItem(int position) {
        return schedules.get(position);
    }

    public interface Listener<T> {
        void apply(T t);
    }

    static class ScheduleHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;

        ScheduleHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}