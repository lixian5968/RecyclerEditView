package com.yjmfortune.recyclereditview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.MyViewHolder> {

    private List<EditOrImageBean> mDatas;
    private LayoutInflater mInflater;
    Context ct;
    List<EditOrImageBean> mSelectBeans;
    //选中的位置
    int pos;


    public interface OnItemClickLitener {
        void onItemAddClick(View view,ImageView imageView, int position);
        void onDeleteAddClick(View view, int position);
        void onUpdateText(View view, int position,String s);
        void onSettingClick(View view,  ImageView imageView, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public EditAdapter(Context context, List<EditOrImageBean> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        ct = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.edit_image_select, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        EditOrImageBean bean = mDatas.get(position);
        if ("text".equals(bean.getType())) {
            holder.mEditText.setVisibility(View.VISIBLE);
            holder.mImageView.setVisibility(View.GONE);
            holder.update.setVisibility(View.GONE);
            holder.setting.setVisibility(View.GONE);


            if(bean.getText()!=null && bean.getText().length()>0 ){
                holder.mEditText.setText(bean.getText());
                //文字还是不去动她
//                holder.mEditText.setGravity(bean.getTextGravity());
//                holder.mEditText.setTextColor(ct.getResources().getColor(bean.getTextColor()));
//                holder.mEditText.setTextSize(bean.getTextSize());
            }else{
                holder.mEditText.setText("");
            }



        } else if ("image".equals(bean.getType())) {
            holder.mEditText.setVisibility(View.GONE);
            holder.mImageView.setVisibility(View.VISIBLE);
            holder.update.setVisibility(View.VISIBLE);
            holder.setting.setVisibility(View.VISIBLE);
            if(bean.getBitmap()!=null){
                holder.mImageView.setImageBitmap(bean.getBitmap());
            }else{
                holder.mImageView.setImageBitmap(null);
            }
        }

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null) {

            holder.setting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onSettingClick(holder.itemView, holder.mImageView,pos);
                }
            });


            holder.update.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemAddClick(holder.itemView,holder.mImageView, pos);
                }
            });

            holder.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onDeleteAddClick(holder.itemView, pos);
                }
            });



            holder.mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onUpdateText(holder.itemView, pos, s+"");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
    }



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends ViewHolder {
        EditText mEditText;
        ImageView mImageView;
        LinearLayout update;
        LinearLayout delete;
        LinearLayout setting;
        public MyViewHolder(View view) {
            super(view);
            mEditText = (EditText) view.findViewById(R.id.mEditText);
            mImageView = (ImageView) view.findViewById(R.id.mImageView);
            update = (LinearLayout) view.findViewById(R.id.update);
            delete = (LinearLayout) view.findViewById(R.id.delete);
            setting = (LinearLayout) view.findViewById(R.id.setting);
        }
    }

//    public static int dip2px(Context context, float dipValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dipValue * scale + 0.5f);
//    }


}