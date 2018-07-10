package io.charg.chargstation.ui.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;

public class EditNumberDialog {

    @BindView(R.id.tv_message)
    TextView mTvMessage;

    @BindView(R.id.edt_value)
    EditText mEtValue;

    @BindView(R.id.seek_bar)
    AppCompatSeekBar mSeekBar;

    private Context mContext;
    private AlertDialog mDialog;

    private String mTitle;
    private double mOldValue;
    private int mMin;
    private int mMax;

    private ICallbackOnComplete<Double> mOnComplete;

    public EditNumberDialog(Context context, String title, double oldValue) {
        mContext = context;
        mTitle = title;
        mOldValue = oldValue;
        initDialog();
    }

    private void initDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_change_text, null);
        ButterKnife.bind(this, view);

        mTvMessage.setText(mTitle);
        mEtValue.setText(String.valueOf(mOldValue));

        mDialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .create();
    }

    public void show() {

        mEtValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        mSeekBar.setVisibility(View.VISIBLE);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress((int) mOldValue);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mEtValue.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mDialog.show();
    }

    public void setNumberRange(int max, int min) {
        mMin = min;
        mMax = max;
    }

    @OnClick(R.id.btn_yes)
    void onBtnSaveClicked() {
        if (mOnComplete != null) {
            mOnComplete.onComplete(Double.valueOf(mEtValue.getText().toString()));
        }
        mDialog.dismiss();
    }

    @OnClick(R.id.btn_no)
    void onBtnDiscardClicked() {
        mDialog.dismiss();
    }

    public void setOnComplete(ICallbackOnComplete<Double> onComplete) {
        mOnComplete = onComplete;
    }

}
