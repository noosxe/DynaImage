package com.noosxe.android;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DynaImage extends ImageView {
	private final DynaImage self = this;
	
	private class LoadImageTask extends AsyncTask<String, Integer, Bitmap> {
		protected Bitmap doInBackground(String... s) {
			Bitmap bitmap = null;

			try {
				String imageUrl = s[0];
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(input, null, options);

				options.inSampleSize = calculateInSampleSize(options, self.getMeasuredWidth(), self.getMeasuredHeight());
				options.inJustDecodeBounds = false;
				HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
				connection1.setDoInput(true);
				connection1.connect();
				input = connection1.getInputStream();
				bitmap = BitmapFactory.decodeStream(input, null, options);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(Bitmap result) {
			self.setImageBitmap(result);
		}

		private int calculateInSampleSize(
				BitmapFactory.Options options, int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {

				// Calculate ratios of height and width to requested height and width
				final int heightRatio = Math.round((float) height / (float) reqHeight);
				final int widthRatio = Math.round((float) width / (float) reqWidth);

				// Choose the smallest ratio as inSampleSize value, this will guarantee
				// a final image with both dimensions larger than or equal to the
				// requested height and width.
				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}

			return inSampleSize;
		}
	}
	
	public DynaImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void loadFromUrl(String url) {
		new LoadImageTask().execute(url);
	}
}
