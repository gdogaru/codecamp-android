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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gdogaru.codecamp.view.MainViewModel
import com.gdogaru.codecamp.view.agenda.AgendaViewModel
import com.gdogaru.codecamp.view.agenda.calendar.CalendarFragmentViewModel
import com.gdogaru.codecamp.view.agenda.list.SessionsListViewModel
import com.gdogaru.codecamp.view.home.HomeViewModel
import com.gdogaru.codecamp.view.home.SidebarViewModel
import com.gdogaru.codecamp.view.session.SessionExpandedViewModel
import com.gdogaru.codecamp.view.session.SessionInfoViewModel
import com.gdogaru.codecamp.view.speaker.SpeakerInfoViewModel
import com.gdogaru.codecamp.view.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SidebarViewModel::class)
    fun bindSidebarViewModel(viewModel: SidebarViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SessionsListViewModel::class)
    fun bindSessionsListViewModel(viewModel: SessionsListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CalendarFragmentViewModel::class)
    fun bindCalendarFragmentViewModel(viewModel: CalendarFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AgendaViewModel::class)
    fun bindAgendaViewModel(viewModel: AgendaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SessionExpandedViewModel::class)
    fun bindSessionExpandedViewModel(viewModel: SessionExpandedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SessionInfoViewModel::class)
    fun bindSessionInfoViewModel(viewModel: SessionInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SpeakerInfoViewModel::class)
    fun bindSpeakerInfoViewModel(viewModel: SpeakerInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(viewModel: MainViewModel): ViewModel


    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
