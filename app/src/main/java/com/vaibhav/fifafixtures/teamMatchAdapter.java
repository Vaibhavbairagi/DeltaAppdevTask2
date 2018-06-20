package com.vaibhav.fifafixtures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class teamMatchAdapter extends RecyclerView.Adapter<teamMatchAdapter.teamlistHolder> {
    Context myContext;
    public teamMatchAdapter(Context context) {
        myContext=context;
    }

    @NonNull
    @Override
    public teamlistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listlayout, parent, false);
        return new teamMatchAdapter.teamlistHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull teamlistHolder holder, int position) {
        holder.mgroupname.setText(listTools.allCards.get(position).groupname);
        holder.mteam1.setText(listTools.allCards.get(position).team1);
        holder.mteam2.setText(listTools.allCards.get(position).team2);
        holder.Date.setText(listTools.allCards.get(position).cdate);
        holder.Time.setText(listTools.allCards.get(position).ctime);
        holder.Venue.setText(listTools.allCards.get(position).cvenue);
        byte[] b1=listTools.allCards.get(position).logo1;
        byte[] b2=listTools.allCards.get(position).logo2;
        Bitmap bmp1= BitmapFactory.decodeByteArray(b1,0,b1.length);
        holder.mlogo1.setImageBitmap(bmp1);
        Bitmap bmp2= BitmapFactory.decodeByteArray(b2,0,b2.length);
        holder.mlogo2.setImageBitmap(bmp2);
    }

    @Override
    public int getItemCount() {
        return listTools.allCards.size();
    }

    public static class teamlistHolder extends RecyclerView.ViewHolder {
        private ImageView mlogo1;
        private ImageView mlogo2;
        private TextView mgroupname;
        private TextView mteam1;
        private TextView mteam2;
        private TextView Date;
        private TextView Time;
        private TextView Venue;
        private teamlistHolder(View v){
            super(v);
            this.mlogo1= v.findViewById(R.id.team1image);
            this.mlogo2=v.findViewById(R.id.team2image);
            this.mgroupname=v.findViewById(R.id.GroupName);
            this.mteam1=v.findViewById(R.id.teamname1);
            this.mteam2=v.findViewById(R.id.teamname2);
            this.Date=v.findViewById(R.id.Date);
            this.Time=v.findViewById(R.id.Time);
            this.Venue=v.findViewById(R.id.Venue);
        }

    }
}
