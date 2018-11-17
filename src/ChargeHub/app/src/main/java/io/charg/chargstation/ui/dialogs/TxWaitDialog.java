package io.charg.chargstation.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.charg.chargstation.R;

public class TxWaitDialog {

    private final AlertDialog mDlgLoading;

    public TxWaitDialog(Context context, String message) {
        TextView tvText = new TextView(context);
        tvText.setText(message);
        tvText.setPadding(8, 0, 0, 0);

        ProgressBar prBar = new ProgressBar(context);
        prBar.setIndeterminate(true);

        LinearLayout layout = new LinearLayout(context);
        layout.setPadding(16, 16, 16, 16);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.addView(prBar);
        layout.addView(tvText);

        mDlgLoading = new AlertDialog.Builder(context)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(layout)
                .setCancelable(false)
                .create();
    }

    public TxWaitDialog(Context context) {
        this(context, context.getString(R.string.executing_smart_contract));
    }

    public void show() {
        mDlgLoading.show();
    }

    public void dismiss() {
        mDlgLoading.dismiss();
    }
}
