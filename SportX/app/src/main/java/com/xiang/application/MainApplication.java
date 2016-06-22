package com.xiang.application;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.xiang.Util.Constant;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.SMSSDK;
import io.rong.imkit.RongIM;

/**
 * Created by 祥祥 on 2016/4/18.
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        File cacheDir = new File(Constant.getSDPath() + "/SportX/img/");

        if (! cacheDir.exists()){
            cacheDir.mkdirs();
        }

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .taskExecutor(DefaultConfigurationFactory.createExecutor(3, Thread.NORM_PRIORITY, QueueProcessingType.FIFO))
                .taskExecutorForCachedImages(DefaultConfigurationFactory.createExecutor(3, Thread.NORM_PRIORITY, QueueProcessingType.FIFO))
                .threadPoolSize(6) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(4 * 1024 * 1024))
                .memoryCacheSize(4 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiscCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(200)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
                .imageDecoder(new BaseImageDecoder(Constant.DEBUG_IMAGELOADER)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
//                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(configuration);

        RongIM.init(this);

        SMSSDK.initSDK(this, "12ce9d19c6a94", "9831ef6e5a61a915c5a1b9f75a589db5");

        JPushInterface.setDebugMode(Constant.DEBUG);
        JPushInterface.init(this);


        // 百度地图
        SDKInitializer.initialize(getApplicationContext());

        // 友盟统计
//        MobclickAgent.setScenarioType(this, );
    }

}
