package com.example.chriseze.spo.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chriseze.spo.Models.EnergyInfo;
import com.example.chriseze.spo.R;

import java.util.List;

/**
 * Created by CHRIS EZE on 6/30/2018.
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private Context context;
    private List<EnergyInfo> data;
    private Typeface font;

    public LogAdapter(Context context, List<EnergyInfo> data, Typeface font){
        this.context = context;
        this.data = data;
        this.font = font;
    }

    @Override
    public LogAdapter.LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.log_layout, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogAdapter.LogViewHolder holder, int position) {
        EnergyInfo energyInfo = data.get(position);
        holder.tvEnergyVal.setText(energyInfo.getEnergy_val());
        holder.tvTimestamp.setText(energyInfo.getTimestamp());

        holder.tvEnergyVal.setTypeface(font);
        holder.tvTimestamp.setTypeface(font);
    }

    @Override
    public int getItemCount() {
        return data == null? 0: data.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEnergyVal, tvTimestamp;

        public LogViewHolder(View itemView) {
            super(itemView);

            tvEnergyVal = (TextView)itemView.findViewById(R.id.energy_value);
            tvTimestamp = (TextView)itemView.findViewById(R.id.timestamp);
        }
    }
}
