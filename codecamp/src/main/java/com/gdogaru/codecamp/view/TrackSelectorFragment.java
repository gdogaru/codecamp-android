/*
 * Copyright (C) 2008 Gabriel Dogaru (gdogaru@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gdogaru.codecamp.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.gdogaru.codecamp.R;
import com.gdogaru.codecamp.model.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel Dogaru (gdogaru@gmail.com)
 */
public class TrackSelectorFragment extends Fragment {

    TextView trackText;
    TrackSelectedListener trackSelectedListener;
    private Object popupWindow;
    private TrackAdapter tracksAdapter;
    private List<Track> trackList = new ArrayList<Track>();
    private TrackSelectorDialog trackDialog;
    private Long currentTrack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.track_selector_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUIComponents();
        init();
    }

    private void initUIComponents() {
        trackText = ((TextView) getView().findViewById(com.gdogaru.codecamp.R.id.trackText));
        if (trackText != null) {
            trackText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listTest();
                }
            });
        }
    }

    public void init() {
        tracksAdapter = new TrackAdapter(getActivity(), trackList);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 11) {
            initListPopup();
        } else {
            initSpinnerPopup();
        }

        //set selected tracks
        for (Track track : trackList) {
            if (track.getId() == currentTrack) {
                trackText.setText(getString(R.string.track_tell, track.getName()));
            }
        }
    }

//    public void initView(){
//        tracksAdapter = new TrackAdapter(activity, trackList);
//        if (popupWindow != null) {
//            ((ListPopupWindow) popupWindow).setAdapter(tracksAdapter);
//        } else {
//            initSpinnerPopup();
//        }
//        if (trackList.size() > 0) {
//            Track track = trackList.get(0);
//            String trackName = track == null || track.getId() == 0 ? getString(R.string.all_tracks) : track.getName();
//            trackText.setText(trackName);
//        }
//        setCurrentTrack(0L);
//    }

    private void initSpinnerPopup() {
        trackDialog = new TrackSelectorDialog(trackList);
        trackDialog.setDialogChangeListener(new DialogChangeListener() {
            @Override
            public void onDialogChange(int newInt) {
                Track track = trackList.get(newInt);
                if (trackSelectedListener != null) {
                    trackSelectedListener.onTrackSelected(track);
                }
                trackText.setText(getString(R.string.track_tell, track.getName()));
            }
        });
    }

    private void initListPopup() {
        ListPopupWindow localPopupWindow = new ListPopupWindow(getActivity());
        localPopupWindow.setAdapter(tracksAdapter);
        localPopupWindow.setAnchorView(trackText);
        popupWindow = localPopupWindow;
        localPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track item = (Track) tracksAdapter.getItem(position);
                if (trackSelectedListener != null) {
                    trackSelectedListener.onTrackSelected(item);
                }
                trackText.setText(getString(R.string.track_tell, item.getName()));
                ((ListPopupWindow) popupWindow).dismiss();
            }
        });
    }

    public void setTrackSelectedListener(TrackSelectedListener trackSelectedListener) {
        this.trackSelectedListener = trackSelectedListener;
    }

    public void listTest() {
        if (popupWindow != null) {
            ((ListPopupWindow) popupWindow).show();
        } else {
            trackDialog.show(getFragmentManager(), "trackDialog");
        }
    }

    public void setCurrentTrack(Long trackId) {
        if (trackId == null) {
            trackId = 0L;
        }
        this.currentTrack = trackId;
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }

    private String[] getTrackNames(List<Track> trackList) {
        String[] result = new String[trackList.size()];
        for (int i = 0; i < trackList.size(); i++) {
            result[i] = trackList.get(i).getName();
        }
        return result;
    }

    public interface TrackSelectedListener {
        void onTrackSelected(Track track);
    }

    public interface DialogChangeListener {
        void onDialogChange(int newInt);
    }

    public class TrackSelectorDialog extends DialogFragment {
        private List<Track> trackList= new ArrayList<Track>();
        int selectedId = 0;
        private DialogChangeListener dialogChangeListener;

        public TrackSelectorDialog() {
        }

        public TrackSelectorDialog(List<Track> trackList) {
            this.trackList = trackList;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String[] objects = getTrackNames(trackList);
            builder.setSingleChoiceItems(objects, selectedId,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedId = which;
                        }
                    })
                    .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            if (dialogChangeListener != null) {
                                dialogChangeListener.onDialogChange(selectedId);
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            return builder.create();
        }

        public void setDialogChangeListener(DialogChangeListener dialogChangeListener) {
            this.dialogChangeListener = dialogChangeListener;
        }
    }
}
