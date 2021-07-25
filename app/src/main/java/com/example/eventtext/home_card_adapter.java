package com.example.eventtext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventtext.R;
import com.example.eventtext.home_cardModel;

import java.util.ArrayList;

public class home_card_adapter extends RecyclerView.Adapter<home_card_adapter.Viewholder> {

    private Context context;
    private ArrayList<home_cardModel> list;

    // Constructor
    public home_card_adapter(FragmentActivity context, ArrayList<home_cardModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public home_card_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull home_card_adapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        home_cardModel model = list.get(position);
        holder.card_title.setText("" + model.getCard_title());
        holder.card_number.setText(""+model.getCard_number());
        holder.card_message.setText(""+model.getCard_message());
        holder.card_date.setText(""+model.getCard_date());
        holder.card_time.setText(""+model.getCard_time());


    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return list.size();
    }

    // View holder class for initializing of 
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView  card_title,card_number,card_message,card_date,card_time;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            card_title = itemView.findViewById(R.id.home_card_title);
            card_number = itemView.findViewById(R.id.home_card_number);
            card_message = itemView.findViewById(R.id.home_card_message);
            card_date = itemView.findViewById(R.id.home_card_date);
            card_time = itemView.findViewById(R.id.home_card_time);

        }
    }


}