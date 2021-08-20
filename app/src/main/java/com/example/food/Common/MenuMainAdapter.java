package com.example.food.Common;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MenuMainAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView itemName,itemName1;
        public ImageView itemPhoto,itemPhoto1;
        public ImageButton delete1, edit1;
        public ShimmerFrameLayout Try;
        public CardView cardView;
        public View ViewPost;
        CardView CrossAndEdit1;

        private ItemClickListener itemClickListener;

        public MenuMainAdapter(@NonNull View itemView) {
            super(itemView);

            ViewPost = itemView;

            //List for Customer Side
            itemName =(TextView) itemView.findViewById(R.id.itemName1);
            itemPhoto = (ImageView) itemView.findViewById(R.id.itemPhoto1);
            /*Try = itemView.findViewById(R.id.ListShimmerCat);*/
            cardView = itemView.findViewById(R.id.LayoutCat);

            //List for Admin Side
            CrossAndEdit1 = (CardView) itemView.findViewById(R.id.CrossAndEdit);
            itemPhoto1 = (ImageView) itemView.findViewById(R.id.itemPhoto12);
            itemName1 =(TextView) itemView.findViewById(R.id.itemName11);
            delete1 = (ImageButton) itemView.findViewById(R.id.delete_cat);
            edit1 = (ImageButton) itemView.findViewById(R.id.edit_cat);

            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
           itemClickListener.onClick(v,getAdapterPosition(),false);
        }

}




