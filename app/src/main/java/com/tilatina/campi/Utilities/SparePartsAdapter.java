package com.tilatina.campi.Utilities;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tilatina.campi.R;

import java.util.ArrayList;

/**
 * Derechos reservados tilatina.
 */

public class SparePartsAdapter extends RecyclerView.Adapter<SparePartsAdapter.MyViewHolder>{
    public ArrayList<SparePartsObjects> partsList = new ArrayList<>();
    public ArrayList<SparePartsObjects> selectedPartsList = new ArrayList<>();
    private Context mCtx;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_part_name;
        LinearLayout linearPartsName;

        MyViewHolder(View view) {
            super(view);
            tv_part_name = (TextView) view.findViewById(R.id.part_name);
            linearPartsName = (LinearLayout) view.findViewById(R.id.linear_parts);
        }
    }

    public SparePartsAdapter(Context context,
                             ArrayList<SparePartsObjects> partsList,
                             ArrayList<SparePartsObjects> selectedParts) {
        this.mCtx = context;
        this.partsList = partsList;
        this.selectedPartsList = selectedParts;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.added_parts_details, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final SparePartsObjects partsObject = partsList.get(position);
        holder.tv_part_name.setText(partsObject.getSparePartsName());

        if (selectedPartsList.contains(partsList.get(position))) {
            holder.linearPartsName.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.colorAccent));
        } else {
            holder.linearPartsName.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return partsList.size();
    }
}
