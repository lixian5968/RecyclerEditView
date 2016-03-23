package com.yjmfortune.recyclereditview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;

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


    int width;     // 屏幕宽度（像素）
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


        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        int height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
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
                mAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onUpdateText(View view, int position, String s) {
                mSelectBeans.get(position).setText(s);
            }

            @Override
            public void onSettingClick(View view, ImageView imageView, int position) {
                EditOrImageBean bean = mSelectBeans.get(position);
                if ("text".equals(bean.getType())) {
                    showSettingDialog(view, position);
                } else if ("image".equals(bean.getType())) {
                    imageView.setTag(position);
                    mImageView = imageView;

                    if (bean.getBitmapUrl() != null && bean.getBitmapUrl().length() > 0) {

                        try {
                            Intent it = new Intent(MainActivity.this, EditImageActivity.class);
                            Uri mUri=    Uri.parse(bean.getBitmapUrl());
                            String[] proj = {MediaStore.Images.Media.DATA};
                            //好像是android多媒体数据库的封装接口，具体的看Android文档
                            Cursor cursor = managedQuery(mUri, proj, null, null, null);
                            //按我个人理解 这个是获得用户选择的图片的索引值
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            //将光标移至开头 ，这个很重要，不小心很容易引起越界
                            cursor.moveToFirst();
                            //最后根据索引值获取图片路径
                            String path = cursor.getString(column_index);
                            it.putExtra(EditImageActivity.FILE_PATH, mUri);


                            String saveDir = Environment.getExternalStorageDirectory() + "/lpj/";
                            String newName = System.currentTimeMillis() + ".jpg";
                            File file = new File(saveDir, newName);
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            it.putExtra(EditImageActivity.EXTRA_OUTPUT, file.getAbsolutePath());
                            Log.e("lxEXTRA_OUTPUT", file.getAbsolutePath());
                            MainActivity.this.startActivityForResult(it, ACTION_REQUEST_EDITIMAGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ct, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ct, "请填入图片", Toast.LENGTH_SHORT).show();
                    }
                }
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

                Intent it = new Intent(ct, ShowActivity.class);
//                Bundle bd = new Bundle();
//                bd.putSerializable("mSelectBeans", (Serializable) mSelectBeans);
//                it.putExtras(bd);
                bena.mSelectBeans = mSelectBeans;
                startActivity(it);


                break;

        }
    }


    private static final int CAMERA_REQUEST = 1888;
    private final int PICTURE_ASK = 1001;
    Uri mUri;
    ImageView mImageView;

    //图片编辑之后
    public static final int ACTION_REQUEST_EDITIMAGE = 9;

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
            if (requestCode == ACTION_REQUEST_EDITIMAGE) {
                String newFilePath = data.getStringExtra("save_file_path");
                GitBitmap(Uri.fromFile(new File(newFilePath)));
            } else {
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
                                setImage("", bmp);
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
    }

    private void GitBitmap(final Uri originalUri) {
        Glide.with(ct).load(originalUri).placeholder(R.drawable.ic_image_loading).error(R.drawable.ic_image_loadfail).crossFade().into(new SimpleTarget<GlideDrawable>(width, width) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                Bitmap bitmap = ((GlideBitmapDrawable) resource).getBitmap();
                bitmap.getWidth();
                Float xMatrix = (Float.valueOf(width)) / bitmap.getWidth();
                Matrix matrix = new Matrix();
                matrix.postScale(xMatrix, xMatrix); //长和宽放大缩小的比例
                Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                setImage(originalUri.toString(), resizeBmp);
            }
        });
    }

    private void setImage(String originalUri, Bitmap resizeBmp) {
        mImageView.setImageBitmap(resizeBmp);
        mSelectBeans.get((Integer) mImageView.getTag()).setBitmap(resizeBmp);
        mSelectBeans.get((Integer) mImageView.getTag()).setBitmapUrl(originalUri);
    }


    // 等待对话框
    public MyDialog MySettingDialog;
    RadioGroup locationRg;
    RadioGroup colorRg;
    RadioGroup SizeRg;
    Button check_ok;
    public int textGravity = Gravity.LEFT;
    public int textColor = R.color.black;
    public int textSize = 25;

    private void showSettingDialog(View HolderView, final int position) {
        View view = View.inflate(ct, R.layout.setting_dialog, null);
        locationRg = (RadioGroup) view.findViewById(R.id.locationRg);
        colorRg = (RadioGroup) view.findViewById(R.id.colorRg);
        SizeRg = (RadioGroup) view.findViewById(R.id.SizeRg);
        check_ok = (Button) view.findViewById(R.id.check_ok);


        if (MySettingDialog == null) {
            MySettingDialog = new MyDialog(ct, 0, 0, view, R.style.dialog);
        }
        MySettingDialog.setCancelable(true);
        MySettingDialog.show();


        locationRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.locationRb_left:
                        textGravity = Gravity.LEFT;
                        break;
                    case R.id.locationRb_middle:
                        textGravity = Gravity.CENTER_HORIZONTAL;
                        break;
                    case R.id.locationRb_right:
                        textGravity = Gravity.RIGHT;
                        break;
                }
            }
        });
        colorRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.colorRb_black:
                        textColor = R.color.black;
                        break;
                    case R.id.colorRb_white:
                        textColor = R.color.white;
                        break;
                    case R.id.colorRb_red:
                        textColor = R.color.red;
                        break;
                }
            }
        });
        SizeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.SizeRb1:
                        textSize = 25;
                        break;
                    case R.id.SizeRb2:
                        textSize = 35;
                        break;
                    case R.id.SizeRb3:
                        textSize = 45;
                        break;
                }
            }
        });


        check_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditOrImageBean bean = mSelectBeans.get(position);
                bean.setTextGravity(textGravity);
                bean.setTextColor(textColor);
                bean.setTextSize(textSize);
                mAdapter.notifyItemChanged(position);
                MySettingDialog.dismiss();
                MySettingDialog.cancel();
                MySettingDialog = null;
            }
        });

    }

}
