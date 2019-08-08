package com.app.jiguar.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utility {
    private Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public static void toast(String toastMessage, Context context) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
    }

    public static void writeSharedPreferences(Context mContext, String key, String value) {
        try {
            Editor editor = mContext.getSharedPreferences(Constant.PREFS_NAME, 0).edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppPrefString(Context mContext, String key) {
        try {
            return mContext.getSharedPreferences(Constant.PREFS_NAME, 0).getString(key, "");
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(0) != null && connectivityManager.getNetworkInfo(0).getState() == State.CONNECTED) || (connectivityManager.getNetworkInfo(1) != null && connectivityManager.getNetworkInfo(1).getState() == State.CONNECTED);
    }

    public static String getDateTime() {
        return new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault()).format(new Date());
    }

    public static String getMonth() {
        return new SimpleDateFormat("mm", Locale.getDefault()).format(new Date());
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault()).format(new Date());
    }

    public static String postRequest(final Context c, String url, List<NameValuePair> data) {
        String result = "";
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Url ");
        stringBuilder.append(url);
        stringBuilder.append(" Data is ");
        stringBuilder.append(data);
        printStream.println(stringBuilder.toString());
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
            HttpConnectionParams.setSoTimeout(httpParameters, 30000);
            HttpEntity entity = ((BasicHttpResponse) new DefaultHttpClient(httpParameters).execute(httpPost)).getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity).trim();
            }
        } catch (Exception e) {
            ((Activity) c).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText((Activity) c, "unable to reach server, please try again later", Toast.LENGTH_LONG).show();
                }
            });
            e.printStackTrace();
        }
        return result;
    }

    public static String getRequest(final Context c, String url, List<NameValuePair> data) {
        String result = "";
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Url ");
        stringBuilder.append(url);
        stringBuilder.append(" Data is ");
        stringBuilder.append(data);
        printStream.println(stringBuilder.toString());
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
            HttpConnectionParams.setSoTimeout(httpParameters, 30000);
            HttpEntity entity = ((BasicHttpResponse) new DefaultHttpClient(httpParameters).execute(httpGet)).getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity).trim();
            }
        } catch (Exception e) {
            ((Activity) c).runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText((Activity) c, "unable to reach server, please try again later", Toast.LENGTH_LONG).show();
                }
            });
            e.printStackTrace();
        }
        return result;
    }
}
