package io.charg.chargstation.ui.fragments;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.root.ICallbackOnError;
import io.charg.chargstation.root.ICallbackOnFinish;
import io.charg.chargstation.root.ICallbackOnPrepare;
import io.charg.chargstation.services.helpers.ContractHelper;
import io.charg.chargstation.services.helpers.StringHelper;
import io.charg.chargstation.services.local.SettingsProvider;
import io.charg.chargstation.services.remote.contract.tasks.ChgAsyncTask;
import io.charg.chargstation.ui.dialogs.TxWaitDialog;

public class ExecuteContractFuncFrg extends BaseNavFragment {

    private SettingsProvider mSettingsProvider;
    private String mOperationName;

    private boolean mSuccess;

    @BindView(R.id.tv_operation)
    TextView mTvOperation;

    @BindView(R.id.tv_result)
    TextView mTvResult;

    @BindView(R.id.iv_result)
    ImageView mIvResult;

    @BindView(R.id.tv_gas_limit)
    TextView mTvGasLimit;

    @BindView(R.id.tv_gas_price)
    TextView mTvGasPrice;

    @BindView(R.id.tv_max_fee)
    TextView mTvMaxFee;

    @BindView(R.id.btn_execute)
    Button mBtnExecute;

    private ChgAsyncTask<TransactionReceipt> mTask;

    private TxWaitDialog mLoadingDialog;

    @Override
    protected int getResourceId() {
        return R.layout.frg_execute_contract;
    }

    @Override
    protected void onShows() {
        initServices();
        initUI();
    }

    private void initServices() {
        mSettingsProvider = new SettingsProvider(getContext());
    }

    private void initUI() {
        mLoadingDialog = new TxWaitDialog(getContext());
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    public void invalidate() {
        refreshUI();
    }

    private void refreshUI() {
        mBtnExecute.setText(mOperationName);
        mTvOperation.setText(mOperationName);
        mTvGasLimit.setText(String.format(Locale.getDefault(), "%d units", mSettingsProvider.getGasLimit().longValue()));
        mTvGasPrice.setText(String.format(Locale.getDefault(), "%d WEI per unit", mSettingsProvider.getGasPrice()));
        mTvMaxFee.setText(String.format(Locale.getDefault(), "%f ETH",
                Convert.fromWei(new BigDecimal(mSettingsProvider.getGasLimit().multiply(mSettingsProvider.getGasPrice())), Convert.Unit.ETHER)));
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    public boolean canNext() {
        return true;
    }

    @Override
    public boolean isValid() {
        if (!mSuccess) {
            Toast.makeText(getContext(), R.string.contract_result_not_success, Toast.LENGTH_SHORT).show();
        }

        return mSuccess;
    }

    @OnClick(R.id.btn_execute)
    void onBtnExecuteClicked() {
        mTask.setPrepareListener(new ICallbackOnPrepare() {
            @Override
            public void onPrepare() {
                mLoadingDialog.show();
            }
        });
        mTask.setFinishListener(new ICallbackOnFinish() {
            @Override
            public void onFinish() {
                mLoadingDialog.dismiss();
            }
        });
        mTask.setErrorListener(new ICallbackOnError<String>() {
            @Override
            public void onError(String message) {
                mTvResult.setText(message);
            }
        });
        mTask.setCompleteListener(new ICallbackOnComplete<TransactionReceipt>() {
            @Override
            public void onComplete(TransactionReceipt result) {
                mTvResult.setText(ContractHelper.getStatus(result));
                mIvResult.setImageResource(ContractHelper.isStatusOK(result) ? R.drawable.ic_ok : R.drawable.ic_error);
                mSuccess = ContractHelper.isStatusOK(result);
            }
        });
        mTask.executeAsync();
    }

    public void setTask(ChgAsyncTask<TransactionReceipt> task) {
        mTask = task;
    }

    public void setOperationName(String operationName) {
        mOperationName = operationName;
    }
}
