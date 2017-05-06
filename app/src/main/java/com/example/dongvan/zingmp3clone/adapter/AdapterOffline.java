package com.example.dongvan.zingmp3clone.adapter;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dongvan.zingmp3clone.R;
import com.example.dongvan.zingmp3clone.model.model_off;

/**
 * Created by VoNga on 28-Apr-17.
 */

public class AdapterOffline extends ArrayAdapter {

    Activity context;
    int resource;

    public AdapterOffline(@NonNull Activity context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= context.getLayoutInflater();
        convertView=inflater.inflate(R.layout.item_offline, null);

        TextView txtNameOff=(TextView) convertView.findViewById(R.id.name_off);
        TextView txtCountOff=(TextView) convertView.findViewById(R.id.count_off);

        final model_off m= (model_off) getItem(position);
        txtNameOff.setText(m.getTen());
        txtCountOff.setText(m.getSoluong());
        return convertView;
    }


}
