package com.android.health.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.health.MainActivity;
import com.android.health.R;
import com.android.health.data.Dashboard;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardAdapterViewHolder> {

    DashboardOnclickHandler dashboardOnclickHandler;
    private ArrayList<Dashboard> pictures;

    public DashboardAdapter(DashboardOnclickHandler handler,ArrayList<Dashboard> pics){
        this.dashboardOnclickHandler = handler;
        this.pictures = pics;
    }

    @NonNull
    @Override
    public DashboardAdapter.DashboardAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_list_card,parent,false);

        return new DashboardAdapterViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAdapter.DashboardAdapterViewHolder holder, int position) {
        holder.mImageView.setImageResource(pictures.get(position).getPicturesId());
        holder.mTextView.setText(pictures.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public interface DashboardOnclickHandler{
        void dashboardOnclick(int position);
    }

    public class DashboardAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView mImageView;
        private final TextView mTextView;

        public DashboardAdapterViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image);
            mTextView = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            dashboardOnclickHandler.dashboardOnclick(position);
        }
    }

}
