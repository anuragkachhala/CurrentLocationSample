package com.inception.software.currentlocation;

import android.location.Location;

public interface CurrentLocationListener {

    void locationStarted();

    void currentLocation(Location location);

    void locationCancelled();
}
