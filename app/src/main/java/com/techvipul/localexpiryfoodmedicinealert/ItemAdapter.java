package com.techvipul.localexpiryfoodmedicinealert;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    private List<ItemModel> itemList;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(ItemModel item);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(ItemModel item);
    }

    public ItemAdapter(Context context, List<ItemModel> itemList, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemModel item = itemList.get(position);
        holder.textViewName.setText(item.getName());
        holder.textViewExpiryDate.setText(item.getExpiryDate());
        holder.textViewDescription.setText(item.getDescription() != null ? item.getDescription() : "");

        // Highlight items expiring within 3 days
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar expiryCal = Calendar.getInstance();
            expiryCal.setTime(sdf.parse(item.getExpiryDate()));
            Calendar todayCal = Calendar.getInstance();
            long diff = expiryCal.getTimeInMillis() - todayCal.getTimeInMillis();
            long days = diff / (1000 * 60 * 60 * 24);
            if (days <= 3) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFDE7")); // Light yellow background
                holder.cardView.setStrokeColor(Color.RED);
                holder.cardView.setStrokeWidth(2);
            } else {
                holder.cardView.setCardBackgroundColor(Color.WHITE);
                holder.cardView.setStrokeWidth(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> longClickListener.onItemLongClick(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewExpiryDate, textViewDescription;
        MaterialCardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewExpiryDate = itemView.findViewById(R.id.textViewExpiryDate);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}