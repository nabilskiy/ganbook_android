package com.ganbook.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.loader.content.CursorLoader;
import android.util.Log;

import com.ganbook.app.MyApp;
import com.ganbook.communication.json.transmitter.JsonTransmitter;
import com.ganbook.ui.CircleImageView;
import com.ganbook.universalloader.UILManager;
import com.ganbook.user.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.utils.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtils  {

	private static final int MAX_PIX_SIZE = 40000; // ggggFix before 90000; // in pix

	private static final int MAX_FILE_SIZE_BYTES = 1000000; // gggFix before 600000; // before: 1000000;	
	private static final int INIT_MAX_DIM_SIZE_PIX = 1000; // ggggFix before 444;

	private static final int DIM_SIZE_STEP = 50;
  

	private ImageUtils() {}

	public static String getFullSizeImage(String gan_id, String class_id, String album_id, String pictureName) {
		//		return getThumbnail(gan_id, class_id, album_id, pictureName) + ".jpg";  
		String url = JsonTransmitter.PICTURE_HOST + 
				gan_id + "/" + 
				class_id + "/" + 
				album_id + "/" + 
				pictureName;  
		return url;		
	}


	public static String getThumbnail(String gan_id, String class_id, String album_id, String pictureName) {
		String url = JsonTransmitter.PICTURE_HOST + 
				gan_id + "/" + 
				class_id + "/" + 
				album_id + "/" +
				JsonTransmitter.TMB +
				pictureName;  
		
		url = url.replace("mp4", "jpeg");
		return url;		
	}

	
	public static File resizeToFile(final String orig_ImgPath, String dest_FilePath) {  
		final File orig_ImgFile = new File(orig_ImgPath);
		if (!orig_ImgFile.exists()) {
			int jj=234;
			jj++; 
			return null;
		}
		if (!orig_ImgFile.isFile()) {
			int jj=234;
			jj++;
			return null; 
		}


		// resize image file
		int dimSize = INIT_MAX_DIM_SIZE_PIX;
		File reduced_ImgFile = null;
		int sampleSize = 2;
		while (true){ ///ggggFix test
			reduced_ImgFile = compressImage(orig_ImgPath, dest_FilePath);//create_reducedFile(orig_ImgPath, dest_FilePath, sampleSize);
			long reduced_len = (reduced_ImgFile==null ? 0 : reduced_ImgFile.length());
			
			long max_size =  MAX_FILE_SIZE_BYTES;
			
			if(User.current.max_image_size_android != null)
			{
				max_size = Long.valueOf(User.current.max_image_size_android);
			}
			
			if (reduced_len != 0 && reduced_len < max_size) {
				int jj=234;
				jj++;
				break;
			}
			sampleSize *= 2;
		} 
		return reduced_ImgFile;
	}



	private static int __resizeDim(long file_len, int dimSize) {
		int jj=234;
		jj++;
		return (int)(0.8*dimSize); 
		//		if (file_len > 3*MAX_FILE_SIZE_BYTES) {
		//			return dimSize = Math.max(100, dimSize/3);
		//		}
		//		else if (file_len > 2*MAX_FILE_SIZE_BYTES) {
		//			return dimSize = Math.max(100, dimSize/2);
		//		}
		//		else  {
		//			return dimSize = Math.max(100, dimSize - 80);
		//		}
	}


	private static File create_reducedFile(String orig_ImgPath, String dest_FilePath, int sampleSize) {
		final File reduced_ImgFile = new File(dest_FilePath);

		try { reduced_ImgFile.delete(); } catch (Exception e) {}
		try { reduced_ImgFile.createNewFile(); } catch (Exception e) {}		

		FileInputStream src_fis = null; 
		FileOutputStream dest_fos = null;
		try {
//			src_fis = new FileInputStream(orig_ImgPath); 
			dest_fos = new FileOutputStream(reduced_ImgFile);
			final BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(src_fis, null, options);
//			int imageHeight = options.outHeight;
//			int imageWidth = options.outWidth;
//			int orig_size =  imageWidth*imageHeight;
//			int sampleSize;

//			sampleSize = calculateInSampleSize(options, dimSize, dimSize);

			src_fis = new FileInputStream(orig_ImgPath); // reset!!

			options.inSampleSize = sampleSize; 
			options.inPurgeable = true;
			options.inInputShareable = true; 
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			Bitmap reduced_bmp = BitmapFactory.decodeStream(src_fis, null, options);
			if (reduced_bmp==null) {  
				int jj=234;
				jj++;
			}
			reduced_bmp.compress(Bitmap.CompressFormat.PNG, 100, dest_fos); // bmp is your Bitmap instance
			int jj=234;
			jj++;
			return reduced_ImgFile;

		} catch (Exception e) {
			e.printStackTrace(); 

			int jj=234;
			jj++; 

			return null; 
		}
		finally {			
			if (src_fis != null) {				
				try { src_fis.close(); } catch (Exception e) {}
			}
			if (dest_fos != null) {
				try { dest_fos.close(); } catch (Exception e) {}
			}
		}

	}
	
	private static String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = MyApp.context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
	
	public static File compressImage(String filePath, String dest_FilePath) {
		
//		Uri myUri = Uri.parse(imageUri);
		
//        String filePath = getDataColumn(MyApp.context,myUri);
        Bitmap scaledBitmap = null;
 
        BitmapFactory.Options options = new BitmapFactory.Options();
 
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
 
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
 
//      max Height and width values of the compressed image is taken as 816x612
 
        float maxHeight = 1704.0f;
        float maxWidth = 1278.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
 
//      width and height values are set maintaining the aspect ratio of the image
 
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
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
//        String filename = getFilename();
        try {
            out = new FileOutputStream(dest_FilePath);
 
//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
 
        final File reduced_ImgFile = new File(dest_FilePath);
         return reduced_ImgFile;
 
    }
	
	public String getFilename() {
	    File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
	    if (!file.exists()) {
	        file.mkdirs();
	    }
	    String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
	    return uriSting;
	 
	}
	

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}


	public static File create_thumb(final String orig_ImgPath, 
			String dest_FilePath,  int destSize, boolean quitIfExists) { 
		final File orig_ImgFile = new File(orig_ImgPath);
		if (!orig_ImgFile.exists()) {
			int jj=234;
			jj++; 
			return null;
		}
		if (!orig_ImgFile.isFile()) {
			int jj=234;
			jj++;
			return null;
		}

		File reduced_ImgFile = new File(dest_FilePath);
		if (quitIfExists && 
				(reduced_ImgFile.exists() && reduced_ImgFile.isFile() && reduced_ImgFile.length() > 20)) {
			int jj=234;
			jj++;
			return reduced_ImgFile;		
		}

		// actually create the thumb:

		reduced_ImgFile = resize_bmp_to_file(reduced_ImgFile, orig_ImgPath, destSize);

		return reduced_ImgFile;
	}


	private static File resize_bmp_to_file(File reduced_ImgFile, String orig_ImgPath, int destSize) {
		FileOutputStream dstTMB = null;
		try { 
			dstTMB = new FileOutputStream(reduced_ImgFile); 
			Bitmap orig_bitmap = __decodeFromFile(orig_ImgPath, MAX_PIX_SIZE);
			Bitmap ThumbImage = null;
			if(orig_ImgPath.contains("mp4"))
			{
				ThumbImage = ThumbnailUtils.createVideoThumbnail(orig_ImgPath, MediaStore.Video.Thumbnails.MINI_KIND);
			}
			else
			{
				ThumbImage = ThumbnailUtils.extractThumbnail(orig_bitmap, destSize, destSize);
			}

			ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, dstTMB);
			return reduced_ImgFile;
		} 
		catch (Exception e) { 
			int jj=234;
			jj++;
			return null;
		}
		finally {
			if (dstTMB != null) {
				try { dstTMB.close();}  catch (Exception e) {}
			}
		}
	}




	//
	//		try  {
	//			//            FileInputStream fis = new FileInputStream(lagreImg);
	//			Options bitmapOptions = new Options();
	//			bitmapOptions.inJustDecodeBounds = true; 
	//			Bitmap imageBitmap = BitmapFactory.decodeFile(orig_ImgPath, bitmapOptions);
	//
	//			int sampleSize = getDesiredSampleSize(bitmapOptions, sizeInPix);
	//
	//			bitmapOptions.inSampleSize = sampleSize; 
	//			bitmapOptions.inJustDecodeBounds = false; 
	//			Bitmap reduced_bmp = BitmapFactory.decodeFile(orig_ImgPath, bitmapOptions);
	//
	//
	//			// and save thumb:
	//			saveBitmapToFile(reduced_bmp, reduced_ImgFile);
	//
	//			reduced_bmp.recycle();
	//			
	//			long orig_len = orig_ImgFile.length(); 
	//			long reduced_len = reduced_ImgFile.length();
	//			
	//			return reduced_ImgFile;
	//		}
	//		catch(Exception ex) {
	//			int jj=234;
	//			jj++;
	//		}
	//		return null;

	public static String getRealPathFromURI(Uri contentUri, Context context) {

		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String result = cursor.getString(column_index);
		cursor.close();
		return result;
	}

	public static Bitmap decodeUri(Context context, Uri selectedImage, int reqSize) throws FileNotFoundException {

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = reqSize;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE
					|| height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

	}

	private static Bitmap __decodeFromFile(String orig_ImgPath, int maxImageSize) {
		//		Uri uri = getImageUri(orig_ImgPath);
		Uri uri = Uri.fromFile(new File(orig_ImgPath));
		InputStream in = null;
		try {
			in = MyApp.contentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();
			int scale = 1;
			while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > maxImageSize) {
				scale++;
			}
			Bitmap b = null;
			in = MyApp.contentResolver().openInputStream(uri);
			if (scale > 1) {
				scale--;
				// scale to max possible inSampleSize that still yields an image
				// larger than target
				o = new BitmapFactory.Options();
				o.inSampleSize = scale;
				b = BitmapFactory.decodeStream(in, null, o);

				// resize to desired dimensions
				int height = b.getHeight();
				int width = b.getWidth();
				double y = Math.sqrt(maxImageSize / (((double) width) / height));
				double x = (y / height) * width;

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
				b.recycle();
				b = scaledBitmap;

				System.gc();
			} else {
				b = BitmapFactory.decodeStream(in);
			}
			in.close();
			return b;
		} 
		catch (IOException e) {
			int jj=24;
			jj++;
			return null;
		}	
	}

	private static int getDesiredSampleSize(Options bitmapOptions, int sizeInPix) {
		int desiredWidth = sizeInPix;
		int desiredHeight = sizeInPix;
		float widthScale = (float)bitmapOptions.outWidth/desiredWidth;
		float heightScale = (float)bitmapOptions.outHeight/desiredHeight;
		float scale = Math.min(widthScale, heightScale);
		int sampleSize = 1;
		while (sampleSize < scale) {
			sampleSize *= 2;
		}	
		return sampleSize;
	}


	private static void saveBitmapToFile(Bitmap thumbnail, File reducedFile) throws Exception {
		FileOutputStream fos = new FileOutputStream(reducedFile);
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, fos);
		fos.flush();
		fos.close();
	}

	//	public static void debug____() {
	//		String orig_1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
	//				+ File.separator + "med_1.jpg"; //"large_1.jpg";
	//		String orig_2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
	//				+ File.separator + "med_2.jpg"; //"large_2.jpg";
	////		String orig_3 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
	////				+ File.separator + "large_3.jpg";
	//		
	//		File dir = new File(orig_1).getParentFile(); 
	//		
	//		String dest_1 = new File(dir, "dest_1.jpg").getAbsolutePath();
	//		String dest_2 = new File(dir, "dest_3.jpg").getAbsolutePath();
	////		String dest_3 = new File(dir, "dest_4.jpg").getAbsolutePath();
	//		
	//
	//		resizeToFile(orig_1, dest_1, 1, false);
	//		resizeToFile(orig_2, dest_2, 1, false);
	////		resizeToFile(orig_3, dest_3, 1, false);
	//		
	//		int jj=1234; 
	//		jj++;
	//		
	//	}
 

	public static void debug__writeToResizedDir(File orig_file) {
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			int jj=234;
			jj++;  
			return; 
		}
		String root = Environment.getExternalStorageDirectory().toString();
		File dir = new File(root + "/Ganbook_resized/");

		dir.mkdirs();
		
		long __len = orig_file.length();
		
		File dest_file = new File(dir, orig_file.getName());
		

		try {  
			dest_file.createNewFile();
			
			FileInputStream fileInputStream = new FileInputStream(orig_file);
			FileOutputStream fileOutputStream = new FileOutputStream(dest_file);
			int bufferSize;
			byte[] bufffer = new byte[2014*10];
			while ((bufferSize = fileInputStream.read(bufffer)) > 0) {
				fileOutputStream.write(bufffer, 0, bufferSize);
			}
			fileInputStream.close();
			fileOutputStream.close();
		}
		catch (Exception e) {
			int jj=235;
			jj++;
		}
	}

	private static DisplayImageOptions uilOptions;
	
	public static void getKidPicture(String kid_pic, CircleImageView imgView, int defImageResId) {
//		String url = kidPicToUrl(kid_pic);
		if (uilOptions==null) {
			uilOptions = UILManager.createDefaultDisplayOpyions(defImageResId);
		} 
		UILManager.imageLoader.displayImage(kid_pic, imgView, uilOptions); 
	}
	
	public static String kidPicToUrl(String kid_pic) {
		return JsonTransmitter.PICTURE_HOST + JsonTransmitter.USERS_HOST + kid_pic + ".png";
	}
	
	public static Bitmap decodeFile(File file){
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file),null,o);

			//The new size we want to scale to
			final int REQUIRED_SIZE=80;

			//Find the correct scale value. It should be the power of 2.
			int width_tmp=o.outWidth, height_tmp=o.outHeight;
			int scale=1;
			while(true){
				if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
					break;
				width_tmp/=2;
				height_tmp/=2;
				scale*=2;
			}

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}
	
	public static String getDataColumn(Context context, Uri uri) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, null, null,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}

	public static File downloadToSdcard(final String imageUrl) {
		String albumDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
				+ File.separator + Const.PICS_DIR;
		File albumDir = new File(albumDirPath);
		albumDir.mkdirs();

		File cachedImage = ImageLoader.getInstance().getDiscCache().get(imageUrl);

		String fileName = UrlUtils.urlToName(imageUrl);
		if (fileName==null) {
			fileName = System.currentTimeMillis() + ".jpeg";
		}
		OutputStream targetStream = null;
		InputStream sourceStream = null;
		String targetFilePath = albumDirPath + "/" + fileName;

		File existingFile = new File(targetFilePath);
		if (existingFile.exists() && existingFile.length() > 400) {
			return existingFile; // use existing file!
		}

		try {
			if (cachedImage != null && cachedImage.exists()) { // if image was cached by UIL
				sourceStream = new FileInputStream(cachedImage);

			} else { // otherwise - download image
				ImageDownloader downloader = new BaseImageDownloader(MyApp.context);
				sourceStream = downloader.getStream(imageUrl, null);
			}

//			http://s3.ganbook.co.il/ImageStore/1589/2073/14664/158920731466469679500.jpeg
//
			targetStream = new FileOutputStream(targetFilePath);
			IoUtils.copyStream(sourceStream, targetStream);
			File f = new File(targetFilePath);
			return f;
		}
		catch (Exception e) {
			int jj=234;
			jj++;
		}
		finally {
			if (targetStream != null) {
				try { targetStream.close(); } catch (Exception e) {}
			}
			if (sourceStream != null) {
				try { sourceStream.close(); } catch (Exception e) {}
			}

			new MyMediaScanner(targetFilePath); // gggFix force scan operation on new file
		}
		return null;
	}

	public static Bitmap getSampleBitmapFromFile(String bitmapFilePath, int reqWidth, int reqHeight) {
		// calculating image size
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(new FileInputStream(new File(bitmapFilePath)), null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int scale = calculateInSampleSize(options, reqWidth, reqHeight);

		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;

		try {
			return BitmapFactory.decodeStream(new FileInputStream(new File(bitmapFilePath)), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;

	}
}
