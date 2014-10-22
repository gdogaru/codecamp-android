package com.gdogaru.codecamp.view.sessions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.util.DateUtil;
import com.gdogaru.codecamp.util.StringUtils;

import java.util.Collections;
import java.util.List;


public class SessionsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SessionListItem> sessions;

    public SessionsAdapter(Context context, List<SessionListItem> sessions) {
        super();
        Collections.sort(sessions, SessionListItem.SESSION_BY_DATE_COMPARATOR);
        this.sessions = sessions;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    @Override
    public Object getItem(int position) {
        return sessions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return sessions.get(position).getId();
    }

    public List<SessionListItem> getSessions() {
        return sessions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : mInflater.inflate(R.layout.session_list_item, parent, false);
        TextView title = (TextView) view.findViewById(R.id.sessionName);
        TextView time = (TextView) view.findViewById(R.id.sessionTime);
        TextView place = (TextView) view.findViewById(R.id.sessionPlace);
        TextView speaker = (TextView) view.findViewById(R.id.sessionSpeaker);
        SessionListItem session = sessions.get(position);
        title.setText(session.getName());
        String timeString = DateUtil.formatPeriod(session.getStart(), session.getEnd());
        time.setText(timeString);
        place.setText(session.getTrackName());
        String speakerNames = StringUtils.join(session.getSpeakerNames(), ", ");
        speaker.setText(speakerNames);
        return view;
    }
}
