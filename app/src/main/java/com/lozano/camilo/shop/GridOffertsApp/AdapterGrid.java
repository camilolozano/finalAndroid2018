package com.lozano.camilo.shop.GridOffertsApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lozano.camilo.shop.R;
import com.lozano.camilo.shop.RecyclerApp.Item_products;
import com.squareup.picasso.Picasso;


import java.util.List;


public class AdapterGrid extends BaseAdapter {
    private Context context;
    private List<Item_products> elements;
    TextView nameOfert;
    TextView priceOfert;
    TextView descriptionOfert;
    ImageView imgGrid;


    public AdapterGrid(Context context, List<Item_products> elements) {
        this.context = context;
        this.elements = elements;

    }

    @Override
    public int getCount() {
        return elements.size();
    }

    @Override
    public Item_products getItem(int position) {
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
            view = inflater.inflate(R.layout.item_grid, null);
        }

        imgGrid = (ImageView) view.findViewById(R.id.imgItemGrid);
        nameOfert = (TextView) view.findViewById(R.id.titleItemGrid);
        priceOfert = (TextView) view.findViewById(R.id.priceItemGrid);
        descriptionOfert = (TextView) view.findViewById(R.id.descriptionOfertItemGrid);

        nameOfert.setText(getItem(position).getNameP());
        priceOfert.setText(getItem(position).getPriceP());
        descriptionOfert.setText(getItem(position).getDescriptionP());

        if (getItem(position).getUrlImg().equals("")) {
            imgGrid.setImageResource(R.drawable.oferts);
        } else {
            Picasso.with(context.getApplicationContext()).load(getItem(position).getUrlImg()).resize(82, 75).placeholder(R.drawable.oferts).into(imgGrid);
        }

        return view;

    }
}
