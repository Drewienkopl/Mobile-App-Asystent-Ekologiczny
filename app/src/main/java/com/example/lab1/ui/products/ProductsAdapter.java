package com.example.lab1.ui.products;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lab1.R;
import com.example.lab1.data.Product;
import com.google.android.material.color.MaterialColors;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {


    private List<Product> productList;
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(Product product);
    }


    public ProductsAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }


    public void updateData(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }


    public List<Product> getProductList() {
        return productList;
    }


    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        int normalBg = MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorSurface);


        // fade-in animation
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(500).start();


        holder.textName.setText(product.getName() != null ? product.getName() : "");
        holder.textCategory.setText(product.getCategory() != null ? product.getCategory() : "");
        holder.textPrice.setText(
                holder.itemView.getContext().getString(R.string.product_price, product.getPrice())
        );
        holder.textExpiry.setText(
                holder.itemView.getContext().getString(R.string.product_expiry, product.getExpiryDate())
        );


        // Podświetlenie produktów po terminie
        if (product.getExpiryDate() != null && !product.getExpiryDate().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date expiry = sdf.parse(product.getExpiryDate());
                if (expiry != null && expiry.before(new Date())) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#FF0000"));
                } else {
                    holder.itemView.setBackgroundColor(normalBg);
                }
            } catch (ParseException e) {
                // jeżeli nie uda się sparsować daty — pozostaw białe tło
                holder.itemView.setBackgroundColor(normalBg);
            }
        } else {
            holder.itemView.setBackgroundColor(normalBg);
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(product);
        });
    }


    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textCategory, textPrice, textExpiry;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textCategory = itemView.findViewById(R.id.textCategory);
            textPrice = itemView.findViewById(R.id.textPrice);
            textExpiry = itemView.findViewById(R.id.textExpiry);
        }
    }
}
