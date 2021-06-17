package com.monta.cozy.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.monta.cozy.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListPopupView<T> extends FrameLayout {

    private RecyclerView.Adapter<ItemViewHolder> adapter;

    private final List<T> data = new ArrayList<>();

    private OnItemClickListener<T> listener;

    public ListPopupView(Context context) {
        super(context);
        init(context);
    }

    public ListPopupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListPopupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ListPopupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View root = inflate(context, R.layout.layout_popup_list, this);
        RecyclerView rcvItem = root.findViewById(R.id.rcv_item);

        adapter = new RecyclerView.Adapter<ItemViewHolder>() {
            @NonNull
            @NotNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_popup, parent, false);
                ItemViewHolder holder = new ItemViewHolder(itemView);
                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(data.get(holder.getAdapterPosition()));
                    }
                });
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull ListPopupView.ItemViewHolder holder, int position) {
                holder.tvTitle.setText(listener.getTitle(data.get(position)));
            }

            @Override
            public int getItemCount() {
                return data.size();
            }
        };

        rcvItem.setAdapter(adapter);
    }

    public void setData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
        adapter.notifyDataSetChanged();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        public ItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
        }
    }

    public interface OnItemClickListener<T> {
        String getTitle(T item);

        void onItemClick(T item);
    }

    public void setListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }
}
