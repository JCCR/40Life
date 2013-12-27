package net.cubitum.fortylife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


import net.cubitum.fortylife.views.CardView;

import java.util.List;


public class CardArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public CardArrayAdapter(Context context, List<String> values) {
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
        cardView.setCardImage(values.get(position));

        return rowView;
    }




}