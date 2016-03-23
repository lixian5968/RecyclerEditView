package com.yjmfortune.recyclereditview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Bind(R.id.look)
    LinearLayout look;
    @Bind(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.add_text)
    LinearLayout add_text;
    @Bind(R.id.add_image)
    LinearLayout add_image;
    @Bind(R.id.system_out)
    LinearLayout system_out;


    Context ct;
    private List<EditOrImageBean> mSelectBeans;
    private EditAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ct = this;
        ButterKnife.bind(this);
        mSelectBeans = new ArrayList<>();
        //String imageUrl, Bitmap bitmap, String edtName, String type
        mSelectBeans.add(new EditOrImageBean(null, "text"));
        mSelectBeans.add(new EditOrImageBean(null, "image"));
        InitRecyclerView();

        add_text.setOnClickListener(this);
        add_image.setOnClickListener(this);
        system_out.setOnClickListener(this);
        look.setOnClickListener(this);

    }

    //设置滑动时间戳
    private void InitRecyclerView() {

        //setHasFixedSize()方法用来使RecyclerView保持固定的大小，该信息被用于自身的优化。
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new EditAdapter(this, mSelectBeans);
        mRecyclerView.setAdapter(mAdapter);

        ((EditAdapter) mAdapter).setOnItemClickLitener(new EditAdapter.OnItemClickLitener() {
            @Override
            public void onItemAddClick(View view, ImageView imageView, int position) {
                imageView.setTag(position);
                showImageDialog(imageView);

            }

            @Override
            public void onDeleteAddClick(View view, int position) {
                mSelectBeans.remove(position);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onUpdateText(View view, int position, String s) {
                mSelectBeans.get(position).setText(s);
            }

        });

        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT //ItemTouchHelper.UP | ItemTouchHelper.DOWN
                , ItemTouchHelper.ACTION_STATE_IDLE) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(mSelectBeans, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(mSelectBeans, i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mSelectBeans.remove(position);
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == 1) {
                    Toast.makeText(ct, "删除", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_text:
                mSelectBeans.add(new EditOrImageBean(null, "text"));
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(mSelectBeans.size());
                break;

            case R.id.add_image:
                mSelectBeans.add(new EditOrImageBean(null, "image"));
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(mSelectBeans.size());
                break;
            case R.id.system_out:
                String s = ((EditText) mRecyclerView.getChildAt(0).findViewById(R.id.mEditText)).getText().toString();
                Toast.makeText(ct, s, Toast.LENGTH_SHORT).show();
                Log.e("lx", s);
                break;


            case R.id.look:
//                UpdateBean();

                Intent it = new Intent(ct,ShowActivity.class);
//                Bundle bd = new Bundle();
//                bd.putSerializable("mSelectBeans", (Serializable) mSelectBeans);
//                it.putExtras(bd);
                bena.mSelectBeans = mSelectBeans;
                startActivity(it);


                break;

        }
    }

//    private void UpdateBean() {
//        for(int i=0;i<mSelectBeans.size();i++){
//            EditOrImageBean bean =  mSelectBeans.get(i);
//            if ("text".equals(bean.getType())) {
//                String result =((EditText) mRecyclerView.getChildAt(i).findViewById(R.id.mEditText)).getText().toString();
//                bean.setText(result);
//            } else if ("image".equals(bean.getType())) {
//                BitmapDrawable drawable = (BitmapDrawable) ((ImageView) mRecyclerView.getChildAt(i).findViewById(R.id.mImageView)).getDrawable();
//                if(drawable!=null){
//                    bean.setBitmap(drawable.getBitmap());
//                }
//            }
//        }
//    }


    private static final int CAMERA_REQUEST = 1888;
    private final int PICTURE_ASK = 1001;
    Uri mUri;
    ImageView mImageView;

    private void showImageDialog(ImageView imageView) {
        mImageView = imageView;
        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        builder.setTitle("选择头像");
        builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        String saveDir = Environment.getExternalStorageDirectory() + "/lpj/";
                        File dir = new File(saveDir);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        String newName = System.currentTimeMillis() + ".jpg";
                        File file = new File(saveDir, newName);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        mUri = Uri.fromFile(file);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent pictureSelect = new Intent(Intent.ACTION_GET_CONTENT);
                pictureSelect.setType("image/*");
                //调用系统相册
                startActivityForResult(pictureSelect, PICTURE_ASK);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //得到的图像
    private Bitmap bmp;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (requestCode == PICTURE_ASK) {
                    Uri originalUri = data.getData();
                    GitBitmap(originalUri);
                } else if (requestCode == CAMERA_REQUEST) {
                    Uri mUri = data.getData();
                    if (mUri != null) {
                        GitBitmap(mUri);
                    } else {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        if (photo != null) {
                            bmp = photo;
                            setImage("",bmp);
                        }
                    }
                }
            } else {
                if (requestCode == CAMERA_REQUEST) {
                    GitBitmap(mUri);
                }
            }
        }
    }

    private void GitBitmap(final Uri originalUri) {
        Glide.with(ct).load(originalUri).placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_image_loadfail).crossFade().into(new SimpleTarget<GlideDrawable>(500, 500) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                Bitmap bitmap = ((GlideBitmapDrawable) resource).getBitmap();
                Float xMatrix = (Float.valueOf(500)) / bitmap.getWidth();
                Matrix matrix = new Matrix();
                matrix.postScale(xMatrix, xMatrix); //长和宽放大缩小的比例
                Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                setImage(originalUri.toString(),resizeBmp);
            }
        });
    }

    private void setImage(String originalUri,Bitmap resizeBmp) {
        mImageView.setImageBitmap(resizeBmp);
        mSelectBeans.get((Integer) mImageView.getTag()).setBitmap(resizeBmp);
        mSelectBeans.get((Integer) mImageView.getTag()).setBitmapUrl(originalUri);
    }
}
