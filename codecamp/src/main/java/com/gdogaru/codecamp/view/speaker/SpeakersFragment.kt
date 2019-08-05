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

package com.gdogaru.codecamp.view.speaker

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Codecamp
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.svc.CodecampClient
import com.gdogaru.codecamp.util.ComparisonChain
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.common.UiUtil
import com.gdogaru.codecamp.view.util.autoCleared
import javax.inject.Inject

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

class SpeakersFragment : BaseFragment() {

    @BindView(R.id.recycler)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @Inject
    lateinit var codecampClient: CodecampClient

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    private var speakersAdapter by autoCleared<SpeakersAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.speakers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ButterKnife.bind(this, view)
        val ma = activity as MainActivity?
        ma!!.setSupportActionBar(toolbar)
        ma.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        speakersAdapter = SpeakersAdapter(requireActivity()) { id, avatar -> showSpeaker(id, avatar) }
        recyclerView.adapter = speakersAdapter
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3, UiUtil.dpToPx(5f), true))


        viewModel.currentEvent.observe(this, Observer { initData(it) })
    }

    private fun showSpeaker(s: Speaker, avatar: ImageView) {
        val extras = FragmentNavigatorExtras(
                avatar to "speakerPhoto")

        findNavController().navigate(SpeakersFragmentDirections.showSpeaker(s.name), extras)
    }

    fun initData(event: Codecamp) {
        var speakerList = event.speakers.orEmpty()
        speakerList = speakerList.sortedWith(Comparator { o1, o2 ->
            ComparisonChain.start()
                    .compare(o1.displayOrder, o2.displayOrder)
                    .result()
        })
        speakersAdapter.updateItems(speakerList)
    }

}

class SpeakersAdapter(private val activity: Activity,
                      private val callback: (Speaker, ImageView) -> Unit)
    : RecyclerView.Adapter<SpeakerHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
    private val speakerList = mutableListOf<Speaker>()

    fun updateItems(value: List<Speaker>) {
        speakerList.clear()
        speakerList.addAll(value)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerHolder {
        return SpeakerHolder(layoutInflater.inflate(R.layout.speakers_item, parent, false))
    }

    override fun onBindViewHolder(vh: SpeakerHolder, position: Int) {
        val s = speakerList[position]
        Glide.with(activity)
                .load(s.photoUrl)
                .apply(RequestOptions()
                        .placeholder(R.drawable.person_icon)
                        .fitCenter())
                .into(vh.logo)
        vh.name.text = s.name
        vh.job.text = s.jobTitle
        vh.company.text = s.company
        vh.itemView.setOnClickListener { callback.invoke(s, vh.logo) }
    }

    override fun getItemCount(): Int {
        return speakerList.size
    }
}

class SpeakerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    @BindView(R.id.logo)
    lateinit var logo: ImageView
    @BindView(R.id.name)
    lateinit var name: TextView
    @BindView(R.id.job)
    lateinit var job: TextView
    @BindView(R.id.company)
    lateinit var company: TextView

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

