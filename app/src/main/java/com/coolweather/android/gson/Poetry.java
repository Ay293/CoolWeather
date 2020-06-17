package com.coolweather.android.gson;


import com.google.gson.annotations.SerializedName;

public class Poetry {

    @SerializedName("status")
    public String status;

    @SerializedName("data")
    public Data data;

    @SerializedName("token")
    public String token;

    @SerializedName("ipAddress")
    public String ip;


}
