package com.inception.software.currentlocation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressFromAPIInteractor implements LocationAddressFromAPIListener {

    private Call<String> addressFromAPICall(String latLong, boolean isSensor, String APIkey) {
        LocationApiInterface locationApiInterface= LocationRetrofitClient.getRetrofitInstance().create(LocationApiInterface.class);
        return locationApiInterface.getAddressDataFromAPICall(latLong,isSensor,APIkey);
    }


    @Override
    public void addressFromAPICall(final onFinishedListener onFinishedListener, String latLong, boolean isSensor, String APIkey) {

        Call<String> apiCall = addressFromAPICall(latLong,isSensor,APIkey);
        if(apiCall.isExecuted()){
            apiCall.isCanceled();
        }
        apiCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                onFinishedListener.onFinished(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
             onFinishedListener.onFailure(t);
            }
        });
    }
}
