/*
 * Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.ganbook.s3transferutility;

import android.content.Context;
import android.net.Uri;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.util.Base64;
import com.amazonaws.util.Md5Utils;
import com.ganbook.app.MyApp;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.user.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.UUID;

//import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

/*
 * Handles basic helper functions used throughout the app.
 */
public class Util {

    // We only need one instance of the clients and credentials provider
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    MyApp.context,
                    Constants.COGNITO_POOL_ID,
                    Regions.EU_WEST_1);
        }
        return sCredProvider;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
//            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));

            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setConnectionTimeout(30000);
            clientConfiguration.setSocketTimeout(30000);
            clientConfiguration.setMaxErrorRetry(3);

            String[] amazonToken = parseAmazonToken();
            if(amazonToken == null || amazonToken.length < 3) {
                return null;
            }
            //sS3Client = new AmazonS3Client(new BasicAWSCredentials("AKIAJ24SOQDQQEOJ2ZDQ",
            //        "utO4WnL4q1xSIgUYUISjfaRItShzo9xLoB0QaaCS"),clientConfiguration);

            sS3Client = new AmazonS3Client(new BasicSessionCredentials(amazonToken[0], amazonToken[1], amazonToken[2]),clientConfiguration);

            sS3Client.setRegion(Region.getRegion(Regions.EU_WEST_1));
        }

        return sS3Client;
    }

    /**
     * get presigned URL using federation credentials
     * for bucket and file
     */
    public static String getPresignedUrl(String fullUrl, Context context) {

        String key = fullUrl.replace(JsonTransmitter.PICTURE_HOST, "ImageStore/");
        Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.HOUR_OF_DAY, 24);
        return getS3Client(context).generatePresignedUrl(Constants.BUCKET_NAME, key, currentDate.getTime(), HttpMethod.GET).toString();

    }

    /**
     * parse federation token
     */
    public static String[] parseAmazonToken() {
        if(User.getAtoken() != null) {
            try {
                String decodedString = new String(Base64.decode(User.getAtoken()), "UTF-8");
                return decodedString.split(";");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     * 
     * @param context
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    MyApp.context);
        }

        return sTransferUtility;
    }
}
