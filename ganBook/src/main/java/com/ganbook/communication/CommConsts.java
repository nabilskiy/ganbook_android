package com.ganbook.communication;

import com.ganbook.communication.json.transmitter.JsonTransmitter;

public class CommConsts {

	private CommConsts() {}
	
	public static final String HOST = JsonTransmitter.API_HOST;
	public static final String REQUEST_HOST = JsonTransmitter.API_URL;

	public static final String PICTURE_HOST = JsonTransmitter.PICTURE_HOST; //"http://s3.ganbook.co.il/ImageStore/";


	/**
	 * Tag used on log messages.
	 */
	public  static final String TAG = "Ganim";


	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	public  static final String EMPTY_RESULT = "EMPTY_RESULT";
	public  static final String ERROR = "ERROR";
	public  static final String LOST_CONNECTION = "LOST_CONNECTION";
	public static final String GANIM = "/GANIM/";

}


