package com.ganbook.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.ganbook.app.MyApp;
import com.ganbook.interfaces.GanbookApiInterface;
import com.ganbook.models.OnPostS3Answer;
import com.ganbook.models.PictureAnswer;
import com.ganbook.s3transferutility.Constants;
import com.ganbook.s3transferutility.Util;
import com.ganbook.utils.Const;
import com.ganbook.utils.DBUtils.HelperFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.BitmapFactory.decodeStream;

/**
 * Created by dmytro_vodnik on 6/16/16.
 * working on ganbook1 project
 */
public class UploadService extends IntentService {

    private static final String TAG = UploadService.class.getName();
    public static String name = "com.ganbook.services.UploadService";
    private TransferUtility transferUtility;

    String bucket;
    private LocalBroadcastManager broadcastManager;
    String tempTmb = Environment.getExternalStorageDirectory().getPath() + "/" + "tmp.jpg";
    String tempOrig = Environment.getExternalStorageDirectory().getPath() + "/" + "tmp_orig.jpg";
    PictureAnswer pictureAnswer;

    File thumbFile;
    File origFile;

    String thumbFileName, origFileName;

    String ganId, classId;

    long uploadedBytesORIG = 0, uploadedBytesTMB = 0, totalBytesToUpload = 0;

    @Inject
    @Named("POST")
    GanbookApiInterface ganbookApiInterfacePOST;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadService(String name) {
        super(name);
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastManager = LocalBroadcastManager.getInstance(this);

        transferUtility = Util.getTransferUtility(this);

        ((MyApp) getApplication()).getGanbookApiComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        pictureAnswer = intent.getParcelableExtra(Const.PICTURE_PARCELABLE);
        ganId = intent.getExtras().getString(Const.GAN_ID);
        classId = intent.getExtras().getString(Const.CLASS_ID);

        Log.d(TAG, "onHandleIntent: upload picture === " + pictureAnswer);

        uploadFile();
    }

    private void uploadFile() {

        Log.d(TAG, "uploading " + pictureAnswer);

        bucket = "ImageStore/" + ganId + "/" + classId
                + "/" + pictureAnswer.getAlbumId() + "/";

        String filePath = pictureAnswer.getLocaFilePath();
        String pictureName = pictureAnswer.getPictureName();

        if (filePath != null) {
        Bitmap thumbnail = null;

        try {
            if (pictureAnswer.getVideoDuration() == null)
                thumbnail = convertSrcToBitmap(filePath);
            else {
                thumbnail = ThumbnailUtils.createVideoThumbnail(filePath,
                        MediaStore.Images.Thumbnails.MINI_KIND);

                if (thumbnail == null) {

                    Log.d(TAG, "uploadFile: lets try this one " + pictureAnswer.getResid());

                    thumbnail = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(),
                            Long.parseLong(pictureAnswer.getResid()),
                            MediaStore.Images.Thumbnails.MINI_KIND, null);

                }
                pictureName = pictureAnswer.getPictureName().replace(".mp4", ".jpeg");
            }

        } catch (Exception e) {

            postToUiRemovePicture();
            return;
        }

            thumbFileName = bucket + Const.TMB + pictureName;
            thumbFile = new File(tempTmb);

            try {
                FileOutputStream fos = null;
                fos = new FileOutputStream(thumbFile);
                Log.d(TAG, "uploadFile: thumb file = " + thumbnail + " fos = " + fos);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                thumbnail.recycle();
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            } catch (NullPointerException npe) {

                Log.e(TAG, "uploadFile: error while get thumb file = " + Log.getStackTraceString(npe));

                postToUiRemovePicture();
                return;
            }

            origFileName = bucket + pictureAnswer.getPictureName();

            try {

                if (pictureAnswer.getVideoDuration() == null) {

                    origFile = compressImage(filePath);
                } else {

                    origFile = new File(filePath);
                }
            } catch (Exception e) {

                Log.e(TAG, "uploadFile: error whie compress image = " + Log.getStackTraceString(e));

                postToUiRemovePicture();
                return;
            }

            totalBytesToUpload = thumbFile.length() + origFile.length();

            Log.d(TAG, "uploadFile: thumb = " + thumbFile.length() + " orig = " + origFile.length());
            Log.d(TAG, "uploadFile: total bytes to upload = " + totalBytesToUpload);

            uploadTmbPhotoToS3();
        } else {

            postToUIFailed();

            //picture not found , remove it from local DB
            postToUiRemovePicture();
        }
    }

    private void postToUiRemovePicture() {

        Intent intent = new Intent(Const.REMOVE_PICTURE);

        intent.putExtra(Const.PICTURE_PARCELABLE, pictureAnswer);

        broadcastManager.sendBroadcast(intent);

        stopSelf();
    }

    private void uploadTmbPhotoToS3() {

            //Sending tmb photo to S3 first

            Log.d(TAG, "uploadTmbPhotoToS3: bucket === " + bucket);

            TransferObserver tmbObserver = transferUtility.upload(Constants.BUCKET_NAME,
                    thumbFileName, thumbFile);

        Log.d(TAG, "uploadOriginalPhotoToS3: uploading thumb file path = " + thumbFile.getAbsolutePath());
        Log.d(TAG, "uploadOriginalPhotoToS3: uploading thumb file name = " + thumbFileName);

            tmbObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    Log.d(TAG, "onStateChanged TMB: id = " + id + " state = " + state);

                    switch (state) {

                        case COMPLETED:

                            Log.d(TAG, "onStateChanged: COMPLETED TMB");

                            thumbFile.delete();

                            postToUIProgress(uploadedBytesTMB);

                            //If completed we sending original photo to S3
                            uploadOriginalPhotoToS3();
                            break;

                        case IN_PROGRESS:

                            break;

                        case FAILED:

                            postToUIFailed();
                            Log.d(TAG, "onStateChanged: FAILED TMB");

                            break;
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    Log.d(TAG, "onProgressChanged: tmb cur " + bytesCurrent + " total = " + bytesTotal);

                    if (bytesCurrent == bytesTotal && uploadedBytesTMB == 0) {
                        uploadedBytesTMB += bytesTotal;

                        postToUIProgress(uploadedBytesTMB);
                    }
                }

                @Override
                public void onError(int id, Exception ex) {

                    Log.e(TAG, "onError: id = " + id + " ex = " + Log.getStackTraceString(ex));
//                    postToUIFailed();
                }
            });
    }

    private static String get_create_date(String path) {
        String createDate = String.valueOf(new Date().getTime());

        try {
            File file = new File(path);
            Date lastModDate = new Date(file.lastModified());
            long create_date = lastModDate.getTime() / 1000;

            createDate = String.valueOf(create_date);
        } catch (NullPointerException e) {

            Log.e(TAG, "get_create_date: error while date get = " + Log.getStackTraceString(e));
        }

        return createDate;
    }

    private void uploadOriginalPhotoToS3() {

        Log.d(TAG, "uploadOriginalPhotoToS3: uploading file path = " + origFile.getAbsolutePath());
        Log.d(TAG, "uploadOriginalPhotoToS3: uploading file name = " + origFileName);

        final TransferObserver originalPhotoObserver = transferUtility.upload(Constants.BUCKET_NAME,
                origFileName, origFile);

        originalPhotoObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                Log.d(TAG, "onStateChanged ORIGINAL: id = " + id + " state = " + state);

                switch (state) {

                    case COMPLETED:

                        Log.d(TAG, "onStateChanged: COMPLETED ORIGINAL");

                        Log.d(TAG, "onStateChanged: uploaded bytes = " + (uploadedBytesTMB + uploadedBytesORIG));

                        if (pictureAnswer.getLocaFilePath() == null) {
                            postToUiRemovePicture();
                            Util.getTransferUtility(UploadService.this).cancel(originalPhotoObserver.getId());
                        }

                        postToUIProgress(uploadedBytesORIG);

                        //If completed we send onposts3 to server
                        Call<OnPostS3Answer> postS3Res = ganbookApiInterfacePOST
                                .sendS3Results(pictureAnswer.getAlbumId(),
                                        ganId,
                                        pictureAnswer.getPictureName(),
                                        pictureAnswer.getVideoDuration(),
                                        true,
                                        get_create_date(pictureAnswer.getLocaFilePath()));

                        postS3Res.enqueue(new Callback<OnPostS3Answer>() {
                            @Override
                            public void onResponse(Call<OnPostS3Answer> call, Response<OnPostS3Answer> response) {

                                OnPostS3Answer successAnswer = response.body();

                                Log.d(TAG, "onResponse: " + successAnswer);

                                origFile.delete();

                                if (successAnswer != null && successAnswer.isSuccess()) {

                                    Log.d(TAG, "onResponse: successful post results to server");

                                    postToUISuccess(successAnswer);
                                } else {

                                    Log.e(TAG, "onResponse: error while send posts3");

                                    postToUIFailed();
                                }
                            }

                            @Override
                            public void onFailure(Call<OnPostS3Answer> call, Throwable t) {

                                Log.e(TAG, "onResponse: error while send posts3 " + Log.getStackTraceString(t));
                                origFile.delete();
                                postToUIFailed();
                            }
                        });
                        break;

                    case IN_PROGRESS:

                        break;

                    case FAILED:

                        postToUIFailed();
                        origFile.delete();
                        Log.d(TAG, "onStateChanged: FAILED ORIGINAL");

                        break;
                    case WAITING_FOR_NETWORK:
                        Intent intent = new Intent(Const.NETWORK_INTENT);

                        intent.putExtra(Const.INTERNET_AVAIL, false);

                        broadcastManager.sendBroadcast(intent);
                        break;

                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                Log.d(TAG, "onProgressChanged: orig cur " + bytesCurrent + " total = " + bytesTotal);

                if (bytesCurrent == bytesTotal && uploadedBytesORIG == 0)
                    uploadedBytesORIG += bytesTotal;
            }

            @Override
            public void onError(int id, Exception ex) {

                Log.d(TAG, "onStateChanged: FAILED ORIGINAL");

//                postToUIFailed();
            }
        });
    }

    private void postToUIProgress(long bytes) {

        Intent intent = new Intent(Const.PIC_PROGRESS_INTENT);

        Log.d(TAG, "postToUIProgress: bytes = " + bytes);
        Log.d(TAG, "postToUIProgress: total bytes = " + totalBytesToUpload);

        int progress  = (int) (((double)(uploadedBytesTMB + uploadedBytesORIG)/totalBytesToUpload)*100);

        Log.d(TAG, "postToUIProgress: pr = " + progress);

        pictureAnswer.setProgress(progress);

        intent.putExtra(Const.PICTURE_PARCELABLE, pictureAnswer);

        broadcastManager.sendBroadcast(intent);
    }

    private void postToUISuccess(OnPostS3Answer successAnswer) {

        pictureAnswer.setStatus(1);
        pictureAnswer.setPictureId(successAnswer.getPictureId());
        pictureAnswer.setLocaFilePath(null);

        Intent intent = new Intent(Const.UPLOADING_INTENT);

        intent.putExtra(Const.SUCCESS_STATUS, successAnswer);
        intent.putExtra(Const.PICTURE_PARCELABLE, pictureAnswer);

        markPictureUploaded(pictureAnswer);

        broadcastManager.sendBroadcast(intent);

        stopSelf();
    }

    private void postToUIFailed() {

        if (origFile != null)
        origFile.delete();
        if (thumbFile != null)
        thumbFile.delete();

        pictureAnswer.setStatus(2);

        Intent intent = new Intent(Const.UPLOADING_INTENT);

        intent.putExtra(Const.SUCCESS_STATUS, new OnPostS3Answer(false));
        intent.putExtra(Const.PICTURE_PARCELABLE, pictureAnswer);

        broadcastManager.sendBroadcast(intent);

        markPictureNotUploaded(pictureAnswer);

        stopSelf();
    }

    private void markPictureNotUploaded(PictureAnswer p) {

        try {
            p.setStatus(2);
            HelperFactory.getHelper()
                    .getPictureAnswerDAO()
                    .update(p);

            Log.d(TAG, "onDestroy: updated " + p);
        } catch (SQLException e) {
            Log.e(TAG, "error while mark picture = " + Log.getStackTraceString(e));
        }
    }

    private void markPictureUploaded(PictureAnswer pictureAnswer) {

        //if picture successful uploaded - delete from DB
        deletePictureFromDB(pictureAnswer);
    }

    private void deletePictureFromDB(PictureAnswer pictureAnswer) {

        try {
            Log.d(TAG, "deleting picture " + pictureAnswer);

            HelperFactory.getHelper().getPictureAnswerDAO().delete(pictureAnswer);
        } catch (SQLException e) {
            Log.e(TAG, "error while delete picture " + Log.getStackTraceString(e));
        }
    }

    private Bitmap convertSrcToBitmap(String imageSrc) {

        File imgFile = new File(imageSrc);
        Bitmap imageBitmap = null;
        if (imgFile.exists()) {
            try {

                //The new size we want to scale to
                final int THUMBNAIL_SIZE = 144;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                decodeStream(new FileInputStream(imgFile), null, options);
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                // recreate the stream
                // make some calculation to define inSampleSize
                options.inSampleSize = calculateInSampleSize(options, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
                imageBitmap = decodeStream(new FileInputStream(imgFile), null, options);
                int imageRotation = getImageRotation(imgFile);
                if(imageRotation != 0) {
                    imageBitmap = getBitmapRotatedByDegree(imageBitmap, imageRotation);
                }
                return imageBitmap;
            } catch (Exception ex) {


            }

        }

        return imageBitmap;
    }

    private static int getImageRotation(final File imageFile) {

        ExifInterface exif = null;
        int exifRotation = 0;

        try {
            exif = new ExifInterface(imageFile.getPath());
            exifRotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif == null)
            return 0;
        else
            return exifToDegrees(exifRotation);
    }

    private static int exifToDegrees(int rotation) {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;

        return 0;
    }

    private static Bitmap getBitmapRotatedByDegree(Bitmap bitmap, int rotationDegree) {
        Matrix matrix = new Matrix();
        matrix.preRotate(rotationDegree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public File compressImage(String imagePath) {

        String filePath = imagePath;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = 1754f;
        float maxWidth = 1240f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(tempOrig);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new File(tempOrig);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
