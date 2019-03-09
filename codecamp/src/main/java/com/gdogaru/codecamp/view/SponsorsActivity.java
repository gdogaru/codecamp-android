package com.gdogaru.codecamp.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Sponsor;
import com.gdogaru.codecamp.model.SponsorshipPackage;
import com.gdogaru.codecamp.svc.CodecampClient;
import com.gdogaru.codecamp.util.ComparisonChain;
import com.gdogaru.codecamp.util.Strings;
import com.gdogaru.codecamp.view.common.UiUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by Gabriel on 10/14/2016.
 */

public class SponsorsActivity extends BaseActivity implements HasSupportFragmentInjector {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    CodecampClient codecampClient;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SponsorsActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.act_right_left, R.anim.act_left_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sponsors);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
    }

    public void initData() {
        List<Sponsor> sponsorList = codecampClient.getEvent().getSponsors();
        List<SponsorshipPackage> packages = codecampClient.getEvent().getSponsorshipPackages();
        Map<String, Integer> ptoIdx = new HashMap<>();
        for (SponsorshipPackage p : packages) {
            ptoIdx.put(p.getName(), p.getDisplayOrder());
        }

        Collections.sort(sponsorList, (o1, o2) ->
                ComparisonChain.start()
                        .compare(ptoIdx.get(o1.getSponsorshipPackage()), ptoIdx.get(o2.getSponsorshipPackage()))
                        .compare(o1.getDisplayOrder(), o2.getDisplayOrder())
                        .result());
        recyclerView.setAdapter(new SponsorsAdapter(this, sponsorList));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, UiUtil.dpToPx(5), true));
    }

    static class SponsorsAdapter extends RecyclerView.Adapter {

        private final Activity activity;
        private final List<Sponsor> sponsorList;
        private final LayoutInflater layoutInflater;

        public SponsorsAdapter(Activity activity, List<Sponsor> sponsorList) {
            this.layoutInflater = LayoutInflater.from(activity);
            this.activity = activity;
            this.sponsorList = sponsorList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SponsorHolder(layoutInflater.inflate(R.layout.sponsors_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Sponsor s = sponsorList.get(position);
            SponsorHolder vh = (SponsorHolder) holder;
            Glide.with(activity)
                    .load(s.getLogoUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.background_white)
                            .fitCenter())
                    .into(vh.logo);
            vh.name.setText(s.getName());
            vh.description.setText(s.getSponsorshipPackage());

            if (Strings.isNullOrEmpty(s.getWebsiteUrl())) {
                holder.itemView.setOnClickListener(null);
            } else {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(s.getWebsiteUrl()));
                        activity.startActivity(i);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return sponsorList.size();
        }
    }

    public static class SponsorHolder extends RecyclerView.ViewHolder {

        public final ImageView logo;
        public final TextView name;
        public final TextView description;

        public SponsorHolder(@NonNull View itemView) {
            super(itemView);
            this.logo = (ImageView) itemView.findViewById(R.id.logo);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.description = (TextView) itemView.findViewById(R.id.type);
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

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
