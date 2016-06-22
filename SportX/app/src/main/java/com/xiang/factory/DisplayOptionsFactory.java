package com.xiang.factory;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.xiang.sportx.R;


/**
 * Created by 祥祥 on 2016/3/3.
 */
public class DisplayOptionsFactory {
    public static DisplayImageOptions createAvatarIconOption(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.image) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.image) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.image) // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(300)  // 下载前的延迟时间
                .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
                .displayer(new FadeInBitmapDisplayer(300)) // default  还可以设置圆角图片new RoundedBitmapDisplayer(20)
                .build();
    }

    /**
     * 列表中的图片的普通图
     * @return
     */
    public static DisplayImageOptions createNormalImageOption(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.image) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.image) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.image) // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(300)  // 下载前的延迟时间
                .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
                .displayer(new FadeInBitmapDisplayer(300)) // default  还可以设置圆角图片new RoundedBitmapDisplayer(20)
                .build();
    }

    public static DisplayImageOptions createBigAvatarOption(int drawable){
        if(0 == drawable){
            return new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.image) // 设置图片下载期间显示的图片
                    .showImageForEmptyUri(R.mipmap.image) // 设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.mipmap.image) // 设置图片加载或解码过程中发生错误显示的图片
                    .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                    .delayBeforeLoading(300)  // 下载前的延迟时间
                    .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
                    .displayer(new FadeInBitmapDisplayer(300)) // default  还可以设置圆角图片new RoundedBitmapDisplayer(20)
                    .build();
        }
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(drawable) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(drawable) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(drawable) // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(300)  // 下载前的延迟时间
                .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
                .displayer(new FadeInBitmapDisplayer(300)) // default  还可以设置圆角图片new RoundedBitmapDisplayer(20)
                .build();
    }

    public static DisplayImageOptions createCoverIconOption(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.image) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.image) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.image) // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(300)  // 下载前的延迟时间
                .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // default  设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565) // default 设置图片的解码类型
                .displayer(new FadeInBitmapDisplayer(300)) // default  还可以设置圆角图片new RoundedBitmapDisplayer(20)
                .build();
    }
}
