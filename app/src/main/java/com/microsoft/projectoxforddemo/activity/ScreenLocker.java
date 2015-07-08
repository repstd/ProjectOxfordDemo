package com.microsoft.projectoxforddemo.activity;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by yulw on 7/8/2015.
 */
public class ScreenLocker extends Service
{
    BroadcastReceiver m_receiver;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KeyguardManager.KeyguardLock key;
        KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        key = km.newKeyguardLock("IN");
        key.disableKeyguard();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        m_receiver = new ScreenLockerReceiver();
        registerReceiver(m_receiver, filter);
        Log.d("ScreenLocker","StartFaceLockerFromService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(m_receiver);
    }
}
