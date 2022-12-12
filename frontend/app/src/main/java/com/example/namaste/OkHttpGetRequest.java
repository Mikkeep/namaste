package com.example.namaste;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpGetRequest {

    // from GitHub Gist:
    // https://gist.github.com/preethamhegdes/fcab7bced52bf2520994ce232f2102ed
    private static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
            Log.d("OKHTTP3", "Exception while configuring IgnoreSslCertificate");
        }
        return builder;
    }

    public Response doGetRequest(String url, String cookie) {
        OkHttpClient client = null;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);


        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder = configureToIgnoreCertificate(builder);
        client = builder
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                .followSslRedirects(true)
                .followRedirects(true)
                .build();

        String baseUrl = "http://10.0.2.2:5000/api/";
        url = baseUrl + url;

        Response response = null;

        if (url.contains("restaurant/all")) {
            Request newReq = new Request.Builder()
                    .addHeader("Cookie", "session=" + cookie)
                    .url(url)
                    .build();
            try {
                response = client.newCall(newReq).execute();
                Log.d("OKHTTP3", "Request done, got the response");
            } catch (IOException e) {
                Log.d("OKHTTP3", "Exception while doing request.");
                e.printStackTrace();
            }
        }

        if (url.contains("file")) {
            Request newReq = new Request.Builder()
                    .addHeader("Cookie", "session=" + cookie)
                    .url(url)
                    .build();
            try {
                response = client.newCall(newReq).execute();
                Log.d("OKHTTP3", "Request done, got the response");
            } catch (IOException e) {
                Log.d("OKHTTP3", "Exception while doing request.");
                e.printStackTrace();
            }
        }

        if (url.contains("users")) {
            Request newReq = new Request.Builder()
                    .addHeader("Cookie", "session=" + cookie)
                    .url(url)
                    .build();
            try {
                response = client.newCall(newReq).execute();
                Log.d("OKHTTP3", "Request done, got the response");
            } catch (IOException e) {
                Log.d("OKHTTP3", "Exception while doing request.");
                e.printStackTrace();
            }
        }

        return response;
    }
}
