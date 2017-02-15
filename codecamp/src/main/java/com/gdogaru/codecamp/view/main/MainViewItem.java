package com.gdogaru.codecamp.view.main;

import com.gdogaru.codecamp.model.Schedule;

/**
 * Created by Gabriel on 2/15/2017.
 */

public interface MainViewItem {

    class AgendaItem implements MainViewItem {
        private final Schedule schedule;

        public AgendaItem(Schedule schedule) {
            this.schedule = schedule;
        }

        public Schedule getSchedule() {
            return schedule;
        }
    }

    class SpeakersItem implements MainViewItem {
    }

    class SponsorsItem implements MainViewItem {
    }
}
