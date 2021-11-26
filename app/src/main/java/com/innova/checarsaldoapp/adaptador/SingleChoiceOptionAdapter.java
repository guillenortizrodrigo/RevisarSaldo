package com.innova.checarsaldoapp.adaptador;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.innova.checarsaldoapp.R;
import com.innova.checarsaldoapp.model.Option;

import java.util.List;

public class SingleChoiceOptionAdapter extends RecyclerView.Adapter<SingleChoiceOptionAdapter.ViewHolder> {
    private Context context;

    private OnItemClickListener mOnItemClickListener;

    private List<Option> options;

    public SingleChoiceOptionAdapter(List<Option> paramList, Context paramContext) {
        this.options = paramList;
        this.context = paramContext;
    }

    public int getItemCount() {
        return this.options.size();
    }

    public void onBindViewHolder(final ViewHolder view, final int position) {
        view.updateItem(this.options.get(position));
        final Option p = this.options.get(position);
        view.container.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SingleChoiceOptionAdapter.this.mOnItemClickListener == null)
                    return;
                view.btnOption.setBackgroundTintList(SingleChoiceOptionAdapter.this.context.getResources().getColorStateList(R.color.colorPrimary));
                view.btnOption.setImageResource(R.drawable.ic_done);
                SingleChoiceOptionAdapter.this.mOnItemClickListener.onItemClick(param1View, p, position);
            }
        });
        view.btnOption.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (SingleChoiceOptionAdapter.this.mOnItemClickListener == null)
                    return;
                view.btnOption.setBackgroundTintList(SingleChoiceOptionAdapter.this.context.getResources().getColorStateList(R.color.colorPrimary));
                view.btnOption.setImageResource(R.drawable.ic_done);
                SingleChoiceOptionAdapter.this.mOnItemClickListener.onItemClick(param1View, p, position);
            }
        });
    }

    public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.item_single_option, paramViewGroup, false));
    }

    public void selectOption(int paramInt, Option paramOption) {
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener paramOnItemClickListener) {
        this.mOnItemClickListener = paramOnItemClickListener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View param1View, Option param1Option, int param1Int);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public FloatingActionButton btnOption;

        public MaterialRippleLayout container;

        private Option option;

        public TextView tvOption;

        public ViewHolder(View param1View) {
            super(param1View);
            this.tvOption = (TextView)param1View.findViewById(R.id.tvOption);
            this.btnOption = (FloatingActionButton)param1View.findViewById(R.id.btnOption);
            this.container = (MaterialRippleLayout)param1View.findViewById(R.id.container);
        }

        public void updateItem(Option param1Option) {
            this.option = param1Option;
            this.tvOption.setText(param1Option.getLabel());
        }
    }
}
