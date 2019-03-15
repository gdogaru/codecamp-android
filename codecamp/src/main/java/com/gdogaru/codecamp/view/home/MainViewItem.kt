package com.gdogaru.codecamp.view.home

import com.gdogaru.codecamp.api.model.Schedule

/**
 * Created by Gabriel on 2/15/2017.
 */

interface MainViewItem {

    data class AgendaItem(val index: Int, val schedule: Schedule) : MainViewItem

    class SpeakersItem : MainViewItem

    class SponsorsItem : MainViewItem
}
