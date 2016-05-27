package com.xiang.sportx;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.adapter.ImageViewAdapter;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.view.TouchImageView;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class ImageAndTextActivity extends BaseAppCompatActivity {

    // views
    private ViewPager viewPager;
    private CircleIndicator ci_images;


    private List<View> views = new ArrayList<>();

    private List<String> imageUrls = new ArrayList<>();
    private ImageViewAdapter imageViewAdapter;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions coverOptions = DisplayOptionsFactory.createCoverIconOption();

    // data
    private int currentindex = 0;
    private boolean showIndicator = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_image_and_text);

        viewPager = (ViewPager) findViewById(R.id.vp_image_text);
        ci_images = (CircleIndicator) findViewById(R.id.ci_images);
    }

    @Override
    protected void initData() {
        currentindex = getIntent().getIntExtra(Constant.CURRENT_INDEX, 0);
        String[] images = getIntent().getStringArrayExtra(Constant.IMAGES);
        showIndicator = getIntent().getBooleanExtra(Constant.SHOW_INDICATOR, true);
        imageUrls.addAll(ArrayUtil.Array2List(images));
    }

    @Override
    protected void configView() {

        for(String url:imageUrls){
            View view = LayoutInflater.from(this).inflate(R.layout.view_image, null);
            TouchImageView iv_image = (TouchImageView) view.findViewById(R.id.iv_image);
            iv_image.setMaxZoom(2.0f);
            iv_image.setMinZoom(1.0f);
            iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            imageLoader.displayImage(url, iv_image, coverOptions);
            views.add(view);
        }

        imageViewAdapter = new ImageViewAdapter(views);
        viewPager.setAdapter(imageViewAdapter);

        ci_images.setViewPager(viewPager);

        viewPager.setCurrentItem(currentindex);

        ci_images.setVisibility(showIndicator ? View.VISIBLE: View.GONE);
    }

}
