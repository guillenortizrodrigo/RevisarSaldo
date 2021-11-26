package com.innova.checarsaldoapp.adaptador;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.innova.checarsaldoapp.R;
import com.innova.checarsaldoapp.model.SquareItem;

import java.util.List;

public class SquareItemAdapter extends RecyclerView.Adapter<SquareItemAdapter.ViewHolder> {
    private List<SquareItem> lstProductCategory;

    private OnRecyclerItemListener<SquareItem> onRecyclerItemListener;

    public SquareItemAdapter(List<SquareItem> paramList, OnRecyclerItemListener<SquareItem> paramOnRecyclerItemListener) {
        this.lstProductCategory = paramList;
        this.onRecyclerItemListener = paramOnRecyclerItemListener;
    }

    public int getItemCount() {
        return (this.lstProductCategory != null) ? this.lstProductCategory.size() : 0;
    }

    public void onBindViewHolder(ViewHolder paramViewHolder, int paramInt) {
        paramViewHolder.updateItem(this.lstProductCategory.get(paramInt));
    }

    public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_square, paramViewGroup, false), this.onRecyclerItemListener);
    }

    public void setLstProductCategory(List<SquareItem> paramList) {
        this.lstProductCategory = paramList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView availableStatus;

        private ImageView ivProduct;

        private OnRecyclerItemListener listener;

        private CardView mainView;

        private RelativeLayout rlContainer;

        private TextView tvQty;

        private TextView tvTitle;

        public ViewHolder(View param1View, OnRecyclerItemListener param1OnRecyclerItemListener) {
            super(param1View);
            this.mainView = (CardView)param1View;
            this.rlContainer = (RelativeLayout)param1View.findViewById(R.id.rlProductCatalogContainer);
            this.ivProduct = (ImageView)param1View.findViewById(R.id.ivProductCatalog);
            this.tvTitle = (TextView)param1View.findViewById(R.id.tvProductCatalogTitle);
            //this.availableStatus = (ImageView)param1View.findViewById(R.id.availableStatus);
            this.listener = param1OnRecyclerItemListener;
        }

        public void setListener(OnRecyclerItemListener param1OnRecyclerItemListener) {
            this.listener = param1OnRecyclerItemListener;
        }

        public void updateItem(final SquareItem productCategoryEntity) {
            this.ivProduct.setImageResource(productCategoryEntity.getImage());
            this.tvTitle.setText(productCategoryEntity.getTitle());
            //this.rlContainer.setBackgroundColor(Color.parseColor(productCategoryEntity.getColor()));
           /* if (productCategoryEntity.isActive()) {
                this.availableStatus.setVisibility(View.VISIBLE);
            } else {
                this.availableStatus.setVisibility(View.GONE);
            }*/
            if (productCategoryEntity.getQty() != null)
                this.tvQty.setText(productCategoryEntity.getQty());
            this.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View param2View) {
                    if (SquareItemAdapter.ViewHolder.this.listener != null)
                        SquareItemAdapter.ViewHolder.this.listener.onClickListener(param2View, productCategoryEntity);
                }
            });
        }
    }

    private class ClickListener implements View.OnClickListener {
        private SquareItemAdapter.ViewHolder onRecyclerItemListener;

        public void onClick(View param1View) {
            if (this.onRecyclerItemListener.listener != null)
                this.onRecyclerItemListener.listener.onClickListener(param1View,null);
        }
    }
}