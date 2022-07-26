package com.ganbook.services;

import android.location.Location;

public interface ILocationServiceDelegate {
    void onLocationChanged(Location location);
}
