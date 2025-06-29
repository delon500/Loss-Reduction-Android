package com.example.lossreductionsystemandroid;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.AssetViewHolder> {

    public interface OnAssetActionClickListener{
        void onAssetActionClicked(Asset asset);
    }

    private final Context context;
    private final List<Asset> assetList;
    private final OnAssetActionClickListener actionListener;

    public AssetAdapter(Context context, List<Asset> assetList, OnAssetActionClickListener listener){
        this.context = context;
        this.assetList = assetList;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_asset, parent, false);
        return new AssetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        Asset current = assetList.get(position);

        // 1) Load asset image with Glide
        String imageUrl = current.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()){
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_asset)
                    .into(holder.imageAsset);
        } else {
            holder.imageAsset.setImageResource(R.drawable.placeholder_asset);
        }

        //Status Chip
        if(current.statusLabel != null){
            holder.chipStatus.setText(current.statusLabel);
            holder.chipStatus.setVisibility(View.VISIBLE);

            // Tint background if a color was supplied
            if(current.statusColor != null && !current.statusColor.isEmpty()){
                try {
                    int color = Color.parseColor(current.statusColor);
                    holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(color));
                } catch (IllegalArgumentException ignore){

                }
            }
        } else {
            holder.chipStatus.setVisibility(View.GONE);
        }

        // 2) Asset name
        holder.assetName.setText(current.name);
        // 3) Category subtitle
        holder.assetCategory.setText(current.category);
        // 4) Quantity
        holder.quantityChip.setText(String.valueOf(current.quantity));

        holder.buttonAction.setOnClickListener(v -> {
            if(actionListener != null){
                actionListener.onAssetActionClicked(current);
            }
        });
    }

    @Override
    public int getItemCount() {
       return assetList.size();
    }

    static class AssetViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView imageAsset;
        TextView assetName, assetCategory;
        Chip quantityChip, chipStatus;
        MaterialButton buttonAction;

        AssetViewHolder(View itemsView){
            super(itemsView);
            imageAsset = itemsView.findViewById(R.id.imageAsset);
            assetName = itemsView.findViewById(R.id.textAssetName);
            assetCategory = itemsView.findViewById(R.id.textAssetCategory);
            quantityChip = itemsView.findViewById(R.id.quantityChip);
            chipStatus = itemsView.findViewById(R.id.chipStatus);
            buttonAction = itemsView.findViewById(R.id.buttonAction);
        }
    }
}
