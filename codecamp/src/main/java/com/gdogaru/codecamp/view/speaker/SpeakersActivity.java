package com.gdogaru.codecamp.view.speaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.ComparisonChain;
import com.gdogaru.codecamp.view.BaseActivity;
import com.gdogaru.codecamp.view.common.UiUtil;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gabriel on 10/14/2016.
 */

public class SpeakersActivity extends BaseActivity {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    CodecampClient codecampClient;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SpeakersActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.act_right_left, R.anim.act_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speakers);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }

    public void initData() {
        List<Speaker> sponsorList = codecampClient.getEvent().getSpeakers();
        Collections.sort(sponsorList, (o1, o2) ->
                ComparisonChain.start()
                        .compare(o1.getDisplayOrder(), o2.getDisplayOrder())
                        .result());
        recyclerView.setAdapter(new SpeakersAdapter(this, sponsorList));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, UiUtil.dpToPx(5), true));
    }

    static class SpeakersAdapter extends RecyclerView.Adapter {

        private final Activity activity;
        private final List<Speaker> speakerList;
        private final LayoutInflater layoutInflater;

        public SpeakersAdapter(Activity activity, List<Speaker> speakerList) {
            this.layoutInflater = LayoutInflater.from(activity);
            this.activity = activity;
            this.speakerList = speakerList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SpeakerHolder(layoutInflater.inflate(R.layout.speakers_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Speaker s = speakerList.get(position);
            SpeakerHolder vh = (SpeakerHolder) holder;
            Glide.with(activity)
                    .load(s.getPhotoUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.person_icon)
                            .fitCenter())
                    .into(vh.logo);
            vh.name.setText(s.getName());
            vh.job.setText(s.getJobTitle());
            vh.company.setText(s.getCompany());
            holder.itemView.setOnClickListener(v -> SpeakerExpandedActivity.start(activity, s.getName()));
        }

        @Override
        public int getItemCount() {
            return speakerList.size();
        }
    }

    public static class SpeakerHolder extends RecyclerView.ViewHolder {

        public final ImageView logo;
        public final TextView name;
        public final TextView job;
        public final TextView company;

        public SpeakerHolder(@NonNull View itemView) {
            super(itemView);
            this.logo = (ImageView) itemView.findViewById(R.id.logo);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.job = (TextView) itemView.findViewById(R.id.job);
            this.company = (TextView) itemView.findViewById(R.id.company);
        }
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
