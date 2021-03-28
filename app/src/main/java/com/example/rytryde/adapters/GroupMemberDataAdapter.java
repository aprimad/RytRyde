package com.example.rytryde.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.R;

public class GroupMemberDataAdapter extends RecyclerView.Adapter<GroupMemberDataAdapter.PlayerViewHolder> {

    public class PlayerViewHolder extends RecyclerView.ViewHolder {

        public PlayerViewHolder(View view) {
            super(view);

        }
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_member_items, parent, false);

        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }
}