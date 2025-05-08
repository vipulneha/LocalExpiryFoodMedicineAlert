package com.techvipul.localexpiryfoodmedicinealert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class DeletedItemAdapter extends RecyclerView.Adapter<DeletedItemAdapter.ViewHolder> {

    private Context context;
    private List<DeletedItemModel> itemList;
    private OnRestoreClickListener restoreListener;
    private OnDeleteClickListener deleteListener;

    public interface OnRestoreClickListener {
        void onRestoreClick(DeletedItemModel item);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(DeletedItemModel item);
    }

    public DeletedItemAdapter(Context context, List<DeletedItemModel> itemList, OnRestoreClickListener restoreListener, OnDeleteClickListener deleteListener) {
        this.context = context;
        this.itemList = itemList;
        this.restoreListener = restoreListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.deleted_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeletedItemModel item = itemList.get(position);
        holder.textViewName.setText(item.getName());
        holder.textViewExpiryDate.setText(item.getExpiryDate());
        holder.textViewDescription.setText(item.getDescription() != null ? item.getDescription() : "");
        holder.textViewDeletedTimestamp.setText(item.getDeletedTimestamp());

        holder.buttonRestore.setOnClickListener(v -> restoreListener.onRestoreClick(item));
        holder.buttonDelete.setOnClickListener(v -> deleteListener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewExpiryDate, textViewDescription, textViewDeletedTimestamp;
        MaterialCardView cardView;
        Button buttonRestore, buttonDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewExpiryDate = itemView.findViewById(R.id.textViewExpiryDate);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDeletedTimestamp = itemView.findViewById(R.id.textViewDeletedTimestamp);
            cardView = itemView.findViewById(R.id.cardView);
            buttonRestore = itemView.findViewById(R.id.buttonRestore);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}