package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class AddCityActivity extends AppCompatActivity {

    private Button back_button;
    private Button add_city_button; //添加城市按钮
    public DrawerLayout drawerLayout; //滑动菜单
    private Button add_city_text1;//添加的城市名
    private Button del_city_button1;//删除当前添加的城市名
    private Button add_city_text2;
    private Button del_city_button2;
    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView(); // 获取DecorView
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            ); // 改变系统UI
            getWindow().setStatusBarColor(Color.TRANSPARENT); // 设置透明
        }

        setContentView(R.layout.add_city);

        //初始化各组件
        back_button = findViewById(R.id.back_button); //返回天气界面的按钮
        add_city_button = findViewById(R.id.add_city_button); //添加常用城市按钮
        drawerLayout = findViewById(R.id.drawer_layout);
        add_city_text1 = findViewById(R.id.add_city_text1); //添加的城市名
        del_city_button1 = findViewById(R.id.del_city_button1); //删除城市的按钮
        add_city_text2 = findViewById(R.id.add_city_text2);
        del_city_button2 = findViewById(R.id.del_city_button2);

        add_city_text1.setBackgroundColor(Color.rgb(255,255,255));
        add_city_text2.setBackgroundColor(Color.rgb(255,255,255));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        final SharedPreferences sp = this.getSharedPreferences("cityMsg", Context.MODE_PRIVATE);

        if(weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showCity(weather);
        }else{
            //无缓存时去服务器查询数据
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }

        //从城市管理界面返回到天气界面
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCityActivity.this,WeatherActivity.class);
                startActivity(intent);
            }
        });

        //添加常用城市，实现打开滑动菜单
        add_city_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START); // 打开滑动菜单
            }
        });


            //删除常用城市
            del_city_button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor ed = sp.edit();
                    if (!TextUtils.isEmpty(sp.getString("city2","null"))
                            && !(sp.getString("city2","null").equals("null"))){
                        String c1 = sp.getString("city2", "null");
                        ed.putString("city1",c1);
                        System.out.println(c1);
                        add_city_text1.setText(c1);
                        ed.remove("city2");
                        ed.putString("weatherId1",sp.getString("weatherId2","null"));
                        ed.remove("weatherId2");
                        ed.commit();
                        add_city_text2.setText(null);
                        add_city_text2.setVisibility(View.INVISIBLE);
                        del_city_button2.setVisibility(View.INVISIBLE);
                        System.out.println("将ct2覆盖ct1");
                        System.out.println("ct1:"+sp.getString("city1","null")+",ct2:"+sp.getString("city2","null"));
                    }else if (TextUtils.isEmpty(sp.getString("city2","null"))
                            || sp.getString("city2","null").equals("null")){
                        ed.remove("city1");
                        ed.remove("weatherId1");
                        ed.commit();
                        add_city_text1.setText(null);
                        add_city_text1.setVisibility(View.INVISIBLE);
                        del_city_button1.setVisibility(View.INVISIBLE);
                        System.out.println("删除了ct1");
                        System.out.println("ct1:"+sp.getString("city1","null")+",ct2:"+sp.getString("city2","null"));
                    }
                }
            });

            del_city_button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor ed = sp.edit();
                    if (!TextUtils.isEmpty(sp.getString("city2","null"))
                            && !(sp.getString("city2","null").equals("null"))){
                        ed.remove("city2");
                        ed.remove("weatherId2");
                        ed.commit();
                        add_city_text2.setText(null);
                        add_city_text2.setVisibility(View.INVISIBLE);
                        del_city_button2.setVisibility(View.INVISIBLE);
                        System.out.println("删除了ct2");
                        System.out.println("ct1:"+sp.getString("city1","null")+",ct2:"+sp.getString("city2","null"));

                    }
                }
            });

        /**
         * 点击城市名跳转到相应城市天气
         */
        add_city_text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = sp.edit();
                String weatherId = sp.getString("weatherId1","null");
                    //该碎片在WeatherActivity中，只需要刷新该活动
                    Intent intent_add = new Intent(AddCityActivity.this,WeatherActivity.class);
                    intent_add.putExtra("id",weatherId);
                    startActivity(intent_add);
            }
        });

        add_city_text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = sp.edit();
                String weatherId = sp.getString("weatherId2","null");
                //该碎片在WeatherActivity中，只需要刷新该活动
                Intent intent_add = new Intent(AddCityActivity.this,WeatherActivity.class);
                intent_add.putExtra("id",weatherId);
                startActivity(intent_add);
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
                        if(weather!=null && "ok".equals(weather.status)){
                            //缓存有效的weather对象(实际上缓存的是字符串)
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(AddCityActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showCity(weather); // 显示内容

                        }else{
                            Toast.makeText(AddCityActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddCityActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /**
     * 处理并展示Weather实体类中的数据
     * @param weather
     */
   private void showCity(Weather weather){
       //获取SharedPreferences对象
       SharedPreferences sp = this.getSharedPreferences("cityMsg", Context.MODE_PRIVATE);
       //像SharedPreference中写入数据需要使用Editor
       SharedPreferences.Editor myeditor = sp.edit();

        if (TextUtils.isEmpty(add_city_text1.getText())){
            // 从Weather对象中获取数据
            String cityName1 = weather.basic.cityName;
            String weatherId1 =weather.basic.weatherId;
            //传入sp
            myeditor.putString("city1",cityName1);
            myeditor.putString("weatherId1",weatherId1);
            myeditor.commit();
            //将数据显示到控件上
            String cityName = sp.getString("city1","null");
            add_city_text1.setText(cityName);
            add_city_text1.setVisibility(View.VISIBLE);
            del_city_button1.setVisibility(View.VISIBLE);

            System.out.println("展示城市名，ct1");
            System.out.println("ct1:"+sp.getString("city1","null")+",ct2:"+sp.getString("city2","null"));

        } else if (TextUtils.isEmpty(add_city_text2.getText())){
            String cityName2 = weather.basic.cityName;
            String weatherId2 =weather.basic.weatherId;
            if (!(sp.getString("city1","").equals(cityName2))){
                myeditor.putString("city2",cityName2);
                myeditor.putString("weatherId2",weatherId2);
                myeditor.commit();
                String cityName = sp.getString("city2","null");
                add_city_text2.setText(cityName);
                add_city_text2.setVisibility(View.VISIBLE);
                del_city_button2.setVisibility(View.VISIBLE);

                System.out.println("展示城市名，ct2");
                System.out.println("ct1:"+sp.getString("city1","null")+",ct2:"+sp.getString("city2","null"));
            }

        }
    }

}
