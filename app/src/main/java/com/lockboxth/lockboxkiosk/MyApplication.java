package com.lockboxth.lockboxkiosk;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lockboxth.lockboxkiosk.helpers.Contextor;
import com.lockboxth.lockboxkiosk.helpers.MyPrefs;
import com.lockboxth.lockboxkiosk.http.model.kiosk.KioskMonitorRequest;
import com.lockboxth.lockboxkiosk.http.model.kiosk.RegisterKiosk;
import com.lockboxth.lockboxkiosk.http.repository.KioskRepository;

import java.util.Timer;
import java.util.TimerTask;


public class MyApplication extends Application {

    private static int activityCount = 0;
    private static boolean inForeGround = false;
    private static boolean monitorCreated = false;

    static {
        System.loadLibrary("native-lib");
        Log.i("MyApplication", "load native-lib completed...");
    }

    void initMonitor() {
        if (!monitorCreated) {


            MyPrefs pref = new MyPrefs(getApplicationContext());
            int profileId = pref.getKioskInfo().getGeneralprofile_id();

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    KioskRepository.Companion.getInstance1().heartbeat(
                            profileId,
                            () -> {
                                return null;
                            },
                            e -> {
                                return null;
                            }
                    );
                }
            };

//        timer.scheduleAtFixedRate(timerTask, 0, 1000);
            timer.scheduleAtFixedRate(timerTask, 0, (4 * 60 * 60) * 1000);


            registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {
                    incrementActivityCount();
                    if (!inForeGround) {
                        sendMonitor(false);
                    }
                    inForeGround = true;
                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {
//                decrementActivityCount();
//                Log.d("registerActivityLifecycleCallbacks", "onActivityPaused " + isAppInForeground() + "");
//                if (!isAppInForeground()) {
//                    isOpen = false;
//                    // send app to background
//                }
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                    decrementActivityCount();
                    if (!isAppInForeground()) {
                        Log.d("registerActivityLifecycleCallbacks", "onActivityStopped " + isAppInForeground() + "");
                        inForeGround = false;
                        sendMonitor(true);
                    }
                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {

                }
            });
            Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
            monitorCreated = true;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().init(getApplicationContext());

    }

    public static void incrementActivityCount() {
        activityCount++;
    }

    public static void decrementActivityCount() {
        activityCount--;
    }

    public static boolean isAppInForeground() {
        return activityCount > 0;
    }

    private void sendMonitor(Boolean background) {

        MyPrefs pref = new MyPrefs(Contextor.getInstance().getContext());

        if (pref.getKioskInfo() != null) {
            KioskMonitorRequest req = new KioskMonitorRequest(
                    pref.getKioskInfo().getGeneralprofile_id(),
                    background
            );

            KioskRepository.Companion.getInstance1().monitorScreen(
                    req,
                    () -> {
                        Log.d("registerActivityLifecycleCallbacks", "send monitor " + background);
//                        if (background) {
//                            Log.d("registerActivityLifecycleCallbacks", " Intent intent = new Intent(getApplicationContext(),RegisterKioskActivity.class)");
//                            Intent intent = new Intent(getApplicationContext(),RegisterKioskActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);
//                        }
                        return null;
                    },
                    e -> {
                        Log.d("registerActivityLifecycleCallbacks", "send monitor error " + e);
                        return null;
                    }
            );
        }

    }

}

class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultExceptionHandler;
    private Handler handler = new Handler(Looper.getMainLooper());

    public MyExceptionHandler() {
        defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        try {
            Log.d("registerActivityLifecycleCallbacks", "uncaughtException");

            MyPrefs pref = new MyPrefs(Contextor.getInstance().getContext());

            KioskMonitorRequest req = new KioskMonitorRequest(
                    pref.getKioskInfo().getGeneralprofile_id(),
                    true
            );

            KioskRepository.Companion.getInstance1().monitorScreen(
                    req,
                    () -> {
                        return null;
                    },
                    err -> {
                        return null;
                    }
            );
            try {
                Thread.sleep(2000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

//            Intent intent = new Intent( Contextor.getInstance().getContext(), RegisterKioskActivity.class);
//            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            Contextor.getInstance().getContext().startActivity(intent);
            Runtime.getRuntime().exit(0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}