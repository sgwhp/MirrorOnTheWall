package com.github.sgwhp.mirroronthewall.sync;

import com.github.sgwhp.mirroronthewall.model.LocationInfo;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by robust on 2015/9/22.
 */
public interface LocationService {
    @GET("/location/ip")
    public Call<LocationInfo> getLocationInfo(@Query("ak") String ak);
}
