package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Poetry;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;
import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.factory.JinrishiciFactory;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.PoetySentence;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private Button positionButton;  //定位显示
    public LocationClient mLocationClient;  //定位实现
    public static String currentCity= "";   //当前定位城市
    private ScrollView weatherLayout;  //滑动显示区
    public DrawerLayout drawerLayout;  //滑动菜单
    private Button navButton;  //启动滑动菜单的按钮
    private LinearLayout forecastLayout;  //近几天天气情况
    private ImageView bingPicImg;  //背景图片
    public SwipeRefreshLayout swipeRefreshLayout;  //下拉刷新
    private String weatherId;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private Button myButton;



    private Button addButton;

    private TextView shici;
    private Typeface face;
    private TextView poem;
    private TextView title;
    private TextView dynasty;
    private  TextView author;
    private TextView content;
    private  TextView reason;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationClient=new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());//位置监听器

        if(Build.VERSION.SDK_INT >= 21){  //背景图与状态栏融合，对SDK版本有要求
            View decorView = getWindow().getDecorView(); // 获取DecorView
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            ); // 改变系统UI
            getWindow().setStatusBarColor(Color.TRANSPARENT); // 设置透明
        }

        setContentView(R.layout.activity_weather);
        positionButton = findViewById(R.id.position_button);
        //初始化各组件
        poem = findViewById(R.id.data_content);
        title = findViewById(R.id.origin_title);
        dynasty = findViewById(R.id.origin_dynasty);
        author = findViewById(R.id.origin_author);
        content = findViewById(R.id.origin_content);
        reason = findViewById(R.id.reason);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);// 设置下拉刷新进度条的颜色
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);//设置当前主题为日间主题

        addButton = findViewById(R.id.add_city);
        //常用城市天气切换
        Intent intent_add = getIntent();
        String Id = intent_add.getStringExtra("id");
        if (!(TextUtils.isEmpty(Id))){
            requestWeather(Id);
        }
        shici = findViewById(R.id.data_content);// 找到对应的诗词控件
        face = Typeface.createFromAsset(shici.getContext().getAssets(), "newFont.TTF");// 这里是使用Typeface获取到放在assets里的字体
        shici.setTypeface(face);// 将字体运用到控件当中

        myButton=findViewById(R.id.btn1);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        TextView text = findViewById(R.id.text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO
                        ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                // 同样需要调用recreate方法使之生效
                recreate();
            }
        });//选择主题切换


        JinrishiciFactory.init(this);
        JinrishiciClient client = new JinrishiciClient();
        client.getOneSentenceBackground(new JinrishiciCallback() {
            @Override
            public void done(PoetySentence poetySentence) {
                //TODO do something
                getPoemToken();
                title.setText(poetySentence.getData().getOrigin().getTitle());
                dynasty.setText(poetySentence.getData().getOrigin().getDynasty());
                author.setText(poetySentence.getData().getOrigin().getAuthor());
                String[] arr = new String[100];
                for(int i=0;i<=poetySentence.getData().getOrigin().getContent().size();i++){
                    arr[i]= String.valueOf(poetySentence.getData().getOrigin().getContent());
                    System.out.println(arr[i]);
                    content.setText(arr[i]);
                }
                String[] arr1 = new String[5];
                for(int i=0;i<=poetySentence.getData().getMatchTags().size();i++){
                    arr1[i] = String.valueOf(poetySentence.getData().getMatchTags());
                    System.out.println(arr1[i]);
                }
                reason.setText(String.valueOf(arr1[0]));
                poem.setText(poetySentence.getData().getContent());
                System.out.println(poetySentence.getData().getContent());
            }

            @Override
            public void error(JinrishiciRuntimeException e) {
                //TODO do something else
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询数据
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE); // 暂时将ScrollView设为不可见
            requestWeather(weatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // 设置下拉刷新监听器,请求重新加载天气
                requestWeather(weatherId);
            }
        });

        String bingPic = prefs.getString("bing_pic",null); // 尝试从缓存中读取背景图
        if(bingPic != null){
           // Glide.with(this).load(bingPic).into(bingPicImg);  //如果有则用Glide加载
        }else{
       //     loadBingPic(); //如果没有则调用此方法网络请求加载
        }



        //打开滑动菜单
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START); // 打开滑动菜单
            }
        });

        //添加常用城市跳转页面
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //跳转到添加常用城市界面
                Intent intent = new Intent(WeatherActivity.this,AddCityActivity.class);//从天气页面跳转到添加常用城市界面
                startActivity(intent);
            }
        });

        //点击定位
        positionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocation();
                requestWeather1(weatherId);
                titleCity.setText(WeatherActivity.currentCity);
                Toast.makeText(WeatherActivity.this,"定位当前城市成功！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 根据天气Id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=6ebfd087db8144cbaab3884bb8f4b19d"; // 这里的key设置为第一个实训中获取到的API Key
        // 组装地址并发出请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText); // 将返回数据转换为Weather对象
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestLocation();
                        if(weather!=null && "ok".equals(weather.status)){
                            //缓存有效的weather对象(实际上缓存的是字符串)
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather); // 显示内容
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false); // 表示刷新事件结束并隐藏刷新进度条
                    }
                });
            //    loadBingPic();
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
      //  loadBingPic();
    }
    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
    private void showWeatherInfo(Weather weather){
        // 从Weather对象中获取数据
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1]; //按24小时计时的时间
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;
        // 将数据显示到对应控件上
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){ // 循环处理每天的天气信息
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            // 加载布局
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            // 设置数据
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            // 添加到父布局
            forecastLayout.addView(view);
        }
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度: " + weather.suggestion.comfort.info;
        String carWash = "洗车指数: " + weather.suggestion.carWash.info;
        String sport = "运动建议: "+ weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE); // 将天气信息设置为可见
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string(); // 获取背景图链接
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply(); // 将北京图链接存到 SharedPreferences 中
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg); // 用 Glide 加载图片
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    //实现BDAbstractLocationListener接口
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder currentPosition=new StringBuilder();
                    currentPosition.append(location.getDistrict());//获取区/县信息
                    currentCity =location.getDistrict();
                    titleCity.setText(currentPosition);
                }
            });
        }
    }

    public void requestLocation(){
        initLocation();
        mLocationClient.start();//响应监听器
    }

    public void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    /**
     * 根据天气Id请求城市天气信息
     */
    public void requestWeather1(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=6ebfd087db8144cbaab3884bb8f4b19d"; // 这里的key设置为第一个实训中获取到的API Key
        // 组装地址并发出请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText); // 将返回数据转换为Weather对象
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestLocation();
                        if(weather!=null && "ok".equals(weather.status)){
                            //缓存有效的weather对象(实际上缓存的是字符串)
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo1(weather); // 显示内容
                        }else{
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false); // 表示刷新事件结束并隐藏刷新进度条
                    }
                });
               // loadBingPic();
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
       // loadBingPic();
    }


    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
    private void showWeatherInfo1(Weather weather){
        // 从Weather对象中获取数据
        String cityName = WeatherActivity.currentCity;
        String updateTime = weather.basic.update.updateTime.split(" ")[1]; //按24小时计时的时间
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;
        // 将数据显示到对应控件上
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){ // 循环处理每天的天气信息
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            // 加载布局
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            // 设置数据
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            // 添加到父布局
            forecastLayout.addView(view);
        }
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度: " + weather.suggestion.comfort.info;
        String carWash = "洗车指数: " + weather.suggestion.carWash.info;
        String sport = "运动建议: "+ weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE); // 将天气信息设置为可见
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }



    /** 本段代码实现了从今日诗词api获得token
     * 并存储在本地sharepreference里，
     * 便于后面返回json数据使用 **/

    public void getPoemToken(){
        String url = "https://v2.jinrishici.com/token";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("获取Token失败！");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response != null){
                    try {
                        String responseData=response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        String token = jsonObject.getString(String.valueOf("data"));
                        System.out.println("当前获取token为："+token);
                        System.out.println("获取Token成功！！");
                        if (check()){
                            System.out.println("本地已有token数据，请直接使用！！");
                            read();
                        }else{
                            saveToPre(token);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }}
        });
    }

    /** sharepreference类的存储和读取方法
     * @return**/
    private String readPre() {
        SharedPreferences sp = getSharedPreferences("TokenName",MODE_PRIVATE);
        String Token = sp.getString("Token","");
        if(Token != null){
            System.out.println("从sharepreference读取Token成功！！");
        }else {
            System.out.println("本地无数据，请重新获取！");
        }

        return Token;

    }
    private String read(){
        SharedPreferences sp = getSharedPreferences("TokenName",MODE_PRIVATE);
        String Token = sp.getString("Token","");
        System.out.println("本地Token为：" +Token);
        return Token;
    }

    /** 检查本地是否有数据**/
    private boolean check(){
        if(readPre() !=null){
            return true;
        }else{
            return false;
        }

    }

    /** 存储token到本地**/
    public void saveToPre(String token) {
        SharedPreferences sp = getSharedPreferences("TokenName",MODE_PRIVATE);
        SharedPreferences.Editor edit  = sp.edit();
        edit.putString("Token",token);
        edit.apply();
        System.out.println("存入Token成功！！");
    }

    public void getThePoem(){
        String TOK =  read();
        Request.Builder builder = new Request.Builder()
                .url("https://v2.jinrishici.com/sentence")
                .addHeader( "X-User-Token", TOK );
        Request build = builder.build();

        OkHttpClient client = new OkHttpClient.Builder().readTimeout( 5000, TimeUnit.SECONDS ).build();
        Call call = client.newCall( build );
        call.enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("获取数据失败！");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response != null){
                    try {
                        final String responseData = response.body().string();
                        final Poetry p = Utility.handlePoetryResponse(responseData);
                        JSONObject poetry = new JSONObject(responseData);
                        System.out.println(poetry);
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("poetry", responseData);
                        editor.clear();
                        editor.apply();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void showPoetry(Poetry poetry){
        String content = poetry.data.singlePoem;
        TextView theshowpoem =  findViewById(R.id.data_content);
        theshowpoem.setText(content);
    }


}