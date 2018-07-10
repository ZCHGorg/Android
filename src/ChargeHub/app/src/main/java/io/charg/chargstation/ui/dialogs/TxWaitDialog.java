package io.charg.chargstation.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import io.charg.chargstation.R;

public class TxWaitDialog {

    private final AlertDialog mDlgLoading;

    public TxWaitDialog(Context context) {
        mDlgLoading = new AlertDialog.Builder(context)
                .setMessage(R.string.transferring)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .create();
    }

    public TxWaitDialog(Context context, String message) {
        mDlgLoading = new AlertDialog.Builder(context)
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .create();
    }

    public void show() {
        mDlgLoading.show();
    }

    public void dismiss() {
        mDlgLoading.dismiss();
    }
}
