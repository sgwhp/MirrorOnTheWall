package com.github.sgwhp.mirroronthewall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.github.sgwhp.mirroronthewall.MainActivity_;

/**
 * Created by robust on 2015/9/26.
 */
public class RemindingReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(context, MainActivity_.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.putExtras(intent);
        context.startActivity(newIntent);
    }
}
