package com.inception.software.currentlocation;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.Date;

import static com.inception.software.currentlocation.LocationConstant.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.inception.software.currentlocation.LocationConstant.METER_TO_KILOMETER;
import static com.inception.software.currentlocation.LocationConstant.REQUEST_CODE_CHECK_SETTINGS;
import static com.inception.software.currentlocation.LocationConstant.UPDATE_INTERVAL_IN_MILLISECONDS;

/*
 * this  util class for access current location of devices.
 */
public class CurrentLocationUtil {
    public static final String TAG = CurrentLocationUtil.class.getName();

    //Bunch of location related API
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient settingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest locationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest locationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback locationCallback;

    /**
     * Represents a geographical location.
     */
    private Location currentLocation;


    private String lastUpdateTime;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     * Whether to require a new location (`true`) or accept old (last known) locations as well (`false`)
     */
    private Boolean requestingLocationUpdates;


    private Context activityContext;
    private Context context;

    /*
     * this listener to callback to activity or fragment  for location
     */
    private CurrentLocationListener currentLocationListener;


    public CurrentLocationUtil(Context context, final Boolean isRequiredLastLocation, final CurrentLocationListener currentLocationListener) {
        this(context, null, isRequiredLastLocation, currentLocationListener);
    }

    public CurrentLocationUtil(Context context, final LocationRequest locationRequest, final Boolean isRequiredLastLocation, final CurrentLocationListener currentLocationListener) {
        this.context = context;
        this.locationRequest = locationRequest;
        this.currentLocationListener = currentLocationListener;
        initializationLocationAPI();
    }


    private void initializationLocationAPI() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        settingsClient = LocationServices.getSettingsClient(context);

        setLocationCallback();
        setLocationRequest();
        buildLocationSettingsRequest();


    }


    /**
     * Creates a callback for receiving location events.
     */

    private void setLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "Location result is available");
                if(locationResult==null){
                    currentLocationListener.locationCancelled();
                }else {
                    currentLocation = locationResult.getLastLocation();
                    lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    sendLastLocationToActivity();
                }


            }


            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (locationAvailability.isLocationAvailable()) {
                    Log.i(TAG, "Location is available");
                } else {
                    Log.i(TAG, "Location is not available");
                }
            }


        };
    }


    private void sendLastLocationToActivity(){
        currentLocationListener.currentLocation(currentLocation);

        if(currentLocation!=null)
        Log.i(TAG,
                "Lat: " + currentLocation.getLatitude() + ", " +
                        "Lng: " + currentLocation.getLongitude()
        );



        // location last updated time
        Log.i(TAG,"Last updated on: " + lastUpdateTime);

    }


    private void setLocationRequest() {
        locationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }




    public void startLocationUpdate(){
        checkLocationSetting();


    }

    public void stopLocationUpdate(){
        stopUpdateLocation();
    }

    private void checkLocationSetting(){
        requestingLocationUpdates = true;
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        beginUpdateLocation();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode){
                            case LocationSettingsStatusCodes
                                    .RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult((Activity) context, REQUEST_CODE_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                        }

                    }


                });
    }

    /**
     * Starts updating the location and requesting new updates after the defined interval
     */
    @SuppressLint("MissingPermission")
    private void beginUpdateLocation(){
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());


    }


    /**
     * Stops the location updates when they aren't needed anymore so that battery can be saved
     */
    @SuppressLint("MissingPermission")
    private void stopUpdateLocation(){
        if (locationCallback != null){
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    public void onActivityResult(int resultCode, Intent data){
        switch (resultCode) {
            case Activity.RESULT_OK:
                Log.e(TAG, "User agreed to make required location settings changes.");
                // Nothing to do. startLocationupdates() gets called in activity onResume again.
                break;
            case Activity.RESULT_CANCELED:
                Log.e(TAG, "User chose not to make required location settings changes.");
                requestingLocationUpdates = false;
                break;
        }
    }


    /**
     * Calculates the difference from the start position to the end position (in meters)
     *
     * @param startLatitude  the latitude of the start position
     * @param startLongitude the longitude of the start position
     * @param endLatitude    the latitude of the end position
     * @param endLongitude   the longitude of the end position
     * @return the distance in meters
     */
    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return convertMeterToKilometer(results[0]);
    }


    public static double convertMeterToKilometer(double distanceInMeter){
        return distanceInMeter*METER_TO_KILOMETER;
    }


}
