package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    public String codeId;

    @SerializedName("content")
    public String singlePoem;

    @SerializedName("popularity")
    public int population;

    @SerializedName("origin")
    public Poem poem;

    @SerializedName("recommendedReason")
    public String reason;

    @SerializedName("cacheAt")
    public String cacheAt;

    public class Poem{
        @SerializedName("title")
        public String title;

        @SerializedName("dynasty")
        public String dynasty;

        @SerializedName("author")
        public String author;

        public List<Content> contentList;

        public List<Translate> translateList;

        public List<MatchTags> matchTagsList;

    }
    public class Content{ }

    public class Translate{ }

    public class MatchTags{}

}
