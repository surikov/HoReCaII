package reactive.ui;

import java.io.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
/*
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
*/
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
//import android.net.http.AndroidHttpClient;
import android.os.*;
import android.util.Base64;

import tee.binding.properties.*;
import tee.binding.task.*;
import tee.binding.*;

public class RawSOAP {
	public NoteProperty<RawSOAP> url = new NoteProperty<RawSOAP>(this);
	public NoteProperty<RawSOAP> xml = new NoteProperty<RawSOAP>(this);
	public NoteProperty<RawSOAP> statusDescription = new NoteProperty<RawSOAP>(this);
	public NoteProperty<RawSOAP> responseEncoding = new NoteProperty<RawSOAP>(this);
	public NoteProperty<RawSOAP> requestEncoding = new NoteProperty<RawSOAP>(this);
	public NumericProperty<RawSOAP> statusCode = new NumericProperty<RawSOAP>(this);
	public NumericProperty<RawSOAP> responseCode = new NumericProperty<RawSOAP>(this);
	public NumericProperty<RawSOAP> timeout = new NumericProperty<RawSOAP>(this);
	public ItProperty<RawSOAP, Task> afterSuccess = new ItProperty<RawSOAP, Task>(this);
	public ItProperty<RawSOAP, Task> afterError = new ItProperty<RawSOAP, Task>(this);
	public ItProperty<RawSOAP, Throwable> exception = new ItProperty<RawSOAP, Throwable>(this);
	public Bough data=null;
	public String rawResponse = null;

	public RawSOAP() {
		timeout.is(60 * 1000);
		responseEncoding.is("UTF-8");
		requestEncoding.is("UTF-8");
		statusCode.is(-1);
		responseCode.is(-1);
	}

	/*
		public static void _post() {
			HttpURLConnection client = null;
			try {
				URL url = new URL("http://exampleurl.com/");
				client = (HttpURLConnection) url.openConnection();
				client.setRequestMethod("POST");
				client.setRequestProperty("Key", "Value");
				client.setDoOutput(true);
				OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
				writeStream(outputPost);
				outputPost.flush();
				outputPost.close();
				client.setFixedLengthStreamingMode(outputPost.getBytes().length);
				client.setChunkedStreamingMode(0);
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				if (client != null) // Make sure the connection is not null.
					client.disconnect();
			}
		}
	*/
/*
	public static String entity2String(HttpEntity entity, String defaultCharset) throws Exception {
		if (entity == null) {
			throw new IllegalArgumentException("HTTP entity may not be null");
		}
		InputStream is = entity.getContent();
		if (is == null) {
			return null;
		}
		BufferedInputStream instream = new BufferedInputStream(is);
		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
		}
		int i = (int) entity.getContentLength();
		if (i < 0) {
			i = 4096;
		}
		//String charset = getContentCharSet(entity);
		String charset = null;
		if (entity.getContentType() != null) {
			HeaderElement values[] = entity.getContentType().getElements();
			if (values.length > 0) {
				NameValuePair param = values[0].getParameterByName("charset");
				if (param != null) {
					charset = param.getValue();
				}
			}
		}
		if (charset == null) {
			charset = defaultCharset;
		}
		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}
		Reader reader = new InputStreamReader(instream, charset);
		CharArrayBuffer buffer = new CharArrayBuffer(i);
		try {
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}
*/
	public void startNow(String login,String password) {
		System.out.println("startNow "+url.property.value()+": "+login+"/"+password);
		HttpURLConnection httpURLConnection = null;
		try {
			statusCode.is(-2);
			URL link = new URL(url.property.value());
			httpURLConnection = (HttpURLConnection) link.openConnection();

			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(timeout.property.value().intValue());
			httpURLConnection.setReadTimeout(timeout.property.value().intValue());
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setChunkedStreamingMode(0);
			httpURLConnection.setRequestProperty("charset", requestEncoding.property.value());

			if(login!=null && password!=null) {
				String userCredentials = login + ":" + password;
				String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
				httpURLConnection.setRequestProperty("Authorization", basicAuth);
			}

			//httpURLConnection.getreq
			//System.out.println("charset "+ requestEncoding.property.value());
			//responseCode.is(httpURLConnection.getResponseCode());
			//System.out.println("responseCode "+responseCode);
			//HttpPost request = new HttpPost(url.property.value());
			//BasicHttpParams httpParameters = new BasicHttpParams();

			//HttpConnectionParams.setConnectionTimeout(httpParameters, timeout.property.value().intValue());
			//HttpConnectionParams.setSoTimeout(httpParameters, timeout.property.value().intValue());
			//HttpConnectionParams.setSocketBufferSize(httpParameters, 16 * 8192);
			//HttpConnectionParams.setConnectionTimeout(httpParameters, timeout.property.value().intValue());
			//HttpConnectionParams.setStaleCheckingEnabled(httpParameters, true);

			//DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			//StringEntity stringEntity = new StringEntity(xml.property.value(), requestEncoding.property.value());
			//stringEntity.setContentType("text/xml; charset=" + requestEncoding.property.value());
			//request.setEntity(stringEntity);
			statusCode.is(-3);
			//HttpResponse httpResponse = client.execute(request);
			OutputStream outputStream=httpURLConnection.getOutputStream();
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
			//httpURLConnection.connect();
			bufferedOutputStream.write(xml.property.value().getBytes(requestEncoding.property.value()));

			bufferedOutputStream.flush();
			bufferedOutputStream.close();
			outputStream.close();
			statusCode.is(-4);
			//statusCode.is(httpResponse.getStatusLine().getStatusCode());
			//statusDescription.is(httpResponse.getStatusLine().getReasonPhrase());
			//HttpEntity entity = httpResponse.getEntity();
			//rawResponse = entity2String(entity, responseEncoding.property.value());
			InputStream inputStream=httpURLConnection.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

			byte[] bytes = new byte[1024];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int intgr = bufferedInputStream.read(bytes);
			while (intgr > -1) {
				byteArrayOutputStream.write(bytes, 0, intgr);
				intgr = bufferedInputStream.read(bytes);
			}

			bufferedInputStream.close();

			rawResponse = new String(byteArrayOutputStream.toByteArray());
			data = tee.binding.Bough.parseXML(rawResponse);
			responseCode.is(httpURLConnection.getResponseCode());
			statusCode.is(httpURLConnection.getResponseCode());
		} catch (Throwable t) {
			exception.is(t);
			t.printStackTrace();
			if(httpURLConnection!=null) {
				try {
					responseCode.is(httpURLConnection.getResponseCode());
					statusCode.is(httpURLConnection.getResponseCode());
				}catch (Throwable tt){
					//
				}
			}
			//t.printStackTrace();

		}
		//System.out.println("startNow done");
	}

	public void startLater(Context context, String alert,final String login,final String password) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(alert);
		final AlertDialog dialog = builder.show();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... r) {
				startNow( login, password);
				return null;
			}

			@Override
			protected void onPostExecute(Void r) {
				if (dialog != null) {
					dialog.dismiss();
				}
				if (exception.property.value() == null) {
					if (afterSuccess.property.value() != null) {
						afterSuccess.property.value().start();
					}
				} else {
					if (afterError.property.value() != null) {
						afterError.property.value().start();
					}
				}
			}
		}.execute();
	}
}
