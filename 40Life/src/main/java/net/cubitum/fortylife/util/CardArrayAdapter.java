package net.cubitum.fortylife.util;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import net.cubitum.fortylife.R;
import net.cubitum.fortylife.views.CardView;

import java.util.List;


public class CardArrayAdapter extends ArrayAdapter<Pair<String,String>> {
    private final Context context;
    private final List<Pair<String,String>> values;

    public CardArrayAdapter(Context context, List<Pair<String,String>> values) {
        super(context, R.layout.listitem_searchresult, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listitem_searchresult, parent, false);

        CardView cardView = (CardView) rowView.findViewById(R.id.view);
        Pair<String,String> data = values.get(position);
        cardView.setCardImage(data.first);
        cardView.setCardName(data.second);
        return rowView;
    }




}