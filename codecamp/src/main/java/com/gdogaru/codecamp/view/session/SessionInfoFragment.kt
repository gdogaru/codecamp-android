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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdogaru.codecamp.R
import com.gdogaru.codecamp.api.model.Session
import com.gdogaru.codecamp.api.model.Speaker
import com.gdogaru.codecamp.api.model.Track
import com.gdogaru.codecamp.di.Injectable
import com.gdogaru.codecamp.util.DateUtil
import com.gdogaru.codecamp.view.BaseFragment
import com.gdogaru.codecamp.view.MainViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*
import javax.inject.Inject

class SessionInfoFragment : BaseFragment(), Injectable {
    @BindView(R.id.sessionTitle)
    lateinit var sessionTitle: TextView
    @BindView(R.id.sessionTime)
    lateinit var sessionTime: TextView
    @BindView(R.id.sessionDescription)
    lateinit var sessionDescription: TextView
    @BindView(R.id.sessionTrack)
    lateinit var sessionTrack: TextView
    @BindView(R.id.speakerLayout)
    lateinit var speakerLayout: ViewGroup
    @BindView(R.id.sessionTrackLayout)
    lateinit var sessionTrackLayout: LinearLayout
    @BindView(R.id.speakerLayoutOuter)
    lateinit var speakerLayoutOuter: ViewGroup

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    lateinit var sessionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
        sessionId = arguments!!.getString(SESSION_ID)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.session_expanded_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manage(ButterKnife.bind(this, view))

        viewModel.getSession(sessionId).observe(this, androidx.lifecycle.Observer { s ->
            s?.let { initView(it.session, it.speakers, it.track) }
        })
    }

    fun initView(session: Session, speakers: List<Speaker>, track: Track?) {

        sessionTitle.text = session.title;
        sessionDescription.text = session.description;
        val timeString = DateUtil.formatPeriod(session.startTime, session.endTime)
        sessionTime.text = timeString

        if (track != null) {
            sessionTrack.text = String.format(Locale.getDefault(), "%s, %s seats, %s", track.name, track.capacity, track.description)
        } else {
            sessionTrack.text = ""
        }

        speakers.forEach { addSpeaker(it) }

        val bundle = Bundle().also {
            it.putString(FirebaseAnalytics.Param.VALUE, session.title)
        }
        firebaseAnalytics.logEvent("session_view", bundle);
    }

    private fun addSpeaker(speaker: Speaker) {
        val speakerView = requireActivity().layoutInflater.inflate(R.layout.session_speaker_info, speakerLayout, false)
        val speakerName = speakerView.findViewById<View>(R.id.speakerName) as TextView
        val speakerDesc = speakerView.findViewById<View>(R.id.speakerDescription) as TextView
        val company = speakerView.findViewById<View>(R.id.company) as TextView
        val job = speakerView.findViewById<View>(R.id.job_title) as TextView
        val picture = speakerView.findViewById<View>(R.id.speakerPhoto) as ImageView

        speakerName.text = speaker.name
        company.text = speaker.company
        job.text = speaker.jobTitle
        speakerDesc.text = speaker.bio
        speakerLayout.addView(speakerView)

        Glide.with(requireActivity())
                .load(speaker.photoUrl)
                .apply(RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.person_icon))
                .into(picture)
    }

    companion object {

        const val SESSION_ID = "sessionId"

        fun newInstance(id: String): SessionInfoFragment {
            val sessionInfoFragment = SessionInfoFragment()
            sessionInfoFragment.arguments = Bundle()
            sessionInfoFragment.arguments!!.putString(SESSION_ID, id)
            return sessionInfoFragment
        }
    }
}
