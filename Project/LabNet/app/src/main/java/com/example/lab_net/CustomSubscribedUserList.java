package com.example.lab_net;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom Listview for Subscribed Users.
 */
public class CustomSubscribedUserList extends ArrayAdapter<String> {

    private ArrayList<String> subscribers;
    private Context context;

    public CustomSubscribedUserList (Context context, ArrayList<String> subscribers){
        super(context,0, subscribers);
        this.subscribers = subscribers;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_subscribed_user, parent,false);
        }

        String subscriber = (String) subscribers.get(position);
        TextView titleText = view.findViewById(R.id.user_title);

        titleText.setText(subscriber);
        return view;

    }
}


