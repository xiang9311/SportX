package com.xiang.view;

import android.content.Context;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 祥祥 on 2016/5/11.
 */
public class TwoOptionMaterialDialog extends MaterialDialog {

    public TwoOptionMaterialDialog(Context context) {
        super(context);
    }

    public void onOptionChoose(int index){
        if(onOptionChooseListener != null) {
            onOptionChooseListener.onOptionChoose(index);
        }
    }

    private OnOptionChooseListener onOptionChooseListener;

    public void setOnOptionChooseListener(OnOptionChooseListener onOptionChooseListener) {
        this.onOptionChooseListener = onOptionChooseListener;
    }

    public OnOptionChooseListener getOnOptionChooseListener() {
        return onOptionChooseListener;
    }

    public interface OnOptionChooseListener{
        public void onOptionChoose(int index);
    }
}
