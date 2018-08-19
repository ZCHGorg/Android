package io.charg.chargstation.services.helpers;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import io.charg.chargstation.R;

import static android.content.Context.WINDOW_SERVICE;

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

    public static void showQrCode(final Context context, final String text) {
        WindowManager manager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        if (manager == null) {
            return;
        }
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        try {

            @SuppressLint("InflateParams")
            View dlgView = LayoutInflater.from(context).inflate(R.layout.dlg_qr, null);

            ImageView imQr = dlgView.findViewById(R.id.iv_qr);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, smallerDimension, smallerDimension);
            imQr.setImageBitmap(bitmap);

            TextView tvAddress = dlgView.findViewById(R.id.tv_address);
            tvAddress.setText(StringHelper.getShortEthAddress(text));

            ImageView btnCopy = dlgView.findViewById(R.id.btn_copy);
            btnCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(text);
                    Toast.makeText(context, "Your address copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });

            new AlertDialog.Builder(context)
                    .setView(dlgView)
                    .show();

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

