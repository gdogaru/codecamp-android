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

package com.gdogaru.codecamp.view.di

import com.gdogaru.codecamp.view.MainActivity
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
import com.gdogaru.codecamp.view.splash.SplashActivity
import com.gdogaru.codecamp.view.sponsors.SponsorsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivitiesModule {

    @ContributesAndroidInjector
    fun contributeSplashScreenActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [(MainActivityBuildersModule::class)])
    fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    fun contributeSponsorsActivity(): SponsorsFragment
}

@Module
interface MainActivityBuildersModule {

    @ContributesAndroidInjector
    fun contributeSidebarFragment(): SidebarFragment

    @ContributesAndroidInjector
    fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    fun contributeAgendaFragment(): AgendaFragment

    @ContributesAndroidInjector
    fun sessionsListFragment(): SessionsListFragment

    @ContributesAndroidInjector
    fun calendarFragment(): CalendarFragment

    @ContributesAndroidInjector
    fun contributeSpeakersFragment(): SpeakersFragment

    @ContributesAndroidInjector
    fun contributeSpeakerExpandedFragment(): SpeakerExpandedFragment

    @ContributesAndroidInjector
    fun sessionInfoFragment(): SessionInfoFragment

    @ContributesAndroidInjector
    fun speakerInfoFragment(): SpeakerInfoFragment

    @ContributesAndroidInjector
    fun sessionExpandedFragment(): SessionExpandedFragment

}
