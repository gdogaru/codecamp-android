/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdogaru.codecamp.view.di

import com.gdogaru.codecamp.view.LoadingDataActivity
import com.gdogaru.codecamp.view.SplashScreenActivity
import com.gdogaru.codecamp.view.SponsorsActivity
import com.gdogaru.codecamp.view.agenda.AgendaActivity
import com.gdogaru.codecamp.view.agenda.calendar.CalendarFragment
import com.gdogaru.codecamp.view.agenda.list.SessionsListFragment
import com.gdogaru.codecamp.view.main.MainActivity
import com.gdogaru.codecamp.view.main.SidebarFragment
import com.gdogaru.codecamp.view.session.SessionExpandedActivity
import com.gdogaru.codecamp.view.session.SessionInfoFragment
import com.gdogaru.codecamp.view.speaker.SpeakerExpandedActivity
import com.gdogaru.codecamp.view.speaker.SpeakerInfoFragment
import com.gdogaru.codecamp.view.speaker.SpeakersActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivitiesModule {

    @ContributesAndroidInjector
    fun contributeSplashScreenActivity(): SplashScreenActivity

    @ContributesAndroidInjector(modules = [(MainActivityBuildersModule::class)])
    fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [(AgendaActivityBuildersModule::class)])
    fun contributeAgendaActivity(): AgendaActivity

    @ContributesAndroidInjector(modules = [(SessionSpeakerBuildersModule::class)])
    fun contributeSessionExpandedActivity(): SessionExpandedActivity

    @ContributesAndroidInjector(modules = [(SessionSpeakerBuildersModule::class)])
    fun contributeSpeakersActivity(): SpeakersActivity

    @ContributesAndroidInjector(modules = [(SpeakerExpandedBuildersModule::class)])
    fun contributeSpeakerExpandedActivity(): SpeakerExpandedActivity

    @ContributesAndroidInjector()
    fun contributeSponsorsActivity(): SponsorsActivity

    @ContributesAndroidInjector()
    fun contributeLoadingDataActivity(): LoadingDataActivity

}

@Module
interface MainActivityBuildersModule {

    @ContributesAndroidInjector()
    fun contributeSidebarFragment(): SidebarFragment

}

@Module
interface AgendaActivityBuildersModule {
    @ContributesAndroidInjector()
    fun sessionsListFragment(): SessionsListFragment

    @ContributesAndroidInjector()
    fun calendarFragment(): CalendarFragment
}


@Module
interface SessionSpeakerBuildersModule {
    @ContributesAndroidInjector()
    fun sessionInfoFragment(): SessionInfoFragment

    @ContributesAndroidInjector()
    fun speakerInfoFragment(): SpeakerInfoFragment

}

@Module
interface SpeakerExpandedBuildersModule {
    @ContributesAndroidInjector()
    fun speakerInfoFragment(): SpeakerInfoFragment
}
