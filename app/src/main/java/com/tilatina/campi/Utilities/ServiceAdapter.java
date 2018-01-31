package com.tilatina.campi.Utilities;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tilatina.campi.R;

import java.util.List;

/**
 * Derechos reservados tilatina.
 */
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.GuardHolder> {

    private List<ServiceObject> serviceStatuses;

    class GuardHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView ticketID;
        TextView ticketDetail;
        ImageView elementType;

        GuardHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            ticketID = (TextView) view.findViewById(R.id.no_ticket);
            ticketDetail = (TextView) view.findViewById(R.id.ticket_des);
            elementType = (ImageView) view.findViewById(R.id.stateIcon);
        }
    }

    public ServiceAdapter(List<ServiceObject> serviceStatuses) {
        this.serviceStatuses = serviceStatuses;
    }

    @Override
    public GuardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_row, parent, false);

        return new GuardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GuardHolder holder, int position) {
        ServiceObject serviceObject = serviceStatuses.get(position);
        if (serviceObject.getName().length() > 24) {
            String name = serviceObject.getName().substring(0, 25);
            holder.name.setText(String.format("%s...",name));
        } else {
            holder.name.setText(serviceObject.getName());
        }
        holder.ticketID.setText(String.format("#%s", serviceObject.getTicketID()));
        holder.ticketDetail.setText(serviceObject.getTicketDetail());

        int SITE = 6;
        int GPS = 4;
        if (serviceObject.getColor() == 'R') {
            if (serviceObject.getElementTypeId() == SITE) {
                holder.elementType.setBackgroundResource(R.drawable.ic_site_red);
            } else if (serviceObject.getElementTypeId() == GPS) {
                holder.elementType.setBackgroundResource(R.drawable.ic_gps_red);
            } else {
                holder.elementType.setBackgroundResource(R.drawable.ic_gm_red);
            }

        }
        if (serviceObject.getColor() == 'Y') {
            if (serviceObject.getElementTypeId() == SITE) {
                holder.elementType.setBackgroundResource(R.drawable.ic_site_yellow);
            } else if (serviceObject.getElementTypeId() == GPS) {
                holder.elementType.setBackgroundResource(R.drawable.ic_gps_yellow);
            } else {
                holder.elementType.setBackgroundResource(R.drawable.ic_gm_yellow);
            }
        }
        if (serviceObject.getColor() == 'G') {
            if (serviceObject.getElementTypeId() == SITE) {
                holder.elementType.setBackgroundResource(R.drawable.ic_site_green);
            } else if (serviceObject.getElementTypeId() == GPS) {
                holder.elementType.setBackgroundResource(R.drawable.ic_gps_green);
            } else {
                holder.elementType.setBackgroundResource(R.drawable.ic_gm_green);
            }
        }
    }

    @Override
    public int getItemCount() {
        return serviceStatuses.size();
    }
}