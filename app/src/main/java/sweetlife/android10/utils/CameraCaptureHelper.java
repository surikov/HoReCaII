package sweetlife.android10.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sweetlife.android10.log.LogHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

public class CameraCaptureHelper {
	public static final String JPEG_FILE_PREFIX = "IMG_";
	public static final String JPEG_FILE_SUFFIX = ".jpg";
	public static final String CAMERA_DIR = "/dcim/";
	// Determine how much to scale down the image
	private static final int SCALE_FACTOR = 2;
	private String mCurrentPhotoPath;

	public File getAlbumStorageDir(String albumName) {
		return new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
	}
	private File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = getAlbumStorageDir("Horeca");
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						return null;
					}
				}
			}
		}
		return storageDir;
	}
	public File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File image = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, getAlbumDir());
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
	public void galleryAddPic(Activity activity) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		activity.sendBroadcast(mediaScanIntent);
	}
	public Bitmap getScaleBitmap() {
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		//int photoW = bmOptions.outWidth;
		//int photoH = bmOptions.outHeight;
		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = SCALE_FACTOR;
		bmOptions.inPurgeable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		return bitmap;
	}
	public static String BitmapToString(String path) {
		return BitmapToString(BitmapFactory.decodeFile(path));
	}
	public static String BitmapToString(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (bitmap == null) {
			LogHelper.debug("BitmapToString is null");
			return "";
		}
		else {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
		}
	}
	public static String getFileExtension(String path) {
		return path.substring(path.lastIndexOf(".") + 1, path.length());
	}
	public static String getFileName(String path) {
		if (path != null && path.length() != 0) {
			return path.substring(path.lastIndexOf("/") + 1, path.length());
		}
		return "";
	}
	public String getCurrentPhotoPath() {
		return mCurrentPhotoPath;
	}
}
