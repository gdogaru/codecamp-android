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

package com.gdogaru.codecamp.view.session


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import com.android.example.github.ui.common.DataBoundListAdapter
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.databinding.SessionExpandedItemBinding
import com.gdogaru.codecamp.databinding.SessionExpandedSpeakerItemBinding
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.util.AppExecutors
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.util.autoCleared
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class SessionInfoFragment : BaseFragment(), Injectable {
    @Inject
    lateinit var appExecutors: AppExecutors
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val viewModel: SessionInfoViewModel by viewModels { viewModelFactory }

    private var binding by autoCleared<SessionExpandedItemBinding>()
    private var adapter by autoCleared<SpeakersAdapter>()
    lateinit var sessionId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.session_expanded_item,
            container,
            false,
            dataBindingComponent
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionId = arguments?.getString(SESSION_ID) ?: ""

        adapter = SpeakersAdapter(dataBindingComponent, appExecutors)
        binding.speakers.adapter = adapter

        viewModel.getSession(sessionId).observe(this, Observer { s ->
            s?.let {
                binding.session = it.session
                binding.track = it.track
                adapter.submitList(it.speakers)

                viewModel.isBookmarked(it.session.id).observe(this, Observer { b ->
                    binding.bookmarked.isChecked = b
                })
                binding.bookmarked.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.setBookmarked(it.session.id, isChecked)
                }
            }
        })


//        firebaseAnalytics.logEvent("session_item", bundle);
    }

    companion object {

        const val SESSION_ID = "sessionId"

        fun newInstance(id: String): SessionInfoFragment {
            val sessionInfoFragment = SessionInfoFragment()
            sessionInfoFragment.arguments = Bundle().apply { putString(SESSION_ID, id) }
            return sessionInfoFragment
        }
    }
}

class SpeakersAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<Speaker, SessionExpandedSpeakerItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Speaker>() {
        override fun areItemsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun createBinding(parent: ViewGroup): SessionExpandedSpeakerItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.session_expanded_speaker_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(binding: SessionExpandedSpeakerItemBinding, item: Speaker) {
        binding.speaker = item
    }
}
