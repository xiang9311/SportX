package com.xiang.factory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.RadioButton;
import com.xiang.sportx.R;
import com.xiang.view.TwoOptionMaterialDialog;

/**
 * Created by 祥祥 on 2016/5/11.
 */
public class MaterialDialogFactory {
    private static int LAYOUT_TWO_OPTION = R.layout.md_two_option;

    /**
     * 返回一个具有两个选项的md，但没有设置title和button，还需使用者自己设置
     * @param context
     * @param options
     * @param showRadio
     * @return
     */
    public static TwoOptionMaterialDialog createTwoOptionMd(Context context, String[] options, boolean showRadio){
        final TwoOptionMaterialDialog twoOptionMaterialDialog = new TwoOptionMaterialDialog(context);
        View view = LayoutInflater.from(context).inflate(LAYOUT_TWO_OPTION, null ,false);
        LinearLayout ll_text = (LinearLayout) view.findViewById(R.id.ll_text);
        LinearLayout ll_radio = (LinearLayout) view.findViewById(R.id.ll_radio);
        final RadioButton rb_option1 = (RadioButton) view.findViewById(R.id.rb_option1);
        final RadioButton rb_option2 = (RadioButton) view.findViewById(R.id.rb_option2);
        TextView tv_option1 = (TextView) view.findViewById(R.id.tv_option1);
        TextView tv_option2 = (TextView) view.findViewById(R.id.tv_option2);

        if(! showRadio) {
            ll_radio.setVisibility(View.GONE);
            try {
                tv_option1.setText(options[0]);
                tv_option2.setText(options[1]);
            } catch (IndexOutOfBoundsException e) {
                Log.e("createTwoOptionMd", "options array out of index");
            }

            tv_option1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    twoOptionMaterialDialog.onOptionChoose(0);
                }
            });
            tv_option2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    twoOptionMaterialDialog.onOptionChoose(1);
                }
            });
        } else{
            ll_text.setVisibility(View.GONE);

            try {
                rb_option1.setText(options[0]);
                rb_option2.setText(options[1]);
            } catch (IndexOutOfBoundsException e) {
                Log.e("createTwoOptionMd", "options array out of index");
            }

            rb_option1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    twoOptionMaterialDialog.onOptionChoose(0);
                    rb_option2.setChecked(false);
                }
            });
            rb_option2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    twoOptionMaterialDialog.onOptionChoose(1);
                    rb_option1.setChecked(false);
                }
            });
        }

        twoOptionMaterialDialog.setContentView(view);
        twoOptionMaterialDialog.setCanceledOnTouchOutside(true);

        return twoOptionMaterialDialog;
    }


}
