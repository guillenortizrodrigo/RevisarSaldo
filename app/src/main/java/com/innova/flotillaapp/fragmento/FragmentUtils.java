package com.innova.flotillaapp.fragmento;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.innova.flotillaapp.R;


public class FragmentUtils {
    private static BottomSheetDialog mBottomSheetDialogError;

    private static BottomSheetDialog mBottomSheetDialogProgress;

    private static BottomSheetDialog mBottomSheetDialogSuccess;

    private static void clear() {
        if (mBottomSheetDialogError != null) {
            mBottomSheetDialogError.dismiss();
            mBottomSheetDialogError = null;
        }
        if (mBottomSheetDialogSuccess != null) {
            mBottomSheetDialogSuccess.dismiss();
            mBottomSheetDialogSuccess = null;
        }
        if (mBottomSheetDialogProgress != null) {
            mBottomSheetDialogProgress.dismiss();
            mBottomSheetDialogProgress = null;
        }
    }

    public static void hideInProcessMessage(Context paramContext, String paramString) {
        mBottomSheetDialogProgress.hide();
    }

    public static void hideSoftKeyboard(View paramView, Activity paramActivity) {
        ((InputMethodManager)paramActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(paramView.getWindowToken(), 1);
    }

    public static void showError(Context paramContext, String paramString) {
        clear();
        mBottomSheetDialogError = new BottomSheetDialog(paramContext);
        View view = ((LayoutInflater)paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_error, null);
        if (paramString != null)
            ((TextView)view.findViewById(R.id.mensajeerror)).setText(paramString);
        mBottomSheetDialogError.setContentView(view);
        if (Build.VERSION.SDK_INT >= 21)
            mBottomSheetDialogError.getWindow().addFlags(67108864);
        mBottomSheetDialogError.show();
    }

    public static void showSoftKeyboard(View paramView, Activity paramActivity) {
        if (paramView.requestFocus())
            ((InputMethodManager)paramActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(paramView, 1);
    }

    public static void showSuccessMessage(Context paramContext, String paramString) {
        clear();
        mBottomSheetDialogSuccess = new BottomSheetDialog(paramContext);
        View view = ((LayoutInflater)paramContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_success, null);
        if (paramString != null)
            ((TextView)view.findViewById(R.id.mensajeexito)).setText(paramString);
        mBottomSheetDialogSuccess.setContentView(view);
        if (Build.VERSION.SDK_INT >= 21)
            mBottomSheetDialogSuccess.getWindow().addFlags(67108864);
        mBottomSheetDialogSuccess.show();
    }
}
