package com.coolweather.android.bean;

import java.util.List;

public class HistoryDescBean {

    /**
     * result : [{"_id":"16301115","title":"德国天文学家开普勒逝世","pic":"http://juheimg.oss-cn-hangzhou.aliyuncs.com/toh/201111/15/1E12445126.jpg","year":1630,"month":11,"day":15,"des":"在389年前的今天，1630年11月15日 (农历十月十二)，德国天文学家开普勒逝世。","content":"在389年前的今天，1630年11月15日 (农历十月十二)，德国天文学家开普勒逝世。\n开普勒(1571～1630.11.15)，德国天文学家。1587年进入蒂宾根大学，开始研究哥白尼的天文学。其主要成就是奠定了天体力学的基础。他分析第谷·布拉赫多年积累的观测资料，提出了行星运动三定律(开普勒定律)：第一定律为椭圆轨道定律\u2014\u2014行星轨道呈椭圆形，太阳在一个焦点上；第二定律为相等面积定律\u2014\u2014在相等的时间内，行星和太阳联线所扫过的面积相等；第三定律为调和定律\u2014\u2014任何两行星公转周期的平方同轨道半长径的立方成正比。这为牛顿发现万有引力定律打下了基础。\n","lunar":"庚午年十月十二"}]
     * reason : 请求成功！
     * error_code : 0
     */

    private String reason;
    private int error_code;
    private List<ResultBean> result;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * _id : 16301115
         * title : 德国天文学家开普勒逝世
         * pic : http://juheimg.oss-cn-hangzhou.aliyuncs.com/toh/201111/15/1E12445126.jpg
         * year : 1630
         * month : 11
         * day : 15
         * des : 在389年前的今天，1630年11月15日 (农历十月十二)，德国天文学家开普勒逝世。
         * content : 在389年前的今天，1630年11月15日 (农历十月十二)，德国天文学家开普勒逝世。
         开普勒(1571～1630.11.15)，德国天文学家。1587年进入蒂宾根大学，开始研究哥白尼的天文学。其主要成就是奠定了天体力学的基础。他分析第谷·布拉赫多年积累的观测资料，提出了行星运动三定律(开普勒定律)：第一定律为椭圆轨道定律——行星轨道呈椭圆形，太阳在一个焦点上；第二定律为相等面积定律——在相等的时间内，行星和太阳联线所扫过的面积相等；第三定律为调和定律——任何两行星公转周期的平方同轨道半长径的立方成正比。这为牛顿发现万有引力定律打下了基础。

         * lunar : 庚午年十月十二
         */

        private String _id;
        private String title;
        private String pic;
        private int year;
        private int month;
        private int day;
        private String des;
        private String content;
        private String lunar;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLunar() {
            return lunar;
        }

        public void setLunar(String lunar) {
            this.lunar = lunar;
        }
    }
}
