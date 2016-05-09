package com.xiang.sportx;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiang.Util.ArrayUtil;
import com.xiang.Util.Constant;
import com.xiang.adapter.ImageViewAdapter;
import com.xiang.factory.DisplayOptionsFactory;
import com.xiang.view.MyTitleBar;

import java.util.ArrayList;
import java.util.List;

public class ImageAndTextActivity extends BaseAppCompatActivity {

    // views
    private ViewPager viewPager;
    private MyTitleBar titleBar;
    private TextView tv_count;


    private List<View> views = new ArrayList<>();

    private List<String> imageUrls = new ArrayList<>();
    private ImageViewAdapter imageViewAdapter;

    // tools
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions coverOptions = DisplayOptionsFactory.createCoverIconOption();

    // data
    private int currentindex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_image_and_text);

        viewPager = (ViewPager) findViewById(R.id.vp_image_text);
        titleBar = (MyTitleBar) findViewById(R.id.titleBar);
        titleBar.setTitleBackgroundColor(R.color.black);
        titleBar.setBackButtomBackgroundColor(R.color.black);
        tv_count = (TextView) findViewById(R.id.tv_count);
    }

    @Override
    protected void initData() {
//        imageUrls.addAll();
        currentindex = getIntent().getIntExtra(Constant.CURRENT_INDEX, 0);
        String[] images = getIntent().getStringArrayExtra(Constant.IMAGES);
        imageUrls.addAll(ArrayUtil.Array2List(images));
    }

    @Override
    protected void configView() {
        titleBar.setBackButton(R.mipmap.back, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitle("");
        titleBar.setMoreButton(0, false, null);


        for(String url:imageUrls){
            View view = LayoutInflater.from(this).inflate(R.layout.view_image, null);
            ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);
            imageLoader.displayImage(url, iv_image, coverOptions);
            views.add(view);
        }

        imageViewAdapter = new ImageViewAdapter(views);
        viewPager.setAdapter(imageViewAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int currentPage = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {  // 0是滑完了
                    setCurrentIndex(currentPage);
                }
            }
        });

        viewPager.setCurrentItem(currentindex);
        setCurrentIndex(currentindex);
    }

    private void setCurrentIndex(int index){
        tv_count.setText((index + 1) + "/" + imageUrls.size());
    }
}
