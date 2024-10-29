package com.tbi.chatapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.ShimmerLayoutBinding;

public class Loader {

    private static Loader instance;
    private static ShimmerLayoutBinding shimmer;
    private View mainContentView;

    public static Loader getInstance() {
        if (instance == null) {
            instance = new Loader();
        }
        return instance;
    }

    public void showLoader(Context context) {

        try{
            hideLoader();

            LayoutInflater inflater = LayoutInflater.from(context);
            shimmer = DataBindingUtil.inflate(inflater, R.layout.shimmer_layout, null, false);
            mainContentView = ((Activity) context).findViewById(android.R.id.content);

            ViewGroup parentView = (ViewGroup) mainContentView.getParent();
            parentView.removeView(mainContentView);
            parentView.addView(shimmer.getRoot());


            shimmer.shimmerViewContainer.setVisibility(View.VISIBLE);
            shimmer.shimmerViewContainer.startShimmer();

        }catch (Exception e){
            throw new RuntimeException("Showing Dialog is Throw exception msg = "+ e.getMessage());
        }
    }

    public void hideLoader(Activity activity) {
        try {
            if (activity != null) {
                activity.runOnUiThread(() -> hideLoader());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void hideLoader() {
        try {
            if (shimmer != null && mainContentView != null) {
                ViewGroup parentView = (ViewGroup) shimmer.getRoot().getParent();
                if (parentView != null) {
                    parentView.removeView(shimmer.getRoot());
                    parentView.addView(mainContentView);
                }
                shimmer.shimmerViewContainer.setVisibility(View.GONE);
                shimmer.shimmerViewContainer.stopShimmer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
