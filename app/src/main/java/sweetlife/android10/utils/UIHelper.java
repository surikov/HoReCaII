package sweetlife.android10.utils;

import sweetlife.android10.R;
import sweetlife.android10.Settings;
import sweetlife.android10.log.LogHelper;

import java.io.*;
import java.net.*;
import java.util.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.*;
import android.widget.*;
import android.os.*;
import android.graphics.*;

public class UIHelper {
	static boolean lockShowPhotoByArtikul = false;

	public interface IMessageBoxCallbackInteger {
		public void MessageBoxResult(int which);
	}

	public interface IMessageBoxCallbackDouble {
		public void MessageBoxResult(double which);
	}

	public interface IMessageBoxCallbackString {
		public void MessageBoxResult(String which);
	}

	public static void MsgBox(String title//
			, String message//
			, Context context//
	) {
		MsgBox(title, message, context, null);
	}
	public static void MsgBox(String title//
			, String message//
			, Context context//
			, final IMessageBoxCallbackInteger msgCallback//
	) {
		LogHelper.debug("UIHelper.MsgBox: " + title + ": " + message);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		dialogBuilder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (msgCallback != null) {
					msgCallback.MessageBoxResult(which);
				}
			}
		});
		dialogBuilder.create().show();
	}
	public static void MsgBox(String title//
			, String message//
			, Context context//
			, String leftBtn//
			, String rightBtn//
			, final IMessageBoxCallbackInteger callbackLeftBtn//
			, final IMessageBoxCallbackInteger callbackRightBtn//
	) {
		LogHelper.debug("UIHelper.MsgBox: " + title + ": " + message);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		dialogBuilder.setPositiveButton(leftBtn, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callbackLeftBtn != null) {
					LogHelper.debug("UIHelper.MsgBox: OK");
					callbackLeftBtn.MessageBoxResult(which);
				}
				else {
					LogHelper.debug("UIHelper.MsgBox: Cancel");
					dialog.dismiss();
				}
			}
		});
		dialogBuilder.setNegativeButton(rightBtn, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callbackRightBtn != null) {
					LogHelper.debug("UIHelper.MsgBox: OK");
					callbackRightBtn.MessageBoxResult(which);
				}
				else {
					LogHelper.debug("UIHelper.MsgBox: Cancel");
					dialog.dismiss();
				}
			}
		});
		dialogBuilder.create().show();
	}
	public static void MsgBox(String title//
			, String message//
			, Context context//
			, final IMessageBoxCallbackInteger callbackLeftBtn//
			, final IMessageBoxCallbackInteger callbackRightBtn//
	) {
		UIHelper.MsgBox(title//
				, message//
				, context//
				, context.getResources().getString(R.string.ok)//
				, context.getResources().getString(R.string.cancel)//
				, callbackLeftBtn//
				, callbackRightBtn//
		);
	}
	public static void MsgBox(String title//
			, String message//
			, Context context//
			, String positiveButtonTitle//
			, final IMessageBoxCallbackInteger callbackPositiveBtn//
			, String neutralButtonTitle//
			, final IMessageBoxCallbackInteger callbackNeutralBtn//
			, String negativeButtonTitle//
			, final IMessageBoxCallbackInteger callbackNegativeBtn//
	) {
		LogHelper.debug("UIHelper.MsgBox: " + title + ": " + message);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		dialogBuilder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callbackNegativeBtn != null) {
					callbackNegativeBtn.MessageBoxResult(which);
				}
				dialog.dismiss();
			}
		});
		dialogBuilder.setNeutralButton(neutralButtonTitle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callbackNeutralBtn != null) {
					callbackNeutralBtn.MessageBoxResult(which);
				}
				dialog.dismiss();
			}
		});
		dialogBuilder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callbackPositiveBtn != null) {
					callbackPositiveBtn.MessageBoxResult(which);
				}
				dialog.dismiss();
			}
		});
		dialogBuilder.create().show();
	}
	public static void MsgBoxList(String title//			
			, Context context//
			, Vector<String> rows //
			, final IMessageBoxCallbackInteger callback//
			, String positiveButtonTitle//
			, final IMessageBoxCallbackInteger callbackPositiveBtn//
			, String negativeButtonTitle//
			, final IMessageBoxCallbackInteger callbackNegativeBtn//
	) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		String[] strings = new String[rows.size()];
		for (int i = 0; i < rows.size(); i++) {
			strings[i] = rows.get(i);
		}
		builder.setItems(strings, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// The 'which' argument contains the index position
				// of the selected item
				//LogHelper.debug("which " + which);
				if (callback != null) {
					callback.MessageBoxResult(which);
				}
			}
		});
		builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callbackPositiveBtn != null) {
					callbackPositiveBtn.MessageBoxResult(which);
				}
			}
		});
		builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callbackNegativeBtn != null) {
					callbackNegativeBtn.MessageBoxResult(which);
				}
			}
		});
		builder.create().show();
	}
	/*public static void MsgBoxInteger(String title//			
			,String message//
			, Context context//
			, int num //
			, String positiveButtonTitle//
			, final IMessageBoxCallbackInteger callbackPositiveBtn//
			, String negativeButtonTitle//
			, final IMessageBoxCallbackInteger callbackNegativeBtn//
	) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		final EditText input = new EditText(context);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setText(""+num);
		builder.setView(input);
		builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
				try {
					int nn = Integer.parseInt(input.getText().toString());
					if (callbackPositiveBtn != null) {
						callbackPositiveBtn.MessageBoxResult(nn);
					}
				}
				catch (NumberFormatException nfe) {
					//System.out.println("Could not parse " + nfe);
				}
				// Do something with value!
			}
		});
		builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (callbackNegativeBtn != null) {
					callbackNegativeBtn.MessageBoxResult(which);
				}
				dialog.dismiss();
			}
		});
		builder.create().show();
	}*/
	public static void MsgBoxDouble(String title//			
			, String message//
			, Context context//
			, double num //
			, String positiveButtonTitle//
			, final IMessageBoxCallbackDouble callbackPositiveBtn//
			, String negativeButtonTitle//
			, final IMessageBoxCallbackDouble callbackNegativeBtn//
			, String neutralButtonTitle//
			, final IMessageBoxCallbackDouble callbackNeutralBtn//
	) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		final EditText input = new EditText(context);
		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		input.setText("" + num);
		builder.setView(input);
		if (callbackPositiveBtn != null)
			builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					try {
						double nn = Double.parseDouble(input.getText().toString());
						if (callbackPositiveBtn != null) {
							callbackPositiveBtn.MessageBoxResult(nn);
						}
					}
					catch (NumberFormatException nfe) {
						System.out.println("Could not parse " + nfe);
					}
				}
			});
		if (callbackNegativeBtn != null)
			builder.setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (callbackNegativeBtn != null) {
						callbackNegativeBtn.MessageBoxResult(which);
					}
					dialog.dismiss();
				}
			});
		if (callbackNeutralBtn != null)
			builder.setNeutralButton(neutralButtonTitle, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (callbackNeutralBtn != null) {
						callbackNeutralBtn.MessageBoxResult(which);
					}
					dialog.dismiss();
				}
			});
		builder.create().show();
	}
	public static void MsgBoxString(String title//			
			, String message//
			, Context context//
			, String txt //
			, String positiveButtonTitle//
			, final IMessageBoxCallbackString callbackPositiveBtn//
	) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		//builder.setMessage(message);
		final EditText input = new EditText(context);
		//input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		input.setText(txt);
		builder.setView(input);
		if (callbackPositiveBtn != null)
			builder.setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if (callbackPositiveBtn != null) {
						callbackPositiveBtn.MessageBoxResult("" + input.getText());
					}
				}
			});
		builder.create().show();
	}
	public static void quickWarning(String s, Context context) {
		//System.out.println("quickWarning: " + s);
		Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
	}
	public static Bitmap loadImageFromURL(String url) {
		//System.out.println("loadImageFromURL " + url);
		Bitmap bitmap = null;
		URL m;
		InputStream i = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream out = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
			bis = new BufferedInputStream(i, 1024 * 8);
			out = new ByteArrayOutputStream();
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = bis.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			bis.close();
			byte[] data = out.toByteArray();
			//System.out.println("decodeByteArray " + data.length);
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		catch (Throwable t) {
			//System.out.println(t.getMessage());
		}
		//System.out.println("bitmap " + bitmap);
		return bitmap;
	}
	/*public static Bitmap loadImageFromURL(String str) {
		Bitmap bm = null;
		try {
			InputStream input = new java.net.URL(str).openStream();
			bm = BitmapFactory.decodeStream(input);
			input.close();
		}
		catch (Throwable t) {
			//System.out.println(t.getMessage());
			t.printStackTrace();
		}
		System.out.println("bm is " + bm);
		return bm;
	}*/
	public static void showPhotoByArtikul(final String artikul, final String name, final Context context) {
		//System.out.println("showPhotoByArtikul " + artikul);
		if (lockShowPhotoByArtikul) {
			quickWarning("Подождите", context);
			return;
		}
		lockShowPhotoByArtikul = true;
		new AsyncTask<String, Integer, Bitmap>() {
			@Override
			protected Bitmap doInBackground(String... params) {
				//System.out.println("doInBackground " + artikul);
				Bitmap bm = null;
				try {
					String artikul = params[0];
					artikul = artikul.replace('/', '-');
					artikul = artikul.replace('\\', '-');
					bm = loadImageFromURL(Settings.getInstance().getBaseURL()+"photo/" + artikul + ".bmp");
					if (bm == null) {
						bm = loadImageFromURL(Settings.getInstance().getBaseURL()+"photo/" + artikul + ".jpg");
					}
					if (bm == null) {
						bm = loadImageFromURL(Settings.getInstance().getBaseURL()+"photo/" + artikul + ".gif");
					}
					if (bm == null) {
						bm = loadImageFromURL(Settings.getInstance().getBaseURL()+"photo/" + artikul + ".png");
					}
				}
				catch (Throwable t) {
					t.printStackTrace();
					return null;
				}
				//System.out.println("doInBackground done " + artikul);
				return bm;
			}
			@Override
			protected void onPostExecute(Bitmap result) {
				try {
					//System.out.println("onPostExecute " + result);
					if (result != null) {
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle( artikul+": "+name);
						ImageView img = new ImageView(context);
						img.setImageBitmap(result);
						builder.setView(img);
						//System.out.println("before show " + result);
						builder.create().show();
						
					}
					else {
						//quickWarning("Нет фотографии", context);
						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle("Артикул " + artikul + ": " + name);
						builder.setMessage("Нет фотографии.");
						//System.out.println("before show " + result);
						builder.create().show();
					}
				}
				catch (Throwable t) {
					//System.out.println(t.getMessage());
				}
				lockShowPhotoByArtikul = false;
				//System.out.println("done onPostExecute");
			}
		}.execute(artikul);
	}
}
