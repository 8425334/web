package com.example.bannerlib;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络加载工具类
 */
public class HttpUtil {

    /**
     *  获取Banner
     * @return
     * @throws IOException
     */
    public static String getBannerJson() throws IOException {
       return requestForResult(BannerConst.BANNER_URL);
    }

    /**
     * 获取强更地址
     * @return
     */
    public static String getJumpJson() throws IOException {
        return requestForResult(BannerConst.JUMP_URL);
    }

    /**
     *  网络加载(默认为get方法)
     * @param url url
     * @return
     */
    private static String requestForResult(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        Log.i("TAG", result);
        return  result;
    }
}
