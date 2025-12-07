package com.example.lab1.ui.deposit;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.data.Deposit;


import java.util.ArrayList;
import java.util.List;


public class DepositAdapter extends RecyclerView.Adapter<DepositAdapter.ViewHolder> {
    private final Context context;
    private List<Deposit> deposits = new ArrayList<>();
    private final OnDepositActionListener listener;


    public interface OnDepositActionListener {
        void onEditDeposit(Deposit deposit);
        void onDeleteDeposit(Deposit deposit);
    }


    public DepositAdapter(Context context, OnDepositActionListener listener) {
        this.context = context;
        this.listener = listener;
    }


    public void setDeposits(List<Deposit> deposits) {
        this.deposits = deposits;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardDeposit;
        TextView tvType, tvValue, tvBarcode;
        ImageButton btnEdit, btnDelete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDeposit = itemView.findViewById(R.id.cardDeposit);
            tvType = itemView.findViewById(R.id.tvType);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvBarcode = itemView.findViewById(R.id.tvBarcode);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_deposit, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deposit d = deposits.get(position);
        holder.tvType.setText(d.getType());
        holder.tvValue.setText(String.format("Wartość: %.2f zł", d.getValue()));
        holder.tvBarcode.setText(d.getBarcode() != null ? "Kod: " + d.getBarcode() : "");


        //Ustawiwenie koloru tla
        if (d.isReturned()) {
            int backgroundColor = ContextCompat.getColor(context, R.color.deposit_returned_bg);
            int textColor = ContextCompat.getColor(context, R.color.deposit_returned_text);
            holder.cardDeposit.setCardBackgroundColor(backgroundColor);
            holder.tvType.setTextColor(textColor);
            holder.tvValue.setTextColor(textColor);
            holder.tvBarcode.setTextColor(textColor);
            holder.itemView.setAlpha(0.95f);
        } else {
            int defaultBackgroundColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_background);
            int defaultTextColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_on_background);
            holder.cardDeposit.setCardBackgroundColor(defaultBackgroundColor);
            holder.tvType.setTextColor(defaultTextColor);
            holder.tvValue.setTextColor(defaultTextColor);
            holder.tvBarcode.setTextColor(defaultTextColor);
            holder.itemView.setAlpha(1.0f);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditDeposit(d);
        });


        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteDeposit(d);
        });

        //uruchomienie animacji slide-in
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
        holder.itemView.startAnimation(animation);

    }


    @Override
    public int getItemCount() {
        return deposits.size();
    }
}




