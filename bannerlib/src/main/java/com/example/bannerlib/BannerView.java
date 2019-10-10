package com.example.bannerlib;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BannerView extends View{

    private List<String> banners, urls;

    private Context context;
    private Banner banner;

    public BannerView(Context context){
        super(context);
    }

    //初始化Handler
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what){
                //没有Banner返回
                case BannerConst.NONE_BANNER:{
                    Log.i("TAG", "没有收到Banner,设置为默认Banner");
                    //banner本身仅仅支持List方式加载Image
                    List<String> list = new ArrayList<>();
                    list.add(BannerConst.DEFAULT_URL);
                    //加载默认Banner(一张)
                    banner.setImages(list).setImageLoader(new GlideImageLoader()).start();
                    break;
                }
                //有Banner返回
                case BannerConst.HAVE_BANNER: {
                    //捕获空指针异常,防止内存泄漏
                    try {
                        banner.setImages(banners).setImageLoader(new GlideImageLoader()).start();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    //banner点击监听
                    banner.setOnBannerListener(new OnBannerListener() {
                        @Override
                        public void OnBannerClick(int position) {
                            Intent intent = new Intent(context, LotteryWeb.class);
                            Log.i("TAG", "收到Banner");
                            try {
                                String url = urls.get(position);
                                Log.i("TAG", "url" + url);
                                intent.putExtra("url", url);
                                intent.putExtra("canGoBack", false);
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            context.startActivity(intent);
                        }
                    });
                    break;
                }
                //可以跳转强更
                case BannerConst.CAN_JUMP:{
                    String url = (String)msg.obj;
                    Log.i("TAG", "收到强更跳转请求");
                    Intent intent = new Intent(context, LotteryWeb.class);
                    intent.putExtra("url", url);
                    intent.putExtra("canGoBack", true);
                    context.startActivity(intent);
                    break;
                }
            }
            return false;
        }
    });

    /**
     * 初始化所有数据
     */
    public BannerView(Context context, Banner banner) {
        super(context);
        this.context = context;
        this.banner = banner;

        //banner = findViewById(R.id.banner);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initBannerData();
                    initJumpData();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     *  初始化Banner数据
     * @throws JSONException
     * @throws IOException
     */
    private void initBannerData() throws JSONException, IOException {
        banners = new ArrayList<>();
        urls = new ArrayList<>();
        String bannerJson = HttpUtil.getBannerJson();
        JSONObject jsonObject = new JSONObject(bannerJson);
        Message message = new Message();
        int code = jsonObject.getInt("code");
        if (code != 200){
            message.what = BannerConst.NONE_BANNER;
            handler.sendMessage(message);
            return ;
        }
        JSONObject dataObject = jsonObject.getJSONObject("data");
        JSONArray bannerArray = dataObject.getJSONArray("images");
        for (int i = 0; i < bannerArray.length(); i++){
            JSONObject banner = (JSONObject) bannerArray.get(i);
            String banner_url = banner.getString("banner_url");
            String down_url = banner.getString("down_url");
            banners.add(banner_url);
            urls.add(down_url);
        }
        message.what = BannerConst.HAVE_BANNER;
        handler.sendMessage(message);
    }

    private void initJumpData() throws IOException, JSONException {
        String json = HttpUtil.getJumpJson();
        JSONObject object = new JSONObject(json);
        String status = object.getString("success");
        System.out.println(status.equals("true"));
        if (status.equals("true")){
            String url = object.getString("Url");
            Message message = new Message();
            message.what = BannerConst.CAN_JUMP;
            message.obj = url;
            handler.sendMessage(message);
        }
    }
}
