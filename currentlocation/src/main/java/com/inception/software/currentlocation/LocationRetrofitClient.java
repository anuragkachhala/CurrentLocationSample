package com.inception.software.currentlocation;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.inception.software.currentlocation.LocationConstant.BASE_URL;

public class LocationRetrofitClient {
    private static Retrofit retrofit = null;


    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
