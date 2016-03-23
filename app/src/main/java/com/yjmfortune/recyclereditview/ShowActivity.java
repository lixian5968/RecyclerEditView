package com.yjmfortune.recyclereditview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.util.List;

public class ShowActivity extends AppCompatActivity {

    private List<EditOrImageBean> mSelectBeans;
    TextView ShowEditText;
    Context ct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
//        mSelectBeans = (List<EditOrImageBean>) getIntent().getSerializableExtra("mSelectBeans");
        mSelectBeans = bena.mSelectBeans;
        ShowEditText = (TextView) findViewById(R.id.ShowEditText);
        ct = this;
        // 获取光标所在位置

        for (int i = 0; i < mSelectBeans.size(); i++) {
            EditOrImageBean bean = mSelectBeans.get(i);
            if ("text".equals(bean.getType())) {
                if (bean.getText() != null && bean.getText().length() > 0) {
                    ShowEditText.append(bean.getText());
                }
            } else if ("image".equals(bean.getType())) {
                if (bean.getBitmap() != null) {
                    if (i>0) {
                        ShowEditText.append("\n");
                    }
                    ImageSpan imageSpan = new ImageSpan(ct, bean.getBitmap());
                    SpannableString spannableString = new SpannableString("*");
                    spannableString.setSpan(imageSpan, spannableString.length() - "*".length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ShowEditText.append(spannableString);

                }
            }
        }


    }


}
