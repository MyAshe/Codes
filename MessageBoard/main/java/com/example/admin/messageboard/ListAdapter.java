package com.example.admin.messageboard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class ListAdapter extends BaseAdapter{

    private List<String> list;
    private Context context;

    public ListAdapter(List<String> list, Context context){

        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);

        View view = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        TextView textView = (TextView)view.findViewById(R.id.listItem);
        textView.setText(list.get(position));
        textView.setTextColor(Color.rgb(r,g,b));
        return view;
    }
}
