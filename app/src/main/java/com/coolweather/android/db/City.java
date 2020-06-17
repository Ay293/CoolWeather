package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    private int id; //DingWeiBianHao
<<<<<<< HEAD
    private String cityName;    //城市名字
    private int cityCode;   //市的代号
    private int provinceCode;   //当前市所属省的id
=======
    private String cityName;
    private int cityCode;
    private int provinceId;
>>>>>>> 02b2270336889e4747d8275357fe49223900ca0f

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
