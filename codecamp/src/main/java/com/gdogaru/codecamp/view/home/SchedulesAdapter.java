package com.gdogaru.codecamp.view.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.api.model.Schedule;
import com.gdogaru.codecamp.util.DateUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Gabriel on 2/15/2017.
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