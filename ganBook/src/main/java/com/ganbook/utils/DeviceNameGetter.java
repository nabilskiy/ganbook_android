package com.ganbook.utils;

import android.os.Build;

public class DeviceNameGetter {
	private DeviceNameGetter() {}

	public static String get() {
		final String manufacturer = Build.MANUFACTURER;
		final String model = Build.MODEL;
		if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) { 
			return model;
		}
		return manufacturer + " " + model;	
	}

	public static String getManufacturer() {
		return Build.MANUFACTURER;
	}

	public static String getDeviceModel() {
		return Build.MODEL;
	}

	
}
