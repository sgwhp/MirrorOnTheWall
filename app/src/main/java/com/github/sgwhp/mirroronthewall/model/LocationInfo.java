package com.github.sgwhp.mirroronthewall.model;

/**
 * Created by robust on 2015/9/21.
 */
public class LocationInfo {
    public String address;
    public LocationContent content;
    public String status;

    public static class LocationContent{
        public AddressDetail address_detail;
        public String address;
        public Point point;
    }

    public static class AddressDetail{
        public String province;
        public String city;
        public String district;
        public String street;
        public String street_number;
        public String city_code;
    }

    public static class Point{
        public String y;
        public String x;
    }
}
