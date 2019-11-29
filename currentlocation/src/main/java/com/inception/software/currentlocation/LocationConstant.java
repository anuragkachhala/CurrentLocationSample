package com.inception.software.currentlocation;

import android.location.LocationManager;

public class LocationConstant {

    private LocationConstant() {
    }

    /**
     * The factor for conversion from kilometers to meters
     */
     static final float KILOMETER_TO_METER = 1000.0f;


    /**
     * The factor for conversion from meter to KILOMETER
     */
    static final float METER_TO_KILOMETER = 1/KILOMETER_TO_METER;


    /**
     * Constant used in the location settings dialog.
     */
     public static final int REQUEST_CODE_CHECK_SETTINGS = 100;

    /**
     * The internal name of the provider for the coarse location
     */
     static final String PROVIDER_COARSE_LOCATION = LocationManager.NETWORK_PROVIDER;
    /**
     * The internal name of the provider for the fine location
     */
     static final String PROVIDER_FINE_LOCATION = LocationManager.GPS_PROVIDER;
    /**
     * The internal name of the provider for the fine location in passive mode
     */
     static final String PROVIDER_FINE_PASSIVE_LOCATION = LocationManager.PASSIVE_PROVIDER;
    /**
     * The default interval to receive new location updates after (in milliseconds)
     */

    // location updates interval - 10sec
     static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
     static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;


     static final String BASE_URL = "https://maps.googleapis.com/maps/";

     static final String END_POINT_GEO_CODE="api/geocode/json";
}
