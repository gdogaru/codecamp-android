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

package com.gdogaru.codecamp.view.agenda.list

import org.threeten.bp.LocalTime

/**
 * @author Gabriel Dogaru (gdogaru@gmail.com)
 */
sealed class AgendaListItem {
    data class HeaderListItem(
        val text: String
    ) : AgendaListItem()

    data class SessionListItem(
        var id: String? = null,
        var name: String? = null,
        var start: LocalTime? = null,
        var end: LocalTime? = null,
        var trackName: String? = null,
        var speakerNames: List<String>? = null,
        var bookmarked: Boolean = false
    ) : AgendaListItem()
}
