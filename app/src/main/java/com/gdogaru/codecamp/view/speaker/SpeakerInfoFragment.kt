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


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.databinding.SessionExpandedSpeakerItemBinding
import com.gdogaru.codecamp.databinding.SessionItemBinding
import com.gdogaru.codecamp.databinding.SpeakersInfoBinding
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.common.DataBoundViewHolder
import com.gdogaru.codecamp.view.util.autoCleared
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class SpeakerInfoFragment : BaseFragment(), Injectable {
    lateinit var speakerId: String
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SpeakerInfoViewModel
    private var binding by autoCleared<SpeakersInfoBinding>()
    private var adapter by autoCleared<SpeakerInfoDataAdapter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.speakers_info,
                container,
                false,
                dataBindingComponent
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(SpeakerInfoViewModel::class.java)

        speakerId = arguments!!.getString(SPEAKER_ID)!!

        val bundle = Bundle()
        bundle.putString("speaker", speakerId)
        firebaseAnalytics.logEvent("speaker_view", bundle)

        adapter = SpeakerInfoDataAdapter(dataBindingComponent) { s, b -> viewModel.setBookmarked(s.id, b) }
        binding.dataRecycler.adapter = adapter

        viewModel.getSpeakerFull(speakerId).observe(this, Observer { d ->
            d?.let { adapter.submitData(it) }
        })
    }

    companion object {
        const val SPEAKER_ID = "speakerId"

        fun newInstance(speakerName: String): SpeakerInfoFragment {
            val sessionInfoFragment = SpeakerInfoFragment()
            sessionInfoFragment.arguments = Bundle()
            sessionInfoFragment.arguments!!.putString(SPEAKER_ID, speakerName)
            return sessionInfoFragment
        }
    }
}

class SpeakerInfoDataAdapter(
        private val dataBindingComponent: DataBindingComponent,
        private val sessionBookmarkListener: (Session, Boolean) -> Unit
) : RecyclerView.Adapter<DataBoundViewHolder<ViewDataBinding>>() {


    private var data: FullSpeakerData? = null

    override fun getItemCount(): Int {
        return data?.sessions?.size?.let { it + 1 } ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is Speaker -> 1
            is Session -> 2
            else -> throw IllegalStateException("unknown $item")
        }
    }

    private fun getItem(position: Int): Any {
        return when (position) {
            0 -> data!!.speaker
            else -> data!!.sessions[position - 1].session
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<ViewDataBinding> {
        return when (viewType) {
            1 -> DataBoundViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.session_expanded_speaker_item,
                    parent,
                    false,
                    dataBindingComponent
            ))

            2 -> DataBoundViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.session_item,
                    parent,
                    false,
                    dataBindingComponent
            ))
            else -> throw IllegalStateException("unknown viewType $viewType")
        }
    }


    override fun onBindViewHolder(holder: DataBoundViewHolder<ViewDataBinding>, position: Int) {
        when (val binding = holder.binding) {
            is SessionItemBinding -> {
                val d = data!!.sessions[position - 1]
                binding.session = d.session
                binding.track = d.track
                binding.bookmarked.isChecked = d.isBookmarked
                binding.bookmarked.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != d.isBookmarked) {
                        sessionBookmarkListener.invoke(d.session, isChecked)
                    }
                }
            }
            is SessionExpandedSpeakerItemBinding -> {
                binding.speaker = data!!.speaker
            }
            else -> throw  java.lang.IllegalStateException("unknown")
        }
    }

    fun submitData(data: FullSpeakerData) {
        this.data = data
        notifyDataSetChanged()
    }
}