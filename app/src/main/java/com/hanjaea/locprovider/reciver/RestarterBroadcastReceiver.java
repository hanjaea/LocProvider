package com.hanjaea.locprovider.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.hanjaea.locprovider.DaumMainActivity;
import com.hanjaea.locprovider.MainActivity;
import com.hanjaea.locprovider.utils.LogUtil;

public class RestarterBroadcastReceiver extends BroadcastReceiver {

    private final static String TAG = RestarterBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(">>> "+TAG, "RestarterBroadcastReceiver.onReceive");
        Toast.makeText(context, "RestarterBroadcastReceiver", Toast.LENGTH_SHORT).show();
        //context.startService(new Intent(context, BackgroundService.class));
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, DaumMainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }

    }



}
