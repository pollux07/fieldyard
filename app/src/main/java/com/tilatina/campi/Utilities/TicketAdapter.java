package com.tilatina.campi.Utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tilatina.campi.R;
import com.tilatina.campi.SparePartsActivity;

import java.io.InputStream;
import java.util.List;

/**
 * Derechos resevados tilatina.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.GuardHolder>{
    private List<TicketObjects> ticketStatus;
    private Context mCtx;

    class GuardHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeTask;
        RelativeLayout relativeParts;
        TextView tv_date;
        TextView tv_detail;
        ImageView photo;
        ImageView ivSpareParts;

        GuardHolder(View view) {
            super(view);

            relativeTask = (RelativeLayout) view.findViewById(R.id.linear_attach);
            relativeParts = (RelativeLayout) view.findViewById(R.id.relative_parts);
            tv_date = (TextView) view.findViewById(R.id.ticket_date);
            tv_detail = (TextView) view.findViewById(R.id.task);
            photo = (ImageView) view.findViewById(R.id.attachment_in_task);
            ivSpareParts = (ImageView) view.findViewById(R.id.image_can_add);
        }
    }

    public TicketAdapter(List<TicketObjects> ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    @Override
    public TicketAdapter.GuardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_task, parent, false);
        mCtx = parent.getContext();

        return new TicketAdapter.GuardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TicketAdapter.GuardHolder holder, int position) {
        TicketObjects ticketObject = ticketStatus.get(position);
        holder.tv_date.setText(ticketObject.getDate());
        holder.tv_detail.setText(ticketObject.getTask());
        final String eventId = ticketObject.getEventId();
        final String elementType = String.valueOf(ticketObject.getElementType());
        final String eventCadAdd = ticketObject.getEventCanAdd();

        holder.ivSpareParts.setVisibility(View.GONE);

        if (ticketObject.getLinkPhoto() == null){
            holder.photo.setVisibility(View.GONE);
        } else {
            holder.photo.setVisibility(View.VISIBLE);
            final String linkPhoto = ticketObject.getLinkPhoto();
            holder.relativeTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewAttachedPhoto(linkPhoto);
                }
            });
        }

        if (Integer.parseInt(eventId) == -1 || Integer.parseInt(eventCadAdd) == 0) {
            holder.tv_detail.setTextColor(ContextCompat.getColor(mCtx, R.color.blackString));
            holder.tv_detail.setClickable(false);
        } else {
            holder.tv_detail.setTextColor(ContextCompat.getColor(mCtx, R.color.blueDark));
            holder.relativeParts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent partsIntent = new Intent(mCtx, SparePartsActivity.class);
                    partsIntent.putExtra(SparePartsActivity.EVENT_ID, eventId);
                    partsIntent.putExtra(SparePartsActivity.ELEMENT_TYPE, elementType);
                    mCtx.startActivity(partsIntent);
                }
            });

            holder.ivSpareParts.setVisibility(View.VISIBLE);
        }
    }

    private void viewAttachedPhoto(final String linkPhoto) {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(mCtx);
        alerBuilder.setMessage("Archivo adjunto");
        alerBuilder.setView(R.layout.attached_photo);
        alerBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = alerBuilder.create();
        dialog.show();
        new DownloadImageTask((ImageView) dialog.findViewById(R.id.image_photo))
                    .execute(linkPhoto);


        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public int getItemCount() {
        return ticketStatus.size();
    }

}
