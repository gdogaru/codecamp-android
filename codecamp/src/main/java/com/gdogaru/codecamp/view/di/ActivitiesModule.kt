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
import com.gdogaru.codecamp.view.MainActivity
import com.gdogaru.codecamp.view.SplashScreenActivity
import com.gdogaru.codecamp.view.agenda.AgendaFragment
import com.gdogaru.codecamp.view.agenda.calendar.CalendarFragment
import com.gdogaru.codecamp.view.agenda.list.SessionsListFragment
import com.gdogaru.codecamp.view.home.HomeFragment
import com.gdogaru.codecamp.view.home.SidebarFragment
import com.gdogaru.codecamp.view.session.SessionExpandedFragment
import com.gdogaru.codecamp.view.session.SessionInfoFragment
import com.gdogaru.codecamp.view.speaker.SpeakerExpandedFragment
import com.gdogaru.codecamp.view.speaker.SpeakerInfoFragment
import com.gdogaru.codecamp.view.speaker.SpeakersFragment
import com.gdogaru.codecamp.view.sponsors.SponsorsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivitiesModule {

    @ContributesAndroidInjector
    fun contributeSplashScreenActivity(): SplashScreenActivity

    @ContributesAndroidInjector(modules = [(MainActivityBuildersModule::class)])
    fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    fun contributeSponsorsActivity(): SponsorsFragment

    @ContributesAndroidInjector()
    fun contributeLoadingDataActivity(): LoadingDataActivity

}

@Module
interface MainActivityBuildersModule {

    @ContributesAndroidInjector
    fun contributeSidebarFragment(): SidebarFragment

    @ContributesAndroidInjector
    fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    fun contributeAgendaFragment(): AgendaFragment

    @ContributesAndroidInjector()
    fun sessionsListFragment(): SessionsListFragment

    @ContributesAndroidInjector()
    fun calendarFragment(): CalendarFragment

    @ContributesAndroidInjector
    fun contributeSpeakersFragment(): SpeakersFragment

    @ContributesAndroidInjector
    fun contributeSpeakerExpandedFragment(): SpeakerExpandedFragment

    @ContributesAndroidInjector()
    fun sessionInfoFragment(): SessionInfoFragment

    @ContributesAndroidInjector()
    fun speakerInfoFragment(): SpeakerInfoFragment

    @ContributesAndroidInjector()
    fun aessionExpandedFragment(): SessionExpandedFragment

}
