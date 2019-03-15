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

package com.gdogaru.codecamp.view.speaker


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.util.DateUtil
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.TrackData
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*
import javax.inject.Inject

class SpeakerInfoFragment : BaseFragment(), Injectable {
    lateinit var speakerId: String
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics
    private var rootView: LinearLayout? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainViewModel::class.java)

        speakerId = if (savedInstanceState != null && savedInstanceState.containsKey(SPEAKER_ID)) {
            savedInstanceState.getString(SPEAKER_ID)!!
        } else {
            arguments!!.getString(SPEAKER_ID)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frame_vertical, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manage(ButterKnife.bind(this, view))
        rootView = view.findViewById<View>(R.id.content) as LinearLayout

        viewModel.getSpeakerFull(speakerId).observe(this, Observer { d ->
            d?.let { initView(it.speaker, it.sessions) }
        })
    }

    fun initView(speaker: Speaker, sessions: List<Pair<Session, TrackData>>) {
        rootView!!.addView(addSpeaker(speaker))
        for (session in sessions) {
            rootView!!.addView(addSession(session.first, session.second))
        }

        val bundle = Bundle()
        bundle.putString("speaker", speakerId)
        firebaseAnalytics.logEvent("speaker_view", bundle)
    }

    private fun addSession(session: Session, track: TrackData): View {
        val sessionView = activity!!.layoutInflater.inflate(R.layout.session_view, rootView, false)
        val sessionTitle = sessionView.findViewById<View>(R.id.sessionTitle) as TextView
        val sessionTime = sessionView.findViewById<View>(R.id.sessionTime) as TextView
        val sessionDescription = sessionView.findViewById<View>(R.id.sessionDescription) as TextView
        val sessionTrack = sessionView.findViewById<View>(R.id.sessionTrack) as TextView
        val bookmarked = sessionView.findViewById<View>(R.id.bookmarked) as CheckBox
        val sessionTrackLayout = sessionView.findViewById<View>(R.id.sessionTrackLayout) as LinearLayout

        sessionTitle.text = session.title
        sessionDescription.text = session.description
        val timeString = DateUtil.formatPeriod(session.startTime, session.endTime)
        sessionTime.text = timeString
        bookmarked.isChecked = viewModel.isBookmarked(session.id)
        bookmarked.setOnCheckedChangeListener { _, isChecked -> viewModel.setBookmarked(session.id, isChecked) }
        if (track.track != null) {
            sessionTrackLayout.visibility = View.VISIBLE
            sessionTrack.text = String.format(Locale.ENGLISH, "%s, %s seats, %s \n%s",
                    track.track.name, track.track.capacity, track.track.description, DateUtil.formatDayOfYear(track.schedule.date))

        } else {
            sessionTrackLayout.visibility = View.GONE
        }
        return sessionView
    }

    private fun addSpeaker(speaker: Speaker): View {
        val speakerView = activity!!.layoutInflater.inflate(R.layout.session_speaker_info, rootView, false)
        val speakerName = speakerView.findViewById<View>(R.id.speakerName) as TextView
        val speakerDesc = speakerView.findViewById<View>(R.id.speakerDescription) as TextView
        val company = speakerView.findViewById<View>(R.id.company) as TextView
        val job = speakerView.findViewById<View>(R.id.job_title) as TextView
        val picture = speakerView.findViewById<View>(R.id.speakerPhoto) as ImageView

        speakerName.text = speaker.name
        company.text = speaker.company
        job.text = speaker.jobTitle
        speakerDesc.text = speaker.bio

        Glide.with(speakerView.context)
                .load(speaker.photoUrl)
                .apply(RequestOptions()
                        .placeholder(R.drawable.person_icon)
                        .centerCrop())
                //                .transition(withCrossFade(R.anim.fade_in))
                .into(picture)
        return speakerView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SPEAKER_ID, speakerId)
    }

    companion object {
        val SPEAKER_ID = "speakerId"

        fun newInstance(speakerName: String): SpeakerInfoFragment {
            val sessionInfoFragment = SpeakerInfoFragment()
            sessionInfoFragment.arguments = Bundle()
            sessionInfoFragment.arguments!!.putString(SPEAKER_ID, speakerName)
            return sessionInfoFragment
        }
    }
}
