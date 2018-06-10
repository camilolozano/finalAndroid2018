package com.lozano.camilo.shop.RecyclerApp;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lozano.camilo.shop.ListViewApp.ItemCompany;
import com.lozano.camilo.shop.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private List<ItemCompany> company;
    Context context;
    int type;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView titleItem, descriptionItem;
        ImageView imgItem;
        ImageView PhotoCheck;
        ImageView checkMap;
        ImageView statusChat;

        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }

        public MyViewHolder(View view) {
            super(view);
            imgItem = (ImageView) itemView.findViewById(R.id.imgItem);
            titleItem = (TextView) itemView.findViewById(R.id.titleItem);
            descriptionItem = (TextView) itemView.findViewById(R.id.descriptionItem);
            PhotoCheck = (ImageView) itemView.findViewById(R.id.PhotoCheckChat);
            checkMap = (ImageView) itemView.findViewById(R.id.checkMap);
            statusChat = (ImageView) itemView.findViewById(R.id.statusChat);

        }
    }

    public RecyclerAdapter(Context context, List<ItemCompany> moviesList, int type) {
        this.context = context;
        this.company = moviesList;
        this.type = type;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler, parent, false);

        return new MyViewHolder(itemView);
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        ItemCompany values = company.get(position);
        if (values.getUrimgComp().equals("")) {
            holder.imgItem.setImageResource(R.drawable.company);
        } else {
            Picasso.with(context.getApplicationContext()).load(values.getUrimgComp()).placeholder(R.drawable.company).into(holder.imgItem);
        }

        if (values.isEstatusimgCheck()) {
            holder.statusChat.setImageResource(R.drawable.checkok);
        } else {
            holder.statusChat.setImageResource(R.drawable.check);
        }

        holder.titleItem.setText(values.getNameComp());
        holder.descriptionItem.setText(values.getSearchInfo()+" -  $"+values.getDescriptionComp());

    }

    @Override
    public int getItemCount() {
        return company.size();
    }

}
