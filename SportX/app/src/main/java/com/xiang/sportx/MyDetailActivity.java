package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xiang.Util.Constant;
import com.xiang.Util.StringUtil;
import com.xiang.Util.UserStatic;
import com.xiang.base.BaseHandler;
import com.xiang.database.helper.BriefUserHelper;
import com.xiang.database.model.TblBriefUser;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.factory.MaterialDialogFactory;
import com.xiang.proto.nano.Common;
import com.xiang.proto.pilot.nano.Pilot;
import com.xiang.proto.pilot.nano.Token;
import com.xiang.request.RequestUtil;
import com.xiang.request.UrlUtil;
import com.xiang.thread.GetQiniuTokenThread;
import com.xiang.view.MyTitleBar;
import com.xiang.view.TwoOptionMaterialDialog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import me.drakeet.materialdialog.MaterialDialog;

public class MyDetailActivity extends BaseAppCompatActivity {

    private static final String TAG = "MyDetailActivity";
    // code
    private final int CODE_CAPTURE = 0;
    private final int CODE_PHOTO = 1;
    private final int CODE_PHOTO_CROP = 2;

    private MyTitleBar myTitleBar;
    private ImageView iv_avatar;
    private RelativeLayout rl_avatar, rl_name, rl_sex, rl_sign;
    private View v_username, v_sign;
    private MaterialEditText met_username, met_sign;
    private TextView tv_username, tv_sign, tv_sex;

    //dialog
    private MaterialDialog md_username, md_sign;
    private TwoOptionMaterialDialog md_changeavatar, md_sex;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = DisplayOptionsFactory.createNormalImageOption();
    private BriefUserHelper briefUserHelper = new BriefUserHelper(this);


    // data
    private String toAvatarUrl, toUsername, toSign;
    private int toSex;
    // data
    private String avatar_key = "";
    private Uri imageUri;
    private String file_name;

    private String qiniuToken = "";
    private String bucketName = "";

    private Bitmap avatarBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_my_detail);
        myTitleBar = (MyTitleBar) findViewById(R.id.titleBar);
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        rl_avatar = (RelativeLayout) findViewById(R.id.rl_avatar);
        rl_name = (RelativeLayout) findViewById(R.id.rl_username);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_sign = (RelativeLayout) findViewById(R.id.rl_sign);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_sign = (TextView) findViewById(R.id.tv_sign);
        tv_sex = (TextView) findViewById(R.id.tv_sex);

        //init dialog views
        v_username = LayoutInflater.from(this).inflate(R.layout.md_input, null, false);
        met_username = (MaterialEditText) v_username.findViewById(R.id.met_input);
        met_username.setMaxCharacters(Constant.MAX_LENGTH_USER_NAME);
        met_username.setFloatingLabelText("昵称");
        met_username.setHint("昵称");

        v_sign = LayoutInflater.from(this).inflate(R.layout.md_input, null, false);
        met_sign = (MaterialEditText) v_sign.findViewById(R.id.met_input);
        met_sign.setMaxCharacters(Constant.MAX_LENGTH_SIGN);
        met_sign.setFloatingLabelText("个性签名");
        met_sign.setHint("个性签名");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        myTitleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myTitleBar.setTitle("个人信息");
        myTitleBar.setMoreButton(0, false, null);

        imageLoader.displayImage(UserStatic.avatarUrl, iv_avatar, options);
        tv_username.setText(UserStatic.realUserName);
        tv_sign.setText(UserStatic.sign);
        tv_sex.setText(UserStatic.getSex());

        // md  点击事件
        rl_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (md_username == null) {
                    md_username = new MaterialDialog(MyDetailActivity.this);
                    md_username.setContentView(v_username);
                    md_username.setCanceledOnTouchOutside(true);
                    md_username.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //

                            String username = met_username.getText().toString();

                            if (!StringUtil.isNotEmpty(username)) {
                                sendToast("用户名不能为空");
                                return;
                            }

                            if (!checkUserName(username) || username.length() > Constant.MAX_LENGTH_USER_NAME) {
                                sendToast("用户名不合法");
                                return;
                            }

                            md_username.dismiss();
                            toUsername = username;
                            C_WHAT = C_USERNAME;

                            Pilot.Request10004.Params params = new Pilot.Request10004.Params();
                            params.userName = username;
                            new UpdateMyInfoThread(params).start();

                        }
                    });
                }
                md_username.show();
            }
        });

        rl_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (md_sign == null) {
                    md_sign = new MaterialDialog(MyDetailActivity.this);
                    md_sign.setContentView(v_sign);
                    md_sign.setCanceledOnTouchOutside(true);
                    md_sign.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //

                            String sign = met_sign.getText().toString();

                            if (!StringUtil.isNotEmpty(sign)) {
                                sendToast("签名不能为空");
                                return;
                            }

                            if (!checkUserSign(sign) || sign.length() > Constant.MAX_LENGTH_SIGN) {
                                sendToast("签名不合法");
                                return;
                            }

                            md_sign.dismiss();
                            toSign = sign;
                            met_sign.setText("");
                            C_WHAT = C_SIGN;

                            Pilot.Request10004.Params params = new Pilot.Request10004.Params();
                            params.sign = sign;
                            new UpdateMyInfoThread(params).start();
                        }
                    });
                }
                md_sign.show();
            }
        });

        rl_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = new String[]{"相册", "拍照"};
                if(md_changeavatar == null){
                    md_changeavatar = MaterialDialogFactory.createTwoOptionMd(MyDetailActivity.this, options, false, 0);
                    md_changeavatar.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                        @Override
                        public void onOptionChoose(int index) {
                            md_changeavatar.dismiss();
                            switch (index) {
                                case 0:
                                    startChoosePhoto();
                                    break;
                                case 1:
                                    startTakePhoto();
                                    break;
                            }
                        }
                    });
                    md_changeavatar.setTitle("选择方式");
                }
                md_changeavatar.show();
            }
        });

        rl_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options = new String[]{"男", "女"};
                if (md_sex == null) {
                    md_sex = MaterialDialogFactory.createTwoOptionMd(MyDetailActivity.this, options, true, UserStatic.sex);
                    md_sex.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                        @Override
                        public void onOptionChoose(final int index) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    md_sex.dismiss();
                                    toSex = index;

                                    Pilot.Request10004.Params params = new Pilot.Request10004.Params();
                                    params.sex = index;
                                    C_WHAT = C_SEX;
                                    new UpdateMyInfoThread(params).start();
                                }
                            }, 250);  // 这个时间时redio的动画时间
                        }
                    });
                    md_sex.setTitle("选择性别");
                }
                md_sex.show();
            }
        });

        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] images = new String[]{UserStatic.avatarUrl};
                Intent intent = new Intent(MyDetailActivity.this, ImageAndTextActivity.class);
                intent.putExtra(Constant.IMAGES, images);
                intent.putExtra(Constant.SHOW_INDICATOR, false);
                startActivity(intent);
            }
        });


        mHandler = new MyHandler(this, null);

        getQiniuToken();
    }

    public static boolean checkUserName(String userName) {
        String regex = "^[^('\"\\\\?)]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(userName);
        return m.matches();
    }

    public static boolean checkUserSign(String sign) {
        String regex = "^[^('\"\\\\?)]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(sign);
        return m.matches();
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
     * 上传图片
     * @param bitmap
     */
    private void uploadBitmap(Bitmap bitmap){

        if(bitmap == null || bitmap.isRecycled()){
            sendToast("头像还未选择或已被系统回收，请重新选择");
            return;
        }

        if (StringUtil.isNullOrEmpty(qiniuToken)){
            sendToast("正在获取配置信息，请稍后重试");
            getQiniuToken();
            return ;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        UploadManager uploadManager = new UploadManager();
        byte[] data = outputStream.toByteArray();
        uploadManager.put(data, StringUtil.generatorQiniuKey(), qiniuToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                C_WHAT = C_AVATAR;
                avatar_key = key;
                Pilot.Request10004.Params params = new Pilot.Request10004.Params();
                params.avatarKey = avatar_key;
                params.bucketName = bucketName;
                new UpdateMyInfoThread(params).start();
            }
        }, null);              // option可以尝试使用，高级策略
        try{
            outputStream.close();
        } catch (Exception e){

        }
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
//            sendToast(error_string);
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
                iv_avatar.setImageBitmap(bitmap);
                if (avatarBitmap != null && !avatarBitmap.isRecycled()){
                    avatarBitmap.recycle();
                }
                avatarBitmap = bitmap;
                uploadBitmap(avatarBitmap);
                break;

        }
    }


    private MyHandler mHandler;
    private static final int KEY_UPDATE_SUC = 101;

    private static int C_WHAT = 0;
    private static final int C_AVATAR = 1;
    private static final int C_USERNAME = 2;
    private static final int C_SEX = 3;
    private static final int C_SIGN = 4;

    class MyHandler extends BaseHandler{
        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_UPDATE_SUC:
                    switch (C_WHAT){
                        case C_AVATAR:
                            UserStatic.avatarUrl = toAvatarUrl;
                            briefUserHelper.saveUser(TblBriefUser.createStaticBriefUser());
                            Uri uri = Uri.parse(UserStatic.avatarUrl);
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(UserStatic.userId + "", UserStatic.realUserName, uri));
                            break;
                        case C_USERNAME:
                            UserStatic.realUserName = toUsername;
                            tv_username.setText(UserStatic.realUserName);
                            briefUserHelper.saveUser(TblBriefUser.createStaticBriefUser());
                            Uri uri2 = Uri.parse(UserStatic.avatarUrl);
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(UserStatic.userId + "", UserStatic.realUserName, uri2));
                            break;
                        case C_SEX:
                            UserStatic.sex = toSex;
                            tv_sex.setText((toSex == Common.MALE) ? "男" : "女");
                            break;
                        case C_SIGN:
                            UserStatic.sign = toSign;
                            tv_sign.setText(toSign);
                            break;
                    }
                    Intent intent = new Intent(Constant.BROADCAST_UPDATE_USERINFO);
                    LocalBroadcastManager.getInstance(MyDetailActivity.this).sendBroadcast(intent);
                    break;

                case KEY_GET_QINIU_TOKEN_SUC:
                    Token.Response11001.Data data = (Token.Response11001.Data) msg.obj;
                    qiniuToken = data.qiniuToken;
                    bucketName = data.bucketName;
                    break;
            }
        }
    }

    class UpdateMyInfoThread extends Thread{
        private Pilot.Request10004.Params params;
        public UpdateMyInfoThread(Pilot.Request10004.Params params){
            this.params = params;
        }

        @Override
        public void run() {
            super.run();

            long currentMills = System.currentTimeMillis();
            int cmdid = 10004;
            Pilot.Request10004 request = new Pilot.Request10004();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_UPDATE_MY_INFO, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10004 response = Pilot.Response10004.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            if(response.data != null){
                                toAvatarUrl = response.data.avatarUrl;
                            }
                            Message msg = Message.obtain();
                            msg.what = KEY_UPDATE_SUC;
                            mHandler.sendMessage(msg);
                            mHandler.sendToast("更新成功");
                        } else{
                            // code is not 0, find error
                            mHandler.sendToast("更新用户信息失败" + response.common.message);
                        }
                    } else {
                        Message msg = Message.obtain();
                        msg.what = BaseHandler.KEY_ERROR;
                        msg.obj = "数据错误";
                        mHandler.sendMessage(msg);
                    }

                } catch (InvalidProtocolBufferNanoException e){
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
