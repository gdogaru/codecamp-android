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

package com.gdogaru.codecamp.view.sponsors

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.api.model.Sponsor
import com.gdogaru.codecamp.util.ComparisonChain
import com.gdogaru.codecamp.util.Strings
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.common.UiUtil
import com.gdogaru.codecamp.view.util.autoCleared
import java.util.*
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

class SponsorsFragment : BaseFragment() {

    @BindView(R.id.recycler)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = layoutInflater.inflate(R.layout.sponsors, container, false)

    private var sponsorsAdapter by autoCleared<SponsorsAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manage(ButterKnife.bind(this, view))

        val act = activity as MainActivity
        act.setSupportActionBar(toolbar)
        act.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        sponsorsAdapter = SponsorsAdapter(requireActivity(), { showSponsor(it) })
        recyclerView.adapter = sponsorsAdapter
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3, UiUtil.dpToPx(5f), true))

        viewModel.currentEvent.observe(this, androidx.lifecycle.Observer { showData(it) })
    }

    private fun showSponsor(s: Sponsor) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(s.websiteUrl)
        requireActivity().startActivity(i)
    }


    private fun showData(c: Codecamp) {
        var sponsorList = c.sponsors
        val packages = c.sponsorshipPackages!!
        val ptoIdx = HashMap<String, Int>()
        for (p in packages) {
            ptoIdx[p.name.orEmpty()] = p.displayOrder
        }

        sponsorList = sponsorList.orEmpty().sortedWith(Comparator { o1, o2 ->
            ComparisonChain.start()
                    .compare(ptoIdx[o1.sponsorshipPackage], ptoIdx[o2.sponsorshipPackage])
                    .compare(o1.displayOrder, o2.displayOrder)
                    .result()
        })

        sponsorsAdapter.updateItems(sponsorList.orEmpty())
    }
}

class SponsorsAdapter(private val context: Context,
                      private val callback: (Sponsor) -> Unit)
    : RecyclerView.Adapter<SponsorHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var sponsorList = mutableListOf<Sponsor>()

    fun updateItems(value: List<Sponsor>) {
        sponsorList.clear()
        sponsorList.addAll(value)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SponsorHolder(layoutInflater.inflate(R.layout.sponsors_item, parent, false))

    override fun onBindViewHolder(vh: SponsorHolder, position: Int) {
        val s = sponsorList[position]
        Glide.with(context)
                .load(s.logoUrl)
                .apply(RequestOptions()
                        .placeholder(R.drawable.background_white)
                        .fitCenter())
                .into(vh.logo)

        vh.name.text = s.name
        vh.description.text = s.sponsorshipPackage

        if (Strings.isNullOrEmpty(s.websiteUrl)) {
            vh.itemView.setOnClickListener(null)
        } else {
            vh.itemView.setOnClickListener {
                callback.invoke(s)
            }
        }
    }

    override fun getItemCount() = sponsorList.size
}

class SponsorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.logo)
    lateinit var logo: ImageView
    @BindView(R.id.name)
    lateinit var name: TextView
    @BindView(R.id.type)
    lateinit var description: TextView

    init {
        ButterKnife.bind(this, itemView)
    }
}

class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}

