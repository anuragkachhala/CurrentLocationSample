package com.inception.software.currentlocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddressUtil {
    private Context context;
    private LocationAddressListener locationAddressListener;
    private AddressFromAPIInteractor addressFromAPIInteractor;


    public LocationAddressUtil(Context context, LocationAddressListener locationAddressListener) {
        this.context = context;
        this.locationAddressListener = locationAddressListener;
        addressFromAPIInteractor = new AddressFromAPIInteractor();
    }

    public void getAddress(Double latitude, Double longitude, String APIkey) {

        if (APIkey == null) {

            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    LocationAddressData locationAddressData = new LocationAddressData();
                    locationAddressData.setCity(city);
                    locationAddressData.setPinCode(postalCode);
                    locationAddressData.setCountry(country);
                    locationAddressData.setState(state);
                    locationAddressData.setFullAddress(address);

                    locationAddressListener.locationAddress(locationAddressData);

                }
            } catch (IOException e) {
                e.printStackTrace();

            }

        } else {
            getAddressFromAPIKey(latitude, longitude, APIkey);
        }
    }

    private void getAddressFromAPIKey(Double latitude, Double longitude, String APIkey) {
        StringBuilder latlong = new StringBuilder();
        latlong.append(latitude);
        latlong.append(",");
        latlong.append(longitude);

        addressFromAPIInteractor.addressFromAPICall(new LocationAddressFromAPIListener.onFinishedListener() {
            @Override
            public void onFinished(String addressJson) {
                convertJSONStringToData(addressJson);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, latlong.toString(), true, APIkey);
    }

    private void convertJSONStringToData(String jsonResponse) {

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray Results = jsonObject.getJSONArray("results");
            JSONObject zero = Results.getJSONObject(0);
            JSONArray address_components = zero.getJSONArray("address_components");
            LocationAddressData locationAddressData = new LocationAddressData();
            locationAddressData.setFullAddress(zero.getString("formatted_address"));
            for (int i = 0; i < address_components.length(); i++) {
                JSONObject zero2 = address_components.getJSONObject(i);
                String long_name = zero2.getString("long_name");
                JSONArray mtypes = zero2.getJSONArray("types");
                String Type = mtypes.getString(0);
                if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                    if (Type.equalsIgnoreCase("street_number")) {
                        //Address1 = long_name + " ";
                    } else if (Type.equalsIgnoreCase("route")) {
                        //Address1 = Address1 + long_name;
                    } else if (Type.equalsIgnoreCase("sublocality")) {
                        // Address2 = long_name;
                    } else if (Type.equalsIgnoreCase("locality")) {
                        // Address2 = Address2 + long_name + ", ";
                        locationAddressData.setCity(long_name);
                    } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                        // County = long_name;

                    } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                        // State = long_name;
                    } else if (Type.equalsIgnoreCase("country")) {
                        locationAddressData.setCountry(long_name);
                    } else if (Type.equalsIgnoreCase("postal_code")) {
                        locationAddressData.setPinCode(long_name);
                    }
                }
            }
            locationAddressListener.locationAddress(locationAddressData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
