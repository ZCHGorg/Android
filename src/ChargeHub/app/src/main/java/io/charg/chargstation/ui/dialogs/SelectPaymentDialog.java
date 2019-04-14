package io.charg.chargstation.ui.dialogs;

import android.content.Context;

import butterknife.OnClick;
import io.charg.chargstation.R;

public class SelectPaymentDialog extends BaseDialog {

    private Runnable mCreditCardClickListener;
    private Runnable mChargCoinClickListener;

    public SelectPaymentDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dlg_select_payment;
    }

    @Override
    protected void init() {

    }

    public void setCreditCardClickListener(Runnable callback) {
        mCreditCardClickListener = callback;
    }

    public void setChargCoinClickListener(Runnable chargCoinClickListener) {
        mChargCoinClickListener = chargCoinClickListener;
    }

    @OnClick(R.id.btn_payment_credit_card)
    void onBtnPaymentCreditCardClicked() {
        if (mCreditCardClickListener != null) {
            mCreditCardClickListener.run();
        }

        getDialog().dismiss();
    }

    @OnClick(R.id.btn_payment_chg)
    void onBtnPaymentChgClicked() {
        if (mChargCoinClickListener != null) {
            mChargCoinClickListener.run();
        }

        getDialog().dismiss();

    }

}
