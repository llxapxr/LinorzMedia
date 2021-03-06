package com.linorz.linorzmedia.main.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


import com.linorz.linorzmedia.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by linorz on 2016/7/13.
 */
public class LinorzApplication extends Application {
    private static LinorzApplication instance;
    private static DisplayImageOptions mOptions;
    private Context app_context;
    private SharedPreferences mSharedPreferences;
    public static int screenWidth, screenHeight;
    public static JSONArray lovelist;

    public static LinorzApplication getInstance() {
        return instance;
    }

    public static LinorzApplication getContext() {
        return instance;
    }

    public static DisplayImageOptions getOptions() {
        return mOptions;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        app_context = this.getApplicationContext();
        //初始化一些服务
        initImageLoader();
        screenWidth = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        screenHeight = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        //缓存
        mSharedPreferences = getSharedPreferences("LinorzMedia", Context.MODE_PRIVATE);
        String str_lovelist = mSharedPreferences.getString("lovelist", null);
        try {
            if (str_lovelist != null)
                lovelist = new JSONArray(str_lovelist);
            else
                lovelist = new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(200 * 1024 * 1024) // 200 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
        //统一使用
        mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.color.white)   //加载过程中
                .showImageForEmptyUri(R.color.white) //uri为空时
                .showImageOnFail(R.color.white)      //加载失败时
                .cacheOnDisk(true)
                .cacheInMemory(true)                             //允许cache在内存和磁盘中
                .bitmapConfig(Bitmap.Config.RGB_565)             //图片压缩质量参数
                .build();
    }

    public void saveLoveList() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("lovelist", lovelist.toString());
        editor.apply();
    }

}
