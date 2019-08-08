package com.app.jiguar.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.app.jiguar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends Activity {

    private Handler handler;
    private Runnable runnable;
    private FirebaseRemoteConfig mRemoteConfig;
    private long cacheExpiration = 3600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        // fetch
        mRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            mRemoteConfig.activateFetched();
                            String version = "1.0";
                            String remoteVersion = mRemoteConfig.getString("android_update_me_current_version");

                            try {
                                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = pInfo.versionName;
                                if (remoteVersion.equalsIgnoreCase(version)) {
                                    handler = new Handler();
                                    runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    };
                                    handler.postDelayed(runnable, 1200);
                                } else {
                                    new AlertDialog.Builder(SplashActivity.this)
                                            .setCancelable(false)
                                            .setTitle("New version available!")
                                            .setMessage("Please update app to new version to continue using.")
                                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.app.jiguar&hl=en"));
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).show();
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                        } else {
                            //task failed
                        }
                    }
                });

    }
}
