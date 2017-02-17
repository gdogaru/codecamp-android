package com.gdogaru.codecamp.view.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.internal.util.Predicate;
import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.EventList;
import com.gdogaru.codecamp.model.EventSummary;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.DateUtil;
import com.gdogaru.codecamp.view.common.DividerItemDecoration;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gabriel on 2/16/2017.
 */

public class SidebarFragment extends Fragment {

    @BindView(R.id.events)
    RecyclerView eventsRecycler;
    @Inject
    CodecampClient codecampClient;
    private EventsAdapter eventsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getDiComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_sidebar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        eventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration decor = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, ContextCompat.getDrawable(getActivity(), R.drawable.list_vertical_divider_sidebar));
        eventsRecycler.addItemDecoration(decor);

        eventsAdapter = new EventsAdapter(LayoutInflater.from(getActivity()), codecampClient.getEventsSummary(), eventSummary -> {
            onItemClicked(eventSummary);
            return true;
        });
        eventsRecycler.setAdapter(eventsAdapter);

    }

    private void onItemClicked(EventSummary item) {
        long refId = item.getRefId();
        codecampClient.setActiveEvent(refId);
        ((MainActivity) getActivity()).initDisplay();
    }

    static class EventHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.eventTitle)
        TextView eventTitle;
        @BindView(R.id.eventDate)
        TextView eventDate;
        @BindView(R.id.city)
        TextView city;

        public EventHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class EventsAdapter extends RecyclerView.Adapter<EventHolder> {

        private final LayoutInflater inflater;
        private final EventList eventList;
        private final Predicate<EventSummary> listener;

        EventsAdapter(LayoutInflater inflater, EventList eventList, @NonNull Predicate<EventSummary> listener) {
            this.inflater = inflater;
            this.eventList = eventList;
            this.listener = listener;
        }


        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new EventHolder(inflater.inflate(R.layout.main_sidebar_item, parent, false));
        }


        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            EventSummary summary = eventList.get(position);
            holder.eventTitle.setText(summary.getTitle());
            holder.eventDate.setText(DateUtil.formatEventPeriod(summary.getStartDate(), summary.getEndDate()));
            holder.city.setText(summary.getVenue().getCity());
            holder.itemView.setOnClickListener(v -> listener.apply(summary));
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public EventSummary getItem(int position) {
            return eventList.get(position);
        }
    }
}
