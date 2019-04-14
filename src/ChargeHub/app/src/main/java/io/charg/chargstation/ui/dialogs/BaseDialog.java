package io.charg.chargstation.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseDialog {

    private final Context mContext;
    private AlertDialog mDialog;

    public BaseDialog(Context context) {
        mContext = context;
        initDialog();
    }

    private void initDialog() {
        View view = LayoutInflater.from(mContext).inflate(getLayoutId(), null);
        ButterKnife.bind(this, view);

        mDialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .create();

        init();
    }

    protected abstract int getLayoutId();

    protected abstract void init();

    public void show() {
        mDialog.show();
    }

    protected AlertDialog getDialog() {
        return mDialog;
    }
}
