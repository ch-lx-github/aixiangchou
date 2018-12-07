package com.gczy.axc.aixiangchou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by lixin on 18/11/21.
 */

public class GuideFragment extends Fragment {

    private View mView;
    private ImageView ivImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_layout_guide, null);
        }
        Bundle arguments = getArguments();
        int position = arguments.getInt("position");
        TextView textView = mView.findViewById(R.id.tv_start);
        if (position == 2) {
            textView.setVisibility(View.VISIBLE);
            //1、通过WindowManager获取
//            DisplayMetrics dm = new DisplayMetrics();
//            int heigth = dm.heightPixels;
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
//            layoutParams.setMargins(0, 200, 0, 0);
//            textView.setLayoutParams(layoutParams);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("firstStart", false);
                    editor.apply();
                    getActivity().finish();
                }
            });
        }
        ivImg = mView.findViewById(R.id.iv_img);
        ivImg.setBackgroundResource(((GuideActivity) getActivity()).img[position]);
        return mView;
    }
}
