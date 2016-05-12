package com.xiang.view;

import android.content.Context;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 祥祥 on 2016/5/12.
 */
public class MateriaDialogWidthCheckBox extends MaterialDialog {
    public MateriaDialogWidthCheckBox(Context context) {
        super(context);
    }

    public void onCheck(boolean checked){
        if(onCheckListener != null){
            onCheckListener.onCheck(checked);
        }
    }

    private OnCheckListener onCheckListener;

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public OnCheckListener getOnCheckListener() {
        return onCheckListener;
    }

    public interface OnCheckListener{
        public void onCheck(boolean checked);
    }
}
