package sweetlife.android10.utils;

import android.util.Base64;

import java.io.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.*;
import org.apache.http.protocol.*;
import org.apache.http.util.*;
//import javax.net.ssl.*;
import sweetlife.android10.supervisor.Cfg;

//import javax.net.ssl.*;
import java.io.InputStream;

//import javax.net.ssl.*;

import java.net.*;

/*
class CustomSSLSocketFactory extends SSLSocketFactory {
    SSLContext sslContext = SSLContext.getInstance("TLS");

    public CustomSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        TrustManager tm = new CustomX509TrustManager();
        sslContext.init(null, new TrustManager[]{tm}, null);
    }

    public CustomSSLSocketFactory(SSLContext context) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        super(null);
        sslContext = context;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}

class CustomX509TrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
*/
public class HTTPRequest {

    private int mTimeOut = 300 * 1000;

    private String mUrl;

    private ArrayList<NameValuePair> mParameters;

    private int mResponseCode = HttpStatus.SC_NO_CONTENT;
    private String mErrorMessage;

    private String mResponse;

    public HTTPRequest(String url) {

        mUrl = url;

        mParameters = new ArrayList<NameValuePair>();
    }

    public String getResponse() {

        return mResponse;
    }

    public void setTimeOut(int timeOut) {

        mTimeOut = timeOut;
    }

    public String getErrorMessage() {

        return mErrorMessage;
    }

    public int getResponseCode() {

        return mResponseCode;
    }

    public void AddParameter(String name, String value) {
        //System.out.println(this.getClass().getCanonicalName()+" AddParameter: "+name+"/"+value);
        mParameters.add(new BasicNameValuePair(name, value));
    }

    public int Execute() throws Exception {
        //ParseException, ClientProtocolException, IOException {

        HttpPost request = new HttpPost(mUrl);

        UrlEncodedFormEntity u = new UrlEncodedFormEntity(mParameters, HTTP.UTF_8);

        if (!mParameters.isEmpty()) {
            //System.out.println(this.getClass().getCanonicalName()+" Execute: "+mUrl);
            request.setEntity(u);
        } else {

            return mResponseCode;
        }

        return ExecuteRequest(request, mUrl);
    }

    public int Execute(String xmlString) throws Exception {
        //ParseException, ClientProtocolException, IOException {
        System.out.println("Execute");
        HttpPost request = new HttpPost(mUrl);

        if (xmlString.length() != 0) {

            StringEntity stringEntity = new StringEntity(xmlString, HTTP.UTF_8);

            stringEntity.setContentType("text/xml; charset=UTF-8");

            request.setEntity(stringEntity);
        } else {

            return mResponseCode;
        }

        //return ExecuteRequest(request, mUrl);
        return ExecutePostRequest(xmlString, mUrl);
    }

    private HttpParams getHTTPParams() {

        HttpParams httpParameters = new BasicHttpParams();

        int timeoutConnection = mTimeOut;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

        int timeoutSocket = mTimeOut;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        return httpParameters;
    }

    private int ExecuteRequest(HttpUriRequest request, String url) throws Exception {
        //ParseException, IOException, ClientProtocolException {
        System.out.println("ExecuteRequest");
        HttpClient client = new DefaultHttpClient(getHTTPParams());
        /*
        SSLContext ctx = SSLContext.getInstance("TLS");
        TrustManager[] tm = new TrustManager[]{
                new CustomX509TrustManager()
        };
        ctx.init(null, tm, new java.security.SecureRandom());
        HttpClient httpClient = new DefaultHttpClient();
        ClientConnectionManager ccm = httpClient.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        sr.register(new org.apache.http.conn.scheme.Scheme("https", ssf, 443));
        HttpClient client = new DefaultHttpClient(ccm,getHTTPParams());
        */
        /*SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https",SSLSocketFactory.getSocketFactory(), 443));
        SingleClientConnManager conMgr = new SingleClientConnManager(getHTTPParams(),schReg);
        */
        /*
        KeyStore trusted = KeyStore.getInstance("BKS");
        trusted.load(null, "".toCharArray());
        SSLSocketFactory sslf = new SSLSocketFactory(trusted);
        sslf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme ("https", sslf, 443));
        SingleClientConnManager cm = new SingleClientConnManager(getHTTPParams(),                schemeRegistry);

        HttpClient client = new DefaultHttpClient(cm, getHTTPParams());
        */
        /*
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        HttpClient client = new DefaultHttpClient( getHTTPParams() );
        */

        HttpResponse httpResponse;
        HttpEntity entity = null;

        try {
            //LogHelper.debug(this.getClass().getCanonicalName()+".ExecuteRequest "+url);

            httpResponse = client.execute(request);
            System.out.println(this.getClass().getCanonicalName() + " ExecuteRequest: "
                    + url + ", code: " + httpResponse.getStatusLine().getStatusCode()
                    + ", reason: " + httpResponse.getStatusLine().getReasonPhrase()
            );
            mResponseCode = httpResponse.getStatusLine().getStatusCode();
            mErrorMessage = httpResponse.getStatusLine().getReasonPhrase();
            //System.out.println(this.getClass().getCanonicalName()+" ExecuteRequest: mResponseCode "+mResponseCode+":\nmErrorMessage:"+mErrorMessage);
            entity = httpResponse.getEntity();

            if (entity != null) {

                mResponse = EntityUtils.toString(entity, "cp-1251");
                //System.out.println(this.getClass().getCanonicalName()+" ExecuteRequest: mResponse: "+mResponse);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {

            if (entity != null) {

                entity.consumeContent();
            }
            client.getConnectionManager().shutdown();
        }
        return mResponseCode;
    }

    private int ExecutePostRequest(String xml, String url) throws Exception {
        System.out.println("ExecutePostRequest "+url+" xml: "+xml);
        //HttpClient client = new DefaultHttpClient( getHTTPParams() );
        //HttpResponse httpResponse;
        //HttpEntity entity = null;
        mResponseCode = -1;
        mErrorMessage = "";
        mResponse="";
        try {
            URL link = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) link.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setConnectTimeout(12345);
            httpURLConnection.setReadTimeout(12345);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setChunkedStreamingMode(0);
            String encoding = "UTF-8";
            httpURLConnection.setRequestProperty("charset", encoding);
           // String userCredentials = Cfg.hrcPersonalLogin + ":" + Cfg.hrcPersonalPassword;
            String userCredentials = Cfg.whoCheckListOwner() + ":" +Cfg.hrcPersonalPassword();
            String basicAuth = "Basic " + new String(android.util.Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
            httpURLConnection.setRequestProperty("Authorization", basicAuth);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(xml.getBytes(encoding));
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            outputStream.close();
            mResponseCode = httpURLConnection.getResponseCode();
            mErrorMessage = httpURLConnection.getResponseMessage();
            System.out.println("code: " + httpURLConnection.getResponseCode()
                    + ", message: " + httpURLConnection.getResponseMessage()
            );
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int intgr = bufferedInputStream.read(bytes);
            while (intgr > -1) {
                byteArrayOutputStream.write(bytes, 0, intgr);
                intgr = bufferedInputStream.read(bytes);
            }
            bufferedInputStream.close();
            //mResponse = new String(byteArrayOutputStream.toByteArray(),"cp-1251");
            mResponse = new String(byteArrayOutputStream.toByteArray(),"UTF-8");
            System.out.println("raw: " + mResponse);
        } catch (Throwable t) {
            t.printStackTrace();
            mErrorMessage = t.getMessage();
        } finally {
            //
        }
        return mResponseCode;
    }
}
