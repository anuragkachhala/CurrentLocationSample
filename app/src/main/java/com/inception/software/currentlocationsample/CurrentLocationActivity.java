package com.inception.software.currentlocationsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inception.software.currentlocation.CurrentLocationListener;
import com.inception.software.currentlocation.CurrentLocationUtil;
import com.inception.software.currentlocation.LocationAddressData;
import com.inception.software.currentlocation.LocationAddressListener;
import com.inception.software.currentlocation.LocationAddressUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.inception.software.currentlocation.LocationConstant.REQUEST_CODE_CHECK_SETTINGS;

public class CurrentLocationActivity extends AppCompatActivity implements View.OnClickListener, CurrentLocationListener, LocationAddressListener {
    private static final String TAG = CurrentLocationActivity.class.getName();

    @BindView(R.id.btn_start_location)
    Button btnStartLocation;

    @BindView(R.id.btn_stop_location)
    Button btnStopLocation;

    @BindView(R.id.tv_location)
    TextView tvLocation;

    @BindView(R.id.tv_last_update)
    TextView tvLastLocation;

    private CurrentLocationUtil currentLocationUtil;
    private boolean isPermissionGranted;

    private Location currentLocation;

    private String mLastUpdateTime;
    private boolean mRequestingLocationUpdates = false;
    private LocationAddressUtil locationAddressUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        ButterKnife.bind(this);
        setListeners();
        currentLocationUtil = new CurrentLocationUtil(this, false, this);
        locationAddressUtil = new LocationAddressUtil(this, this);
    }

    private void setListeners() {
        btnStartLocation.setOnClickListener(this);
        btnStopLocation.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_location:
                startGettingLocation();
                break;
            case R.id.btn_stop_location:
                currentLocationUtil.stopLocationUpdate();
                break;
        }
    }


    private void startGettingLocation(){
        // check if location permission is done
        currentLocationUtil.startLocationUpdate();
    }


    private void updateLocationUI() {

        if (currentLocation != null) {
            tvLocation.setText("Lat: " + currentLocation.getLatitude() + ", " +
                    "Lng: " + currentLocation.getLongitude());
            Log.i(TAG,
                    "Lat: " + currentLocation.getLatitude() + ", " +
                            "Lng: " + currentLocation.getLongitude()
            );


            tvLastLocation.setText(mLastUpdateTime);
            // location last updated time
            Log.i(TAG, "Last updated on: " + mLastUpdateTime);
        }


    }

    @Override
    public void locationStarted() {

    }

    @Override
    public void currentLocation(Location location) {
        currentLocation = location;
        updateLocationUI();

        // if have google map API key put api key else pass null
        locationAddressUtil.getAddress(location.getLatitude(), location.getLongitude(), null);

    }

    @Override
    public void locationCancelled() {

    }





    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (isPermissionGranted) {
            // startLocationUpdate();
            currentLocationUtil.startLocationUpdate();
        }


    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            currentLocationUtil.stopLocationUpdate();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHECK_SETTINGS:
                currentLocationUtil.onActivityResult(resultCode, data);
                break;
        }

    }

    @Override
    public void locationAddress(LocationAddressData locationAddressData) {
        Toast.makeText(this, locationAddressData.getFullAddress(), Toast.LENGTH_LONG).show();
    }

}
