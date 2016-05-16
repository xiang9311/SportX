package com.xiang.sportx;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.model.ChoosedGym;
import com.xiang.view.MyTitleBar;
import com.xiang.view.TwoOptionMaterialDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.drakeet.materialdialog.MaterialDialog;

public class CreateTrendActivity extends BaseAppCompatActivity {

    private static final String TAG = "CreateTrendActivity";

    private MyTitleBar myTitleBar;
    private FlowLayout fl_images;
    private RelativeLayout rl_add, rl_choose_gym;
    private EditText et_content;
    private TextView tv_gymname;

    private MaterialDialog md_quit;
    private TwoOptionMaterialDialog md_choose_image;

    // code
    private final int CODE_CAPTURE = 0;
    private final int CODE_PHOTO = 1;
    private final int CODE_PHOTO_CROP = 2;
    private final int CODE_CHOOSE_GYM = 3;
    private Uri imageUri;
    private String file_name;
    private int imageSize = 0;

    private static final int MAX_COUNT = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_create_trend);

        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
        fl_images = (FlowLayout) findViewById(R.id.fl_images);
        rl_add = (RelativeLayout) findViewById(R.id.rl_images);
        rl_choose_gym = (RelativeLayout) findViewById(R.id.rl_choose_gym);
        et_content = (EditText) findViewById(R.id.et_trend_content);
        tv_gymname = (TextView) findViewById(R.id.tv_gym_name);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setBackButtonDefault();
        myTitleBar.setTitle("新动态");
        myTitleBar.setMoreTextButton("发布", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO fabu
            }
        });

        rl_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = new String[]{"相册", "拍照"};
                if (md_choose_image == null) {
                    md_choose_image = MaterialDialogFactory.createTwoOptionMd(CreateTrendActivity.this, options, false, 0);
                    md_choose_image.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                        @Override
                        public void onOptionChoose(int index) {
                            md_choose_image.dismiss();
                            switch (index) {
                                case 0:
                                    startChoosePhoto();
                                    break;
                                case 1:
                                    startTakePhoto();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    md_choose_image.setTitle("选择方式");
                }
                md_choose_image.show();
            }
        });

        rl_choose_gym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTrendActivity.this, ChooseGymActivity.class);
                startActivityForResult(intent, CODE_CHOOSE_GYM);
            }
        });
    }


    private void startChoosePhoto(){
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openAlbumIntent, CODE_PHOTO);
    }

    /**
     * 调用拍照
     */
    private void startTakePhoto(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        file_name = simpleDateFormat.format(date) + ".jpg";
        // 创建file存储图片
        Log.d(TAG, "absolute:" + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.d(TAG, "path:" + Environment.getExternalStorageDirectory().getPath());
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SportX/";
        File file = new File(file_path, file_name);
        try{
            File folder = new File(file_path);

            if(!folder.exists()){
                folder.mkdirs();
            }

            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
            sendToast("哎吆，程序出错了...");
        }
        imageUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CODE_CAPTURE);
    }

    private void startPhotoCrop(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");

        // 宽高比
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CODE_PHOTO_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode
                + ", resultCode: " + requestCode + ", data: " + data);

        if (resultCode != RESULT_OK) {
//            sendToast(resultCode + "");
            return;
        }

        switch (requestCode){
            case CODE_CAPTURE:
                startPhotoCrop(imageUri);
                break;
            case CODE_PHOTO:
                try{
                    imageUri = data.getData();
                    startPhotoCrop(imageUri);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_PHOTO_CROP:
                // 解析成Bitmap
                Bitmap bitmap = data.getParcelableExtra("data");
                addImageView2FlowLayout(bitmap);
//                civ_choose_avatar.setImageBitmap(bitmap);
//                uploadBitmap(bitmap);
                //TODO upload?
                break;

            case CODE_CHOOSE_GYM:
                ChoosedGym choosedGym = (ChoosedGym) data.getSerializableExtra(ChooseGymActivity.CHOOSED_GYM);
                tv_gymname.setText(choosedGym.getGymName());
                break;

        }
    }

    private void addImageView2FlowLayout(Bitmap bitmap){
        View view = LayoutInflater.from(this).inflate(R.layout.view_small_imageview, null);

        int size = (int) getResources().getDimension(R.dimen.trend_upload_size);
        int margin = (int) getResources().getDimension(R.dimen.trend_iv_margin);
//        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(size, size);
        FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(size, size);
        layoutParams.setMargins(margin, margin, margin, margin);

        RelativeLayout rl_image = (RelativeLayout) view.findViewById(R.id.rl_image);
        ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);

        rl_image.setLayoutParams(layoutParams);

        iv_image.setImageBitmap(bitmap);
        fl_images.addView(view, imageSize);

        imageSize ++;

        if(imageSize >= 9){
            rl_add.setVisibility(View.GONE);
        }
    }

//    /**
//     * 上传图片
//     * @param bitmap
//     */
//    private void uploadBitmap(Bitmap bitmap){
//
//        if (StringUtil.isNullOrEmpty(qiniuToken)){
//            sendToast("正在获取配置信息，请稍后重试");
//            return ;
//        }
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
//        UploadManager uploadManager = new UploadManager();
//        byte[] data = outputStream.toByteArray();
//        uploadManager.put(data, StringUtil.generatorQiniuKey(), qiniuToken, new UpCompletionHandler() {
//            @Override
//            public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
//                avatar_key = key;
//            }
//        }, null);              // option可以尝试使用，高级策略
//        try{
//            outputStream.close();
//        } catch (Exception e){
//
//        }
//    }


    @Override
    public void onBackPressed() {
        if(!et_content.getText().toString().equals("") || imageSize > 0){
            //
            if(md_quit == null){
                md_quit = new MaterialDialog(this);
                md_quit.setMessage("退出编辑？");
                md_quit.setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        md_quit.dismiss();
                    }
                });
                md_quit.setNegativeButton("退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                md_quit.setCanceledOnTouchOutside(true);
            }

            md_quit.show();
        } else {
            super.onBackPressed();
        }
    }
}
