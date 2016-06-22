package com.xiang.factory;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.CheckBox;
import com.rey.material.widget.RadioButton;
import com.xiang.sportx.LoginActivity;
import com.xiang.sportx.R;
import com.xiang.sportx.RegisterActivity;
import com.xiang.view.MateriaDialogWidthCheckBox;
import com.xiang.view.TwoOptionMaterialDialog;

/**
 * Created by 祥祥 on 2016/5/11.
 */
public class MaterialDialogFactory {
    private static int LAYOUT_TWO_OPTION = R.layout.md_two_option;
    private static int LAYOUT_CAN_CHECK = R.layout.md_with_checkbox;

    /**
     * 返回登录 或注册的对话框
     * @param context
     * @return
     */
    public static TwoOptionMaterialDialog createLoginOrRegisterMd(final Context context) {
        String[] options = new String[]{"登录", "注册"};
        TwoOptionMaterialDialog md_login_register = createTwoOptionMd(context, options, false, 0, true);
        md_login_register = MaterialDialogFactory.createTwoOptionMd(context, options, false, 0, true);
        final TwoOptionMaterialDialog finalMd_login_register = md_login_register;
        md_login_register.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
            @Override
            public void onOptionChoose(int index) {
                if(index == 0){
                    context.startActivity(new Intent(context, LoginActivity.class));
                    finalMd_login_register.dismiss();
                } else{
                    context.startActivity(new Intent(context, RegisterActivity.class));
                    finalMd_login_register.dismiss();
                }
            }
        });
        md_login_register.setTitle("您还未登录");
        md_login_register.setCanceledOnTouchOutside(true);
        return md_login_register;
    }

    /**
     *
     * @param context
     * @param options
     * @param showRadio
     * @param choosedIndex
     * @return
     */
    public static TwoOptionMaterialDialog createTwoOptionMd(Context context, String[] options, boolean showRadio, int choosedIndex) {
        return createTwoOptionMd(context, options, showRadio, choosedIndex, false);
    }

    /**
     * 返回一个具有两个选项的md，但没有设置title和button，还需使用者自己设置
     * @param context
     * @param options
     * @param showRadio
     * @param choosedIndex -1 表示不选择 ，其他从0开始
     * @param firstPrimary 第一个选项是否改变颜色
     * @return
     */
    public static TwoOptionMaterialDialog createTwoOptionMd(Context context, String[] options, boolean showRadio, int choosedIndex, boolean firstPrimary){
        final TwoOptionMaterialDialog twoOptionMaterialDialog = new TwoOptionMaterialDialog(context);
        View view = LayoutInflater.from(context).inflate(LAYOUT_TWO_OPTION, null ,false);
        LinearLayout ll_text = (LinearLayout) view.findViewById(R.id.ll_text);
        LinearLayout ll_radio = (LinearLayout) view.findViewById(R.id.ll_radio);
        final RadioButton rb_option1 = (RadioButton) view.findViewById(R.id.rb_option1);
        final RadioButton rb_option2 = (RadioButton) view.findViewById(R.id.rb_option2);
        TextView tv_option1 = (TextView) view.findViewById(R.id.tv_option1);
        TextView tv_option2 = (TextView) view.findViewById(R.id.tv_option2);

        if(firstPrimary){
            tv_option1.setTextColor(context.getResources().getColor(R.color.primary_button_color));
            rb_option1.setTextColor(context.getResources().getColor(R.color.primary_button_color));
        }

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

            if(choosedIndex != -1){
                switch (choosedIndex){
                    case 0:
                        rb_option1.setChecked(true);
//                        rb_option1.clearAnimation();
                        break;
                    case 1:
                        rb_option2.setChecked(true);
                        break;
                    default:
                        break;
                }
            }

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

    public static MateriaDialogWidthCheckBox createCheckBoxMd(Context context, String content, String checkContent, boolean checked){
        final MateriaDialogWidthCheckBox md = new MateriaDialogWidthCheckBox(context);
        View view = LayoutInflater.from(context).inflate(LAYOUT_CAN_CHECK, null, false);
        TextView textView = (TextView) view.findViewById(R.id.tv_content);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        textView.setText(content);
        checkBox.setChecked(checked);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                md.onCheck(isChecked);
            }
        });

        md.setContentView(view);
        md.setCanceledOnTouchOutside(true);

        return md;
    }


}
