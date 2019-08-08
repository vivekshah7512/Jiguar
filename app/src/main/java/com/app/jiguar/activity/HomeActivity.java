package com.app.jiguar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.jiguar.R;
import com.app.jiguar.utilities.Utility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kobakei.ratethisapp.RateThisApp;

import org.apache.http.NameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends Activity {

    private Context mContext;
    private WebView webView, webview_hidden;
    private ProgressDialog pd;
    private LinearLayout ll_no_data;
    private Button btn_retry;
    private String Url = "", userid = "";

    public static boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        RateThisApp.onCreate(this);
        setContentView(R.layout.activity_home);

        RateThisApp.Config config = new RateThisApp.Config(2, 3);
        config.setTitle(R.string.rate_title);
        config.setMessage(R.string.rate_message);
        config.setYesButtonText(R.string.rate_positive);
        config.setNoButtonText(R.string.rate_negative);
        config.setCancelButtonText(R.string.rate_nautral);
        RateThisApp.init(config);

        initUI();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                Utility.writeSharedPreferences(mContext, "device_token", newToken);
            }
        });

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadURL();
            }
        });

        Calendar c = Calendar.getInstance();
        String month = String.valueOf(c.get(Calendar.MONTH) + 1);
        Log.v("Month :", month);
        if (Utility.getAppPrefString(mContext, "month") != null &&
                !Utility.getAppPrefString(mContext, "month").equalsIgnoreCase("")) {
            if (!month.equalsIgnoreCase(Utility.getAppPrefString(mContext, "month"))) {
                Utility.writeSharedPreferences(mContext, "month", month);
                RateThisApp.showRateDialog(HomeActivity.this);
            }
        } else {
            Utility.writeSharedPreferences(mContext, "month", month);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initUI() {
        mContext = HomeActivity.this;
        webView = (WebView) findViewById(R.id.webview);
        webview_hidden = (WebView) findViewById(R.id.webview_hidden);
        ll_no_data = (LinearLayout) findViewById(R.id.ll_no);
        btn_retry = (Button) findViewById(R.id.btn_retry);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        WebSettings webSettings1 = webview_hidden.getSettings();
        webSettings1.setJavaScriptEnabled(true);
        webview_hidden.getSettings().setLoadWithOverviewMode(true);
        webview_hidden.getSettings().setUseWideViewPort(true);

        loadURL();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            if (Url.equalsIgnoreCase("https://app.jiguar.com/leaper/ChildrenList")) {
                System.exit(0);
                finishAffinity();
            } else {
                this.webView.goBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void loadURL() {
        if (isNetworkAvaliable(mContext)) {
            ll_no_data.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl("https://app.jiguar.com/login");
            webView.addJavascriptInterface(new MyJavaScriptInterface(HomeActivity.this), "HtmlViewer");
            webview_hidden.addJavascriptInterface(new MyJavaScriptInterface(HomeActivity.this), "HtmlViewer");
            pd = ProgressDialog.show(mContext, "",
                    "Please wait as this may take a few minutes...", true);

            webView.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(mContext, description, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    pd.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Url = url;
                    pd.dismiss();
                    if (url.equalsIgnoreCase("https://app.jiguar.com/leaper/ChildrenList")) {
                        webview_hidden.loadUrl("https://app.jiguar.com/Myphone");
                    }
                   /* else if (url.equalsIgnoreCase("https://app.jiguar.com/login")) {
                        RateThisApp.showRateDialog(HomeActivity.this);
                    }*/
                }
            });

            webview_hidden.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(mContext, description, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    pd.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    Url = url;
                    pd.dismiss();
                    if (url.equalsIgnoreCase("https://app.jiguar.com/Myphone")) {
                        webview_hidden.loadUrl("javascript:HtmlViewer.showHTML" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }
                }
            });
        } else {
            ll_no_data.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }
    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html) {
            String output = html.substring(html.indexOf("<body>") + 6, html.lastIndexOf("</body>"));
            try {
                JSONObject obj = null;
                String stringResponse = output;
                JSONTokener tokener = new JSONTokener(stringResponse);
                obj = new JSONObject(tokener);
                userid = obj.getString("userid");
                System.out.println(userid);

                if (Utility.isNetworkAvaliable(mContext)) {
                    try {
                        sendToken getTask = new sendToken(mContext);
                        getTask.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class sendToken extends AsyncTask<String, Integer, Object> {

        private final Context mContext;

        public sendToken(final Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                getAboutMeListItem();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
        }

        // ======================================================================================================

        private Object getAboutMeListItem() {
            String webUrl = "https://admin.jiguar.com/api/ParentsPhoneInfos/GetParentsPhoneInfos?id=" +
                    userid +
                    "&PhoneType=2&PhoneToken=" +
                    Utility.getAppPrefString(mContext, "device_token");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                String response1 = Utility.getRequest(mContext, webUrl, nameValuePairs);
                JSONObject jObject = new JSONObject(response1);
                Log.v("response", jObject.toString() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}