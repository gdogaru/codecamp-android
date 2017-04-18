package com.gdogaru.codecamp.svc;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.gdogaru.codecamp.App;
import com.gdogaru.codecamp.model.Codecamp;
import com.gdogaru.codecamp.model.EventList;
import com.gdogaru.codecamp.model.EventSummary;
import com.gdogaru.codecamp.model.Schedule;
import com.gdogaru.codecamp.model.Session;
import com.gdogaru.codecamp.model.Speaker;
import com.gdogaru.codecamp.model.Track;
import com.gdogaru.codecamp.svc.jobs.DataLoadingEvent;
import com.gdogaru.codecamp.util.IOUtils;
import com.gdogaru.codecamp.util.Throwables;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

import static com.google.common.collect.Iterables.find;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
@Singleton
public class CodecampClient {
    private static final Logger LOG = getLogger(CodecampClient.class);
    @Inject
    Gson gson;
    @Inject
    App app;
    @Inject
    OkHttpClient client;
    @Inject
    AppPreferences appPreferences;
    @Inject
    EventBus eventBus;
    @Inject
    BookmarkingService bookmarkingService;
    private Codecamp currentCodecamp;
    private EventList eventList;

    @Inject
    public CodecampClient() {
    }

    public File download(String url, String root, String fileName) throws Exception {
        LOG.info("Downloading {} to {} {}", url, root, fileName);
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        File myDir = new File(app.getFilesDir(), root);
        myDir.mkdirs();
        File outputFile = new File(myDir, fileName);

        BufferedSink sink = Okio.buffer(Okio.sink(outputFile));
        // you can access body of response
        sink.writeAll(response.body().source());
        sink.close();
        return outputFile;
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public Codecamp getEvent() {
        if (currentCodecamp == null) { //todo
            loadCodecamp();
        }
        return currentCodecamp;
    }

    private void loadCodecamp() {
        eventList = readData("events.json", EventList.class);
        //try to load from preferences
        long id = 0;
        long pe = appPreferences.getActiveEvent();
        if (pe != 0) {
            for (EventSummary e : eventList) {
                if (e.getRefId() == pe) {
                    id = pe;
                    break;
                }
            }
        }
        //if no preferences load first
        if (id == 0 && eventList.size() > 0) {
            id = eventList.get(0).getRefId();
            appPreferences.setActiveEvent(id);
        }
        currentCodecamp = id == 0 ? null : readData("codecamp_" + id + ".json", Codecamp.class);

        for (Schedule schedule : currentCodecamp.getSchedules()) {
            Map<String, Integer> trackPositions = new HashMap<>();
            for (Track t : schedule.getTracks()) {
                trackPositions.put(t.getName(), t.getDisplayOrder());
            }
            trackPositions.put("", -1);
            trackPositions.put(null, -1);

            Collections.sort(schedule.getSessions(), new Comparator<Session>() {
                @Override
                public int compare(Session o1, Session o2) {
                    int result = o1.getStartTime().compareTo(o2.getStartTime());
                    if (result == 0) {
                        result = trackPositions.get(o1.getTrack()) - trackPositions.get(o2.getTrack());
                    }
                    return result;
                }
            });
        }
    }

    private <T> T readData(String s, Class<T> clazz) {
        if (appPreferences.getLastUpdated() == 0) {
            return readFromAssets(s, clazz);
        }
        try {
            return readFromStorage(s, clazz);
        } catch (Exception e) {
            LOG.error("Could not read from storage", e);
            return readFromAssets(s, clazz);
        }
    }

    private <T> T readFromStorage(String file, Class<T> clazz) throws Exception {
        long root = appPreferences.getLastUpdated();
        File myDir = new File(app.getFilesDir(), String.valueOf(root));
        File outputFile = new File(myDir, file);
        return gson.fromJson(new FileReader(outputFile), clazz);
    }

    private <T> T readFromAssets(String fileName, Class<T> clazz) {
        try {
            return gson.fromJson(IOUtils.toString(app.getAssets().open(fileName)), clazz);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public void fetchAllData() throws Exception {
        long now = System.currentTimeMillis();
        String root = String.valueOf(now);

        File file = download("https://connect.codecamp.ro/api/Conferences", root, "events.json");
        eventBus.post(new DataLoadingEvent(20));
        EventList eventList = gson.fromJson(IOUtils.toString(new FileReader(file)), EventList.class);
        int progress = 20;
        for (EventSummary es : eventList) {
            download("https://connect.codecamp.ro/api/Conferences/" + es.getRefId(), root, "codecamp_" + es.getRefId() + ".json");
            progress += 70 / eventList.size();
            eventBus.post(new DataLoadingEvent(progress));
        }

        if (eventList.size() > 0) {
            long prev = appPreferences.getLastUpdated();
            appPreferences.setLastUpdated(now);
            delete(new File(app.getFilesDir(), String.valueOf(prev)));
            removeExpiredPreferences();

            //set first
            Collections.sort(eventList, Ordering.natural().nullsLast().onResultOf(EventSummary::getStartDate));
            EventSummary es = Iterables.find(eventList, e -> e!= null && e.getStartDate() != null && !e.getStartDate().isBefore(LocalDateTime.now()), null);
            if (es != null) {
                appPreferences.setActiveEvent(es.getRefId());
            } else {
                appPreferences.setActiveEvent(eventList.get(0).getRefId());
            }
        } else {
            appPreferences.setActiveEvent(0L);
        }
    }

    private void removeExpiredPreferences() {
        Set<String> events = new HashSet<>();
        for (EventSummary e : getEventsSummary()) {
            events.add(e.getTitle());
        }
        bookmarkingService.keepOnlyEvents(events);
    }

    void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) delete(c);
        } else {
            if (!f.delete()) LOG.warn("Failed to delete file: " + f);
        }
    }

    public Schedule getSchedule() {
        return getEvent().getSchedules().get(appPreferences.getActiveSchedule());
    }

    public Session getSession(String id) {
        return find(getSchedule().getSessions(), new Predicate<Session>() {
            @Override
            public boolean apply(Session input) {
                return input.getId().equals(id);
            }
        });
    }

    public Speaker getSpeaker(String id) {
        return find(getEvent().getSpeakers(), input -> input.getName().equals(id));
    }

    @Nullable
    public Track getTrack(String track) {
        return find(getSchedule().getTracks(), input -> input.getName().equals(track), null);
    }

    public Pair<Track, Schedule> getTrackExtended(String track) {
        for (Schedule s : getEvent().getSchedules()) {
            Track t = find(s.getTracks(), input -> input.getName().equals(track), null);
            if (t != null) return new Pair<>(t, s);
        }
        return null;
    }

    public ArrayList<String> getTrackSesssionsIds(String trackId) {
        ArrayList<String> result = new ArrayList<>();
        List<Session> sessions = getSchedule().getSessions();
        for (int i = 0; i < sessions.size(); i++) {
            Session s = sessions.get(i);
            if (trackId == null || s.getTrack() == null || trackId.equals(s.getTrack())) {
                result.add(s.getId());
            }
        }
        return result;
    }

    public EventList getEventsSummary() {
        loadCodecamp();
        return eventList;
    }

    public void setActiveEvent(long id) {
        appPreferences.setActiveEvent(id);
        currentCodecamp = null;
    }

    public List<Pair<Codecamp, Session>> getSessionsBySpeaker(String speakerId) {
        Codecamp event = getEvent();
        List<Schedule> schedules = event.getSchedules();
        List<Pair<Codecamp, Session>> result = new ArrayList<>();
        for (Schedule schedule : schedules) {
            for (Session s : schedule.getSessions()) {
                if (s.getSpeakerIds() == null || s.getSpeakerIds().size() == 0) continue;
                for (String sp : s.getSpeakerIds()) {
                    if (sp != null && sp.equals(speakerId)) {
                        result.add(new Pair<>(event, s));
                        break;
                    }
                }
            }
        }
        return result;
    }
}

