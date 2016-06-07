package com.xiang.sportx;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sea_monster.common.Md5;
import com.xiang.Util.Constant;
import com.xiang.Util.StringUtil;
import com.xiang.base.BaseHandler;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;
import me.relex.circleindicator.CircleIndicator;

public class RegisterActivity extends BaseAppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int PAGER_TRANSLATE_TIME = 300;

    // code
    private final int CODE_CAPTURE = 0;
    private final int CODE_PHOTO = 1;
    private final int CODE_PHOTO_CROP = 2;

    private MyTitleBar titleBar;
    private ViewPager viewPager;
    private ActionProcessButton apb_getcode, apb_verifycode, apb_register;
    private MaterialEditText met_phone, met_code, met_username, met_password;
    private TextView tv_sex;
    private CircleImageView civ_avatar;
    private CircleIndicator ci_register;

    private TwoOptionMaterialDialog chooseImageDialog, chooseSexDialog;
    private MaterialDialog sendMessageDialog;

    private List<Fragment> registerFragments;

    // data
    private String avatar_key = "";
    private Uri imageUri;
    private String file_name;
    private int sex = Common.MALE;

    private String qiniuToken = "";
    private String bucketName = "";

    private Bitmap avatarBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_register);

        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        viewPager = (ViewPager) findViewById(R.id.vp_register);
        ci_register = (CircleIndicator) findViewById(R.id.ci_register);


        //TODO 取消注释使用自定义的转换时间
//        try {
//            Field field = ViewPager.class.getDeclaredField("mScroller");
//            field.setAccessible(true);
//            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(),
//                    new AccelerateInterpolator());
//            field.set(viewPager, scroller);
//            scroller.setmDuration(PAGER_TRANSLATE_TIME);
//        } catch (Exception e) {
//            Log.e(TAG, "", e);
//        }

        registerFragments = new ArrayList<>();

        addPhoneView();
        
        addVerifyCodeView();
        
        addUserInfoView();
    }

    private void addUserInfoView() {
        UserInfoFragment userInfoFragment = new UserInfoFragment();
        registerFragments.add(userInfoFragment);
    }

    private void addVerifyCodeView() {
        CodeFragment codeFragment = new CodeFragment();
        registerFragments.add(codeFragment);
    }

    private void addPhoneView() {
        PhoneFragment phoneFragment = new PhoneFragment();
        registerFragments.add(phoneFragment);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void configView() {
        titleBar.setDefault("注册--获取验证码");
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new RegisterPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:titleBar.setTitle("注册--获取验证码");break;
                    case 1:titleBar.setTitle("注册--验证手机号");break;
                    case 2:titleBar.setTitle("注册--基本信息");break;
                        default:titleBar.setTitle("注册");break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ci_register.setViewPager(viewPager);

        mHandler = new MyHandler(this, null);

        getQiniuToken();

        initSMS();
    }

    private void getCode(){
        if(sms_status == STATUS_VERIFY_SUC){
            sendToast("手机号已经验证通过");
            return ;
        }

        if(sms_status >= STATUS_SEND_SUC && sms_status <= STATUS_SEND_SUC){
            sendToast("验证码已发送，稍后重试");
            return ;
        }

        // send
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(met_phone.getText().toString());
        if(m.matches()){
            if(sendMessageDialog == null){
                sendMessageDialog = new MaterialDialog(RegisterActivity.this);
                sendMessageDialog.setTitle("发送验证码");
                sendMessageDialog.setMessage("即将给手机号“+86" + met_phone.getText().toString() + "”发送验证码");
                sendMessageDialog.setPositiveButton("好的", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        met_phone.setEnabled(false);
                        apb_getcode.setEnabled(false);
                        apb_getcode.setProgress(50);
                        sms_status = STATUS_IS_SENDING;
                        SMSSDK.getVerificationCode("+86", met_phone.getText().toString());
                        sendMessageDialog.dismiss();

//                        if(updateButtonThread != null && updateButtonThread.isAlive()){
//
//                        } else{
//                            // 设置计时以及try again
//                            updateButtonThread = new UpdateButtonThread();
//                            updateButtonThread.start();
//                        }

                    }
                });
                sendMessageDialog.setNegativeButton("稍等", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        met_phone .setEnabled(true);
                        sendMessageDialog.dismiss();
                    }
                });
            }
            sendMessageDialog.show();
        } else{
            sendToast("请输出正确的手机号码");
            return ;
        }
    }

    private void verifyCode(){
        if(sms_status < STATUS_SEND_SUC){
            sendToast("还未发送验证码");
            return;
        }

        String code = met_code.getText().toString();
        // 最短为4位
        if (StringUtil.isNullOrEmpty(code) || met_code.getText().toString().length() < 4) {
            return;
        }
        SMSSDK.submitVerificationCode("+86", met_phone.getText().toString(), met_code.getText().toString());
        sms_status = STATUS_IS_VERIFYING;
        apb_verifycode.setEnabled(false);
        apb_verifycode.setProgress(50);
    }

    private void getQiniuToken(){
        Token.Request11001 request11001 = new Token.Request11001();
        request11001.common = RequestUtil.getProtoCommon(11001, System.currentTimeMillis());
        new GetQiniuTokenThread(mHandler, request11001).start();
    }

    private void doRegiste(){
        apb_register.setMode(ActionProcessButton.Mode.ENDLESS);
        apb_register.setProgress(50);
        met_username.setEnabled(false);
        met_password.setEnabled(false);
        civ_avatar.setEnabled(false);
        tv_sex.setEnabled(false);
        new RegisterThread().start();
    }

    public static boolean checkUserName(String userName) {
        String regex = "^[^('\"\\\\?)]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(userName);
        return m.matches();
    }

    public static boolean checkPassword(String password) {
        String regex = "^[0-9a-zA-Z]{6,16}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
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
                civ_avatar.setImageBitmap(bitmap);
                if (avatarBitmap != null && !avatarBitmap.isRecycled()){
                    avatarBitmap.recycle();
                }
                avatarBitmap = bitmap;
                break;

        }
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

        if (sms_status != STATUS_VERIFY_SUC && ! Constant.DEBUG){ //TODO debug是不用验证手机号
            sendToast("手机号还未验证");
            return ;
        }

        String username = met_username.getText().toString();

        if(!StringUtil.isNotEmpty(username)){
            sendToast("用户名不能为空");
            return;
        }

        if(!checkUserName(username) || username.length() > Constant.MAX_LENGTH_USER_NAME){
            sendToast("用户名不合法");
            return;
        }

        String password = met_password.getText().toString();
        if(!StringUtil.isNotEmpty(password)){
            sendToast("密码不能为空");
            return;
        }

        if(!checkPassword(password)){
            sendToast("密码只能有字母和数字组成，且长度在6-16位");
            return;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        UploadManager uploadManager = new UploadManager();
        byte[] data = outputStream.toByteArray();
        uploadManager.put(data, StringUtil.generatorQiniuKey(), qiniuToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo responseInfo, JSONObject jsonObject) {
                avatar_key = key;
                doRegiste();
            }
        }, null);              // option可以尝试使用，高级策略
        try{
            outputStream.close();
        } catch (Exception e){

        }
    }

    // tools
    private EventHandler eventHandler;
    private int sms_status = -1;
    private static final int STATUS_START = -1;
    private static final int STATUS_IS_SENDING = 0;
    private static final int STATUS_SEND_SUC = 1;
    private static final int STATUS_IS_VERIFYING = 2;
    private static final int STATUS_VERIFY_SUC = 3;
    private int verify_error_times = 0;

    private void initSMS() {

        // 这个是非主线程的handler，不能直接使用
        eventHandler = new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        mHandler.sendEmptyMessage(KEY_VERIFY_SUC);
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        mHandler.sendEmptyMessage(KEY_GET_VERIFY_SUC);
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    mHandler.sendEmptyMessage(KEY_VERIFY_ERROR);
                }
            }
        };
        SMSSDK.registerEventHandler(eventHandler); //注册短信回调
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterEventHandler(eventHandler);
        super.onDestroy();
    }

    private MyHandler mHandler;
    private static final int KEY_REGISTER_SUC = 101;
    private static final int KEY_VERIFY_SUC = 102;
    private static final int KEY_GET_VERIFY_SUC = 103;
    private static final int KEY_VERIFY_ERROR = 104;     // 验证三次失败
    private static final int KEY_UPDATE_BUTTON = 201;
    private static final int KEY_UPDATE_BUTTON_ERROR = 202;
    private static final int KEY_UPDATE_BUTTON_FINISH = 203;
    private static final int KEY_PHONE_CAN_USE = 301;
    private static final int KEY_PHONE_CANNOT_USE = 302;

    class MyHandler extends BaseHandler{

        public MyHandler(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
            super(context, mSwipeRefreshLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case KEY_VERIFY_SUC:
                    if(sms_status == STATUS_IS_VERIFYING) {
                        met_code.setEnabled(false);
                        met_phone.setEnabled(false);
                        sms_status = STATUS_VERIFY_SUC;
                        apb_verifycode.setProgress(100);
                        viewPager.setCurrentItem(2);
                    }
                    break;
                case KEY_GET_VERIFY_SUC:
                    sendToast("发送成功");
                    apb_getcode.setProgress(100);
                    sms_status = STATUS_SEND_SUC;
                    viewPager.setCurrentItem(1);
                    break;

                case KEY_VERIFY_ERROR:
                    verify_error_times ++;
                    if(verify_error_times >= 3) {
                        met_phone.setEnabled(true);
                        met_code.setEnabled(true);
                        apb_getcode.setEnabled(true);
                        apb_getcode.setProgress(0);
                        apb_verifycode.setEnabled(true);
                        apb_verifycode.setProgress(0);
                        sendToast("失败次数过多，请重新获取");
                        viewPager.setCurrentItem(0);
                        sms_status = STATUS_START;
                        verify_error_times = 0;
                    } else{
                        met_code.setEnabled(true);
                        sms_status = STATUS_SEND_SUC;
                        apb_verifycode.setEnabled(true);
                        apb_verifycode.setProgress(-1);
                    }
                    break;

                case KEY_REGISTER_SUC:
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra(Constant.PHONE, met_phone.getText().toString());
                    intent.putExtra(Constant.PASSWORD, met_password.getText().toString());
                    startActivity(intent);
                    finish();
                    break;

                // 发送验证码相关
                case KEY_UPDATE_BUTTON:
                    if(sms_status != STATUS_VERIFY_SUC){
                        int second = msg.arg1;
//                        apb_send_message.setText(second + "s");
                    }
                    break;
                case KEY_UPDATE_BUTTON_ERROR:
                    // 计时失败
                    if(sms_status != STATUS_VERIFY_SUC) {
//                        apb_send_message.setText("OK");
//                        et_phone.setEnabled(true);
//                        et_verify.setEnabled(true);
                        sms_status = STATUS_START;
                    }
                    break;
                case KEY_UPDATE_BUTTON_FINISH:
                    if(sms_status != STATUS_VERIFY_SUC) {
                        sms_status = STATUS_START;
                        sendToastLong("如果您尝试多次未收到验证码，请通过“合作”与我们联系。");
                    }
                    break;

                case KEY_GET_QINIU_TOKEN_SUC:
                    Token.Response11001.Data data = (Token.Response11001.Data) msg.obj;
                    qiniuToken = data.qiniuToken;
                    bucketName = data.bucketName;
                    break;

                case KEY_PHONE_CAN_USE:
                    getCode();
                    break;

                case KEY_PHONE_CANNOT_USE:
                    sendToast("手机号码已经注册");
                    apb_getcode.setProgress(0);
                    break;
            }
        }
    }


    class RegisterPagerAdapter extends FragmentStatePagerAdapter {

        public RegisterPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return registerFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return registerFragments.get(position);
        }
    }

    class PhoneFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.view_register_phone, null);
            apb_getcode = (ActionProcessButton) view.findViewById(R.id.apb_get_code);
            met_phone = (MaterialEditText) view.findViewById(R.id.et_phone);

            apb_getcode.setMode(ActionProcessButton.Mode.ENDLESS);
            apb_getcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    if (met_phone.getText().toString().length() > 8) {
                        new VerifyPhoneThread().start();
                    }
                }
            });

            return view;
        }
    }

    class CodeFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.view_register_code, null);
            met_code = (MaterialEditText) view.findViewById(R.id.et_code);
            apb_verifycode = (ActionProcessButton) view.findViewById(R.id.apb_verify_code);
            apb_verifycode.setMode(ActionProcessButton.Mode.ENDLESS);
            apb_verifycode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    verifyCode();
                }
            });
            return view;
        }
    }

    class UserInfoFragment extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.view_register_userinfo, null);
            met_username = (MaterialEditText) view.findViewById(R.id.et_user_name);
            met_password = (MaterialEditText) view.findViewById(R.id.et_password);
            apb_register = (ActionProcessButton) view.findViewById(R.id.apb_registe);
            tv_sex = (TextView) view.findViewById(R.id.tv_select_sex);
            civ_avatar = (CircleImageView) view.findViewById(R.id.civ_choose_avatar);

            civ_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chooseImageDialog == null) {
                        chooseImageDialog = MaterialDialogFactory.createTwoOptionMd(RegisterActivity.this, new String[]{"相册", "拍照"}, false, 0);
                        chooseImageDialog.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                            @Override
                            public void onOptionChoose(int index) {
                                if (index == 0) {
                                    startChoosePhoto();
                                } else if (index == 1) {
                                    startTakePhoto();
                                } else {

                                }
                                chooseImageDialog.dismiss();
                            }
                        });
                        chooseImageDialog.setTitle("选择方式");
                        chooseImageDialog.setCanceledOnTouchOutside(true);
                    }
                    chooseImageDialog.show();
                }
            });

            tv_sex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chooseSexDialog == null) {
                        chooseSexDialog = MaterialDialogFactory.createTwoOptionMd(RegisterActivity.this, new String[]{"男", "女"}, true, 0);
                        chooseSexDialog.setOnOptionChooseListener(new TwoOptionMaterialDialog.OnOptionChooseListener() {
                            @Override
                            public void onOptionChoose(int index) {
                                if (index == 0) {
                                    tv_sex.setText("性别：男");
                                    sex = Common.MALE;
                                } else if (index == 1) {
                                    tv_sex.setText("性别：女");
                                    sex = Common.FEMALE;
                                } else {

                                }
                                chooseSexDialog.dismiss();
                            }
                        });
                        chooseSexDialog.setTitle("选择性别");
                        chooseSexDialog.setCanceledOnTouchOutside(true);
                    }
                    chooseSexDialog.show();
                }
            });

            apb_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 上传成功后会自动注册
                    uploadBitmap(avatarBitmap);
                }
            });

            met_username.setMaxCharacters(Constant.MAX_LENGTH_USER_NAME);

            return view;
        }
    }

    class RegisterThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10001;
            Pilot.Request10001 request = new Pilot.Request10001();
            Pilot.Request10001.Params params = new Pilot.Request10001.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.avatarKey = avatar_key;
            params.bucketName = bucketName;
            params.phone = met_phone.getText().toString();
            params.username = met_username.getText().toString();
            params.password = Md5.encode(met_password.getText().toString()).toUpperCase();
            params.sex = sex;

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_REGISTER, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10001 response = Pilot.Response10001.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            Message msg = Message.obtain();
                            msg.what = KEY_REGISTER_SUC;
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

    class VerifyPhoneThread extends Thread{
        @Override
        public void run() {
            super.run();
            long currentMills = System.currentTimeMillis();
            int cmdid = 10016;
            Pilot.Request10016 request = new Pilot.Request10016();
            Pilot.Request10016.Params params = new Pilot.Request10016.Params();
            Common.RequestCommon common = RequestUtil.getProtoCommon(cmdid, currentMills);
            request.common = common;
            request.params = params;

            params.phone = met_phone.getText().toString();

            byte[] result = RequestUtil.postWithProtobuf(request, UrlUtil.URL_VERIFY_PHONE_CAN_USE, cmdid, currentMills);
            if (null != result){
                // 加载成功
                try{
                    Pilot.Response10016 response = Pilot.Response10016.parseFrom(result);

                    if (response.common != null){
                        if(response.common.code == 0){
                            if(response.data.canUser) {
                                Message msg = Message.obtain();
                                msg.what = KEY_PHONE_CAN_USE;
                                mHandler.sendMessage(msg);
                            } else{
                                Message msg = Message.obtain();
                                msg.what = KEY_PHONE_CANNOT_USE;
                                mHandler.sendMessage(msg);
                            }
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
