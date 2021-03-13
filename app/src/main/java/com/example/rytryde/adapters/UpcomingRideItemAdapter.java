package com.example.rytryde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.R;
import com.example.rytryde.data.model.Ride;

import java.util.List;

public class UpcomingRideItemAdapter extends RecyclerView.Adapter<UpcomingRideItemAdapter.ViewHolder> {

    public static CardView cardView;
    public Context mContext;
    public String mdatastoreStr;
    public List<Ride> mDataset;
    protected UpcomingRideItemAdapter.OnRideClickListener cRideListener;


    //int mode = 1;

    public UpcomingRideItemAdapter(Context context, List<Ride> myDataset) {
        mDataset = myDataset;
        mContext = context;

    }

    public void swapData(List<Ride> mNewDataSet) {
        this.mDataset = mNewDataSet;
        notifyDataSetChanged();
    }

    public void SetOnRideClickListener(final UpcomingRideItemAdapter.OnRideClickListener mItemClickListener) {
        this.cRideListener = mItemClickListener;
    }

    @Override
    public UpcomingRideItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Ride ride = mDataset.get(position);


        holder.dateTV.setText(mDataset.get(position).getRide_date());
        holder.startingPointTV.setText(mDataset.get(position).getPick_up_address());
        holder.endingPointTV.setText(mDataset.get(position).getDrop_off_address());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cRideListener.onRideClick(view, ride, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface OnRideClickListener {
        void onRideClick(View view, Ride student, int position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView dateTV;
        public TextView statusTV;
        public TextView startingPointTV;
        public TextView endingPointTV;
        public CardView cardView;
        public TextView riderTV;
        public TextView rideStatusTV;


        public ViewHolder(View v) {
            super(v);
            dateTV = (TextView) v.findViewById(R.id.dateLabel);
            statusTV = (TextView) v.findViewById(R.id.statusLabel);
            startingPointTV = (TextView) v.findViewById(R.id.startingPointLabel);
            endingPointTV = (TextView) v.findViewById(R.id.endingPointLabel);
            cardView = (CardView) v.findViewById(R.id.rideCardView);
            riderTV = (TextView) v.findViewById(R.id.riderLabel);
            rideStatusTV = (TextView) v.findViewById(R.id.ridestatusLabel);
        }

    }

}
