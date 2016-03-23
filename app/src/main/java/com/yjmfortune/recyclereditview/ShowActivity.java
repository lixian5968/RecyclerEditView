package com.yjmfortune.recyclereditview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.EditText;

import java.util.List;

public class ShowActivity extends AppCompatActivity {

    private List<EditOrImageBean> mSelectBeans;
    EditText ShowEditText;
    Context ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
//        mSelectBeans = (List<EditOrImageBean>) getIntent().getSerializableExtra("mSelectBeans");
        mSelectBeans = bena.mSelectBeans;
        ShowEditText = (EditText) findViewById(R.id.ShowEditText);
        ct = this;
        // 获取光标所在位置
        Editable edit_text = ShowEditText.getEditableText();


        for (int i = 0; i < mSelectBeans.size(); i++) {
            EditOrImageBean bean = mSelectBeans.get(i);
            if ("text".equals(bean.getType())) {
                if (bean.getText() != null && bean.getText().length() > 0) {
                    edit_text.append(bean.getText());
                }
            } else if ("image".equals(bean.getType())) {
                if (bean.getBitmap() != null) {
                    if (i != 0) {
                        edit_text.append("\n");
                    }
                    ImageSpan imageSpan = new ImageSpan(ct, bean.getBitmap());
                    SpannableString spannableString = new SpannableString("*");
                    spannableString.setSpan(imageSpan, spannableString.length() - "*".length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    edit_text.append(spannableString);
                    if (i == mSelectBeans.size() - 1) {
                        edit_text.insert(edit_text.length(), "\n");
                    }else{
                        edit_text.append("\n");
                    }
                }
            }
        }

        ShowEditText.setFocusable(false);
        ShowEditText.setEnabled(false);
    }


}
