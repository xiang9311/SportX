package com.xiang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xiang.Util.CardUtil;
import com.xiang.proto.nano.Common;
import com.xiang.sportx.R;

import java.util.List;

/**
 * Created by 祥祥 on 2016/4/28.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context context;
    private List<Common.GymCard> gymCards;
    private RecyclerView recyclerView;

    public CardAdapter(Context context, List<Common.GymCard> gymCards, RecyclerView recyclerView) {
        this.context = context;
        this.gymCards = gymCards;
        this.recyclerView = recyclerView;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem_gym_card , parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Common.GymCard card = gymCards.get(position);

        holder.tv_card_price.setText(card.price + "元");
        holder.tv_card_name.setText(CardUtil.getCardName(card.cardType));
        holder.b_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "购买" + card.price, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return gymCards.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_card_name;
        private TextView tv_card_price;
        private TextView b_buy;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_card_name = (TextView) itemView.findViewById(R.id.tv_card_name);
            tv_card_price = (TextView) itemView.findViewById(R.id.tv_card_price);
            b_buy = (TextView) itemView.findViewById(R.id.b_buy);
        }
    }
}
