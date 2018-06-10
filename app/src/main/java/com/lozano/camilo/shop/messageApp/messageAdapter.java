package com.lozano.camilo.shop.messageApp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lozano.camilo.shop.R;

import java.util.ArrayList;
import java.util.List;


public class messageAdapter extends BaseAdapter {
    private Context context;
    private List<Message> elements;;
    TextView msgSend;
    TextView nameSendMsg;
    TextView hourMsg;



    public messageAdapter(Context context, List<Message> list) {
        this.context = context;
        this.elements = list;
    }

    @Override
    public int getCount() {

        return elements.size();
    }

    @Override
    public Message getItem(int position) {
        return elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = convertView;

        if (convertView == null) {

        }

        if (elements.get(position).typemsg) {
            view = inflater.inflate(R.layout.left, null);
        } else {
            view = inflater.inflate(R.layout.right, null);
        }

        msgSend = (TextView) view.findViewById(R.id.msgSend);
        nameSendMsg = (TextView) view.findViewById(R.id.nameSendMsg);
        hourMsg = (TextView) view.findViewById(R.id.hourMsg);


        msgSend.setText(elements.get(position).getMsg());
        nameSendMsg.setText(elements.get(position).getNameMsg());
        hourMsg.setText(elements.get(position).getHour());




//https://trinitytuts.com/simple-chat-application-using-listview-in-android/

        return view;

    }

}