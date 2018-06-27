package io.charg.chargstation.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import io.charg.chargstation.R;

public class ChangeAddressDialog {

    private Context mContext;

    public ChangeAddressDialog(Context mContext) {
        this.mContext = mContext;
    }

    public void show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_change_address, null);

        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .create();

        dialog.show();
    }
}
