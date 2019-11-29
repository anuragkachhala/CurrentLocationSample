package com.inception.software.currentlocation;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.inception.software.currentlocation.LocationConstant.END_POINT_GEO_CODE;

public interface LocationApiInterface {


        @GET(END_POINT_GEO_CODE)
        Call<String> getAddressDataFromAPICall(@Query("latlng") String latLong, @Query("sensor") boolean sensor, @Query("key") String key);

}
