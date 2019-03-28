package io.charg.chargstation.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;

public class RequestStationEthAddressDialog {

    private final Context mContext;

    private View mView;
    private AlertDialog mDialog;
    private ICallbackOnComplete<String> mOnComplete;

    @BindView(R.id.tv_station_id)
    TextView mTvStationId;

    public RequestStationEthAddressDialog(Context context) {
        mContext = context;
        initView();
        initDialog();
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.dlg_request_station_eth_address, null);
        ButterKnife.bind(this, mView);
    }

    private void initDialog() {
        mDialog = new AlertDialog.Builder(mContext)
                .setView(mView)
                .create();
    }

    public void show() {
        mDialog.show();
    }

    @OnClick(R.id.btn_yes)
    void onBtnSaveClicked() {
        if (mOnComplete != null) {
            mOnComplete.onComplete(mTvStationId.getText().toString());
        }
        mDialog.dismiss();
    }

    @OnClick(R.id.btn_no)
    void onBtnDiscardClicked() {
        mDialog.dismiss();
    }

    public void setOnComplete(ICallbackOnComplete<String> onComplete) {
        mOnComplete = onComplete;
    }

}
