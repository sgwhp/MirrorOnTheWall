package com.github.sgwhp.mirroronthewall.model;

/**
 * Created by robust on 2015/9/24.
 */
public class VoiceResult {
    public Content content;
    public Result result;

    public static class Content{
        public String json_res;
        public String[] item;
    }

    public static class JsonRes{
        public String parsed_text;
        public String raw_text;
        public NLUResult[] results;
    }

    public static class NLUResult{
        public int demand;
        public String domain;
        public String intent;
        public float score;
        public int update;
        public NLUObject object;
    }

    public static class NLUObject{
        public int requestCode;
        public String date;
        public String event;
        public String time;
        public String type;
        public Range range;
        public long interval;
        public String repeat;
    }

    public static class Range{
        public DateTime begin;
        public DateTime end;
    }

    public static class DateTime{
        public String date;
        public String time;
    }

    public static class Result{
        public String sn;
        public int idx;
        public int res_type;
        public int err_no;
        public long corpus_no;
    }
}
