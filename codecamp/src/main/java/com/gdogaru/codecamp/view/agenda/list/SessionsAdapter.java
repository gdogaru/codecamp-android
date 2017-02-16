package com.gdogaru.codecamp.view.agenda.list;

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
import java.util.Set;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


public class SessionsAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private final Set<String> favorites;
    private LayoutInflater mInflater;
    private List<SessionListItem> sessions;

    public SessionsAdapter(Context context, List<SessionListItem> sessions, Set<String> favorites) {
        super();
        this.favorites = favorites;
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
        return position;
    }

    public List<SessionListItem> getSessions() {
        return sessions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) view = convertView;
        else {
            view = mInflater.inflate(R.layout.agenda_sessions_list_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.title = (TextView) view.findViewById(R.id.sessionName);
            holder.time = (TextView) view.findViewById(R.id.sessionTime);
            holder.place = (TextView) view.findViewById(R.id.sessionPlace);
            holder.speaker = (TextView) view.findViewById(R.id.sessionSpeaker);
            holder.root = view.findViewById(R.id.root);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        SessionListItem session = sessions.get(position);
        holder.title.setText(session.getName());
        String timeString = DateUtil.formatPeriod(session.getStart(), session.getEnd());
        holder.time.setText(timeString);
        holder.place.setText(session.getTrackName());
        String speakerNames = StringUtils.join(session.getSpeakerNames(), ", ");
        holder.speaker.setText(speakerNames);
        if(favorites.contains(session.getId())){
            holder.root.setBackgroundResource(R.drawable.list_item_background_favorite);
        }else{
            holder.root.setBackgroundResource(R.drawable.list_item_background);
        }
        return view;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.agenda_sessions_list_header, parent, false);
            holder.text = (TextView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        SessionListItem session = sessions.get(position);
        String headerText = DateUtil.formatPeriod(session.getStart(), session.getEnd());
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        SessionListItem session = sessions.get(position);
        return DateUtil.formatPeriod(session.getStart(), session.getEnd()).hashCode();
//        return sessions.get(i).getStart().getTime();
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView title;
        TextView time;
        TextView place;
        TextView speaker;
        View root;
    }

}