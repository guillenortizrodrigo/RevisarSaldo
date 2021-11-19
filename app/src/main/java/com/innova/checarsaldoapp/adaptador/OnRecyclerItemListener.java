package com.innova.checarsaldoapp.adaptador;

import android.view.View;

public interface OnRecyclerItemListener<T> {
    void onClickListener(View paramView, T paramT);
}
