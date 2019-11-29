package com.inception.software.currentlocation;

interface LocationAddressFromAPIListener {

    interface onFinishedListener{
        void onFinished(String addressJson);


        void onFailure(Throwable t);
    }

    void addressFromAPICall(onFinishedListener onFinishedListener, String latLong, boolean isSensor, String APIkey);
}
