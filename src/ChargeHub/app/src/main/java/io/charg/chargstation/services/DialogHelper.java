package io.charg.chargstation.services;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import io.charg.chargstation.R;

/**
 * Created by worker on 24.11.2017.
 */

public class DialogHelper {

    public static void showQuestion(Context context, String message, final Runnable runnable) {
        new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(R.string.app_name)
                .setIcon(R.drawable.charg)
                .setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        runnable.run();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static AlertDialog createDialogFromView(Context context, View view) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.app_name)
                .setIcon(R.drawable.charg)
                .setView(view)
                .create();
    }
}
