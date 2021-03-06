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

package com.gdogaru.codecamp.view.home

import com.gdogaru.codecamp.api.model.Schedule
import com.gdogaru.codecamp.util.DateUtil

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */

abstract class MainViewItem(
    val title: String,
    val subtitle: String?
) {

    class AgendaItem(title: String, val index: Int, val schedule: Schedule) :
        MainViewItem(title, DateUtil.formatDayOfYear(schedule.date))

    class SpeakersItem(title: String) : MainViewItem(title, null)

    class SponsorsItem(title: String) : MainViewItem(title, null)
}
