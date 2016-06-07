package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.wefika.flowlayout.FlowLayout;
import com.xiang.Util.BitmapUtil;
import com.xiang.Util.Constant;
import com.xiang.Util.StringUtil;
import com.xiang.base.BaseHandler;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.model.ChoosedGym;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Token;
import com.xiang.proto.trend.nano.Trend;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.thread.GetQiniuTokenThread;
import com.xiang.view.MyTitleBar;
import com.xiang.view.TwoOptionMaterialDialog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class CreateTrendActivity extends BaseAppCompatActivity {

    private static final String TAG = "CreateTrendActivity";

    private MyTitleBar myTitleBar;
    private FlowLayout fl_images;
    private RelativeLayout rl_add, rl_choose_gym;
    private EditText et_content;
    private TextView tv_gymname;
    private View v_creating;

    private MaterialDialog md_quit;
    private TwoOptionMaterialDialog md_choose_image;

    // code
    private final int CODE_CAPTURE = 0;
    private final int CODE_PHOTO = 1;
    private final int CODE_PHOTO_CROP = 2;
    private final int CODE_CHOOSE_GYM = 3;
    private int imageSize = 0;
    private static final int MAX_COUNT = 9;
    private List<String> avatar_key = new ArrayList<>();
    private Uri imageUri;
    private String file_name;
    private String qiniuToken = "";
    private String bucketName = "";
    private List<Uri> imageUris = new ArrayList<>();
    private int uploadedCount = 0;
    private boolean isCreateing = false;

    //data
    private ChoosedGym choosedGym;

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

                if(isCreateing){
                    return ;
                }

                if (et_content.getText().toString().length() > Constant.MAX_LENGTH_TREND_CONENT) {
                    sendToast("内容过长，应在" + Constant.MAX_LENGTH_TREND_CONENT +"字以内");
                    return;
                }

                if (et_content.getText().toString().length() > 0 || imageUris.size() > 0) {
                    // 上传完成后自动发布
                    uploadBitmap();
                    myTitleBar.setMoreTextButtonEnable(false);
                    isCreateing = true;
                } else {
                    sendToast("还未编辑内容");
                }
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

        mHandler = new MyHandler(this, null);
        getQiniuToken();
    }

    private void getQiniuToken(){
        Token.Request11001 request11001 = new Token.Request11001();
        request11001.common = RequestUtil.getProtoCommon(11001, System.currentTimeMillis());
        new GetQiniuTokenThread(mHandler, request11001).start();
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

//    private void startPhotoCrop(Uri uri){
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//
//        // 宽高比
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);
//        intent.putExtra("return-data", true);
//        intent.putExtra("noFaceDetection", true);
//        startActivityForResult(intent, CODE_PHOTO_CROP);
//    }

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
                imageUris.add(imageUri);
                addImageView2FlowLayout(imageUri);
                break;
            case CODE_PHOTO:
                try{
                    imageUri = data.getData();
                    imageUris.add(imageUri);
                    addImageView2FlowLayout(imageUri);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
//            case CODE_PHOTO_CROP:
//                // 解析成Bitmap
//                Bitmap bitmap = data.getParcelableExtra("data");
//                addImageView2FlowLayout(bitmap);
//                civ_choose_avatar.setImageBitmap(bitmap);
//                uploadBitmap(bitmap);
//                break;

            case CODE_CHOOSE_GYM:
                choosedGym = (ChoosedGym) data.getSerializableExtra(ChooseGymActivity.CHOOSED_GYM);
                tv_gymname.setText(choosedGym.getGymName());
                break;

        }
    }

    private void addImageView2FlowLayout(Uri bitmapUri) {
        try {
            Bitmap bitmap = null;
            bitmap = BitmapUtil.getCompressedImage(this, bitmapUri, true, 80, false);
            addImageView2FlowLayout(bitmap);
        } catch(NullPointerException e) {
            sendToast("未找到图片，可能由于机型问题，请联系我们解决。");
        }
    }

    private void addImageView2FlowLayout(Bitmap bitmap){

        addBitmapToRecycle(bitmap);

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

    /**
     * 上传图片
     */
    private void uploadBitmap(){

        if(imageUris.size() == 0){
            // 如果没有图片，直接发布
            mHandler.sendEmptyMessage(KEY_START_CREATE_TREND);
            return;
        }

        if (StringUtil.isNullOrEmpty(qiniuToken)){
            sendToast("正在获取配置信息，请稍后重试");
            getQiniuToken();
            myTitleBar.setMoreTextButtonEnable(true);
            return ;
        }

        /**
         * 通过handle机制，一张一张上传
         */
        startUploadBitmap(0);
    }

    private void startUploadBitmap(int index){
        final Bitmap bitmap;

        bitmap = BitmapUtil.getCompressedImage(this, imageUris.get(index), false, 90, true);
        addBitmapToRecycle(bitmap);

        String thisKey = StringUtil.generatorTrendKey();
        avatar_key.add(thisKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        UploadManager uploadManager = new UploadManager();
        byte[] data = outputStream.toByteArray();
        uploadManager.put(data, thisKey, qiniuToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                uploadedCount ++;
                bitmap.recycle();
                System.gc();
                mHandler.sendEmptyMessage(KEY_UPLOAD_IMAGE);
            }
        }, null);              // option可以尝试使用，高级策略
        try {
            outputStream.close();
        } catch (Exception e) {

        }
    }

    private MyHandler mHandler;
    private final int KEY_UPLOAD_IMAGE = 101;
    private final int KEY_START_CREATE_TREND = 102;
    private final int KEY_TREND_CREATE_SUC = 103;
    class MyHandler extends BaseHandler {

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_GET_QINIU_TOKEN_SUC:
                    Token.Response11001.Data data = (Token.Response11001.Data) msg.obj;
                    qiniuToken = data.qiniuToken;
                    bucketName = data.bucketName;
                    break;
                case KEY_UPLOAD_IMAGE:
                    showProgress("已上传：" + uploadedCount + "/" + imageUris.size(), false);
                    if(uploadedCount == imageUris.size()){
                        sendEmptyMessage(KEY_START_CREATE_TREND);
                    } else {
                        startUploadBitmap(uploadedCount);
                    }
                    break;
                case KEY_START_CREATE_TREND:
                    showProgress("开始发布信息", false);
                    new CreateTrendThrad().start();
                    break;
                case KEY_TREND_CREATE_SUC:
                    showProgress("发布成功", true);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dissmissProgress();
                        }
                    }, 1000);
                    finish();
                    isCreateing = false;
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if ( ! isCreateing) {
            if (!et_content.getText().toString().equals("") || imageSize > 0) {
                //
                if (md_quit == null) {
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
        } else{
            sendToast("正在创建动态，请稍等");
        }
    }

    class CreateTrendThrad extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 12001;
            Trend.Request12001 request = new Trend.Request12001();
            Trend.Request12001.Params params = new Trend.Request12001.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.bucketName = bucketName;
            params.content = et_content.getText().toString();
            if (choosedGym != null){
                params.gymId = choosedGym.getGymId();
            }
            params.imageKeys = new String[avatar_key.size()];
            for(int i = 0; i < avatar_key.size(); i ++){
                params.imageKeys[i] = avatar_key.get(i);
            }

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_CREATE_TREND, cmdid, currentMills);
            isCreateing = false;
            if (null != result){
                // 加载成功
                try{
                    Trend.Response12001 response = Trend.Response12001.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_TREND_CREATE_SUC;
                            mHandler.sendMessage(msg);
                        } else{
                            // code is not 0, find error
                            Message msg = Message.obtain();
                            msg.what = BaseHandler.KEY_ERROR;
                            msg.obj = response.common.message;
                            mHandler.sendMessage(msg);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (Exception e){
                    Message msg = Message.obtain();
                    msg.what = BaseHandler.KEY_PARSE_ERROR;
                    mHandler.sendMessage(msg);
                    e.printStackTrace();
                }
            } else {
                // 加载失败
                Message msg = Message.obtain();
                msg.what = BaseHandler.KEY_NO_RES;
                mHandler.sendMessage(msg);
            }
        }
    }
}
