package com.ganbook.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.ganbook.interfaces.VideoProcessListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

/**
 * Created by Noa on 21/01/2016.
 */
public class FfmpegUtils {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG = FfmpegUtils.class.getName();


    private FfmpegUtils() {};

//    public static File getCompressedVideo(String orig_file_path, String startMs, String endMs) {
//
//        LoadJNI vk = new LoadJNI();
//        String newFile = null;
//
//        try {
//            newFile = getOutputMediaFilePath(MEDIA_TYPE_VIDEO);
//            String trim = "";
//
//            String[] command = {"ffmpeg","-y","-i",orig_file_path,
//                    "-strict","experimental","-vf","scale=640:-1","-r","30","-ab","48000","-ac","2",
//                    "-ar","22050","-vcodec","libx264","-preset","ultrafast","-threads","4","-b","2097152",
//                    newFile};
//
//            if(startMs != "-1" && endMs != "-1")
//            {
//                command = new String[]{"ffmpeg", "-y", "-i", orig_file_path,
//                        "-strict", "experimental", "-vf", "scale=640:-1", "-r", "30", "-ss", startMs, "-to", endMs, "-ab", "48000", "-ac", "2",
//                        "-ar", "22050", "-vcodec", "libx264", "-preset", "ultrafast", "-threads", "4", "-b", "2097152",
//                        newFile};
//            }
//
////            String com = "ffmpeg -y -i " + orig_file_path + " -c:v libx264 -preset ultrafast " + newFile;
//
//            String com = "ffmpeg -y -i " + orig_file_path + " -strict experimental -vf scale=640:-1 -r 30 " + trim + //"-ss 00:00:00 -to 00:00:05 " +
//                    "-ab 48000 -ac 2 -ar 22050 -vcodec libx264 -preset ultrafast " +
//                    "-threads 4 -b 2097152 " + newFile;
//
////				String commandStr = "ffmpeg -y -i " + current_photoUri.getPath() + " -s 640x480 -aspect 4:3 -c:a copy " + current_photoUri.getPath();
////						String commandStr = "ffmpeg -y -i " + new_url + "in.mp4" + " -strict experimental -s 320x240 -r 30 -aspect 3:4 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 " + new_url + "out.mp4";
//
//
//
//            String workFolder = context.getFilesDir().getAbsolutePath() + "/";
//
//            vk.run(command, workFolder, context);
//
//            Log.i("test", "ffmpeg4android finished successfully");
//        } catch (Throwable e) {
//            Log.e("test", "vk run exception.", e);
//        }
//
//        return new File(newFile);
//    }

    public static File getCompressedVideo(Context applicationContext,
                                          final VideoProcessListener vListener,
                                          String orig_file_path, String startMs, String endMs) {

        try {
            String newFile = getOutputMediaFilePath(MEDIA_TYPE_VIDEO);

            //String[] command = {"-i", orig_file_path, "-s", "480x320", "-codec", "copy", newFile};

//            String[] command = {"-i", orig_file_path, "-c:v", "libx265", "-preset", "veryfast", "-tag:v", "hvc1", "-vf", "format=yuv420p", "-c:a", "copy", newFile};
//
//            if(!startMs.equals("-1") && !endMs.equals("-1")) {
//                command = new String[] {"-i", orig_file_path, "-ss", startMs, "-to", endMs,  "-s", "320x240", "-codec", "copy", newFile};
//            }

            String[] command = {"-y","-i",orig_file_path,
                    "-strict","experimental","-vf","scale=640:-1","-r","30","-ab","48000","-ac","2",
                    "-ar","22050","-vcodec","libx264","-preset","ultrafast","-threads","4","-b","2097152",
                    newFile};
            if(!startMs.equals("-1") && !endMs.equals("-1")) {
                command = new String[] {"-y", "-i", orig_file_path, "-ss", startMs, "-to", endMs,  "-strict","experimental","-vf","scale=640:-1","-r","30","-ab","48000","-ac","2",
                        "-ar","22050","-vcodec","libx264","-preset","ultrafast","-threads","4","-b","2097152",
                        newFile};
            }
            int rc = FFmpeg.execute(command);
            if (rc == RETURN_CODE_SUCCESS) {
                Log.i(Config.TAG, "Command execution completed successfully.");
                vListener.onSuccess(new File(newFile));
            } else if (rc == RETURN_CODE_CANCEL) {
                Log.i(Config.TAG, "Command execution cancelled by user.");
            } else {
                Log.i(Config.TAG, String.format("Command execution failed with rc=%d and output=%s.", rc, newFile));
            }

        } catch (Exception e) {
            Log.e(TAG, "getCompressedVideo: error while execute ffmpeg = " + Log.getStackTraceString(e));
        }


        return new File(orig_file_path);
    }

    public static String getOutputMediaFilePath(int type){
        String pictDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File pictDir = new File(pictDirPath);
        if (!pictDir.exists()){
            if (!pictDir.mkdirs()){
                int jj=234;
                jj++;
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = pictDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg";
        }
        else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = pictDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4";
        } else {
            return null;
        }
        return mediaFile;
    }

    public static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(new File(getOutputMediaFilePath(type)));
    }

}
