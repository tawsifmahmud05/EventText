package com.example.eventtext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class profile_card_adapter extends RecyclerView.Adapter<profile_card_adapter.Viewholder> {

    private Context context;
    private ArrayList<home_cardModel> list;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    // Constructor
    public profile_card_adapter(FragmentActivity context, ArrayList<home_cardModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public profile_card_adapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_card_layout, parent, false);
        return new Viewholder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        home_cardModel model = list.get(position);
        holder.card_title.setText("" + model.getCard_title());
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

        private TextView category_name, card_title,card_message,card_date,card_time;

        public Viewholder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            card_title = itemView.findViewById(R.id.prof_card_title);
            card_date = itemView.findViewById(R.id.prof_card_date);
            card_time = itemView.findViewById(R.id.prof_card_time);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if(listener !=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}