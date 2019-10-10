package com.example.bannerlib;


/**
 * 全局变量
 */
public class BannerConst {


    //Banner测试Id为123
    //强更测试id为T123456

    //Banner加载
    public final static String BANNER_URL = "http://47.56.177.143/ADMApp/1230/";

    //强更加载
    public final static String JUMP_URL = "http://apk.uu-app.com:12569/getAppConfig.php?appid=T1203456";

    //默认Banner
    public final static String DEFAULT_URL = "https://avatars0.githubusercontent.com/u/28915646?s=460&v=4";

    //解析后banner
    public final static int NONE_BANNER = 0;

    //解析后有banner
    public final static int HAVE_BANNER = 1;

    //解析后可以跳转
    public final static int CAN_JUMP = 2;

}

