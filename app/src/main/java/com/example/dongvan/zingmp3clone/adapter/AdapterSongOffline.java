package com.example.dongvan.zingmp3clone.adapter;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dongvan.zingmp3clone.R;
import com.example.dongvan.zingmp3clone.model.AudioSong;

/**
 * Created by VoNga on 29-Apr-17.
 */

public class AdapterSongOffline extends ArrayAdapter {

    Activity context;
    int resource;

    public AdapterSongOffline(@NonNull Activity context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = context.getLayoutInflater().inflate(R.layout.item_song_offline,null);

        TextView tenbaihat = (TextView) convertView.findViewById(R.id.tenbaihat);
        TextView casi = (TextView) convertView.findViewById(R.id.casi);

        final AudioSong s = (AudioSong) getItem(position);
        tenbaihat.setText(s.getTitle()+"");
        casi.setText(s.getArtist()+"");
        return convertView;
    }
}
