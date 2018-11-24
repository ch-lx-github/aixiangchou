package com.gczy.axc.aixiangchou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixin on 18/11/21.
 */

public class GuideActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 3;

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    public List<Fragment> fragments = new ArrayList<>();

    public int[] img = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {
        pager = (ViewPager) findViewById(R.id.pager);
        GuideFragment fragment0 = new GuideFragment();
        Bundle bundle0 = new Bundle();
        bundle0.putInt("position",0);
        fragment0.setArguments(bundle0);
        fragments.add(fragment0);
        GuideFragment fragment1 = new GuideFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("position",1);
        fragment1.setArguments(bundle1);
        fragments.add(fragment1);
        GuideFragment fragment2 = new GuideFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("position",2);
        fragment2.setArguments(bundle2);
        fragments.add(fragment2);
        pagerAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(),fragments);
        pager.setAdapter(pagerAdapter);
//        pager.setPageTransformer(true, new DepthPageTransformer());
//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (position == NUM_PAGES - 1) {
//                    indicator.setVisibility(View.GONE);
//                } else {
//                    indicator.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

//        indicator = (CircleIndicator) findViewById(R.id.indicator);
//        if (indicator != null) {
//            indicator.setViewPager(pager);
//        }
    }

//    public class GuideAdapter extends PagerAdapter {
//        //图片资源合集:ViewPager滚动的页面种类
//        private int[] mImageId = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
//        private Context mContext;
//
//        public int[] getImageId() {
//            return mImageId;
//        }
//
//        //构造函数
//        public GuideAdapter(Context context) {
//            super();
//            this.mContext = context;
//        }
//
//        //返回填充ViewPager页面的数量
//        @Override
//        public int getCount() {
//            return mImageId.length;
//        }
//
//        //销毁ViewPager内某个页面时调用
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;//固定是view == object
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            RelativeLayout rl = new RelativeLayout(mContext);
//            ImageView imageView = new ImageView(mContext);
//            //设置背景资源
//            imageView.setBackgroundResource(mImageId[position]);
//            rl.addView(imageView);
//            if (position == 2){
//                TextView tv = new TextView(mContext);
//                tv.setText("立即体验");
//                tv.setTextSize(22);
//                tv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        startActivity(new Intent(mContext, MainActivity.class));
//                        SharedPreferences sharedPreferences = getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putBoolean("firstStart", false);
//                        editor.apply();
//                        finish();
//                    }
//                });
//            }
//            container.addView(rl);
//            return rl;
//        }
//
//    }

    class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList = new ArrayList<Fragment>();

        public ViewPagerFragmentAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }
    }
}
