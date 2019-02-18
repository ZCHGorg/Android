package io.charg.chargstation.ui.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.core.methods.response.EthGasPrice;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.services.helpers.ContractHelper;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.helpers.DialogHelper;
import io.charg.chargstation.services.helpers.StringHelper;

/**
 * Created by worker on 13.11.2017.
 */

public class ChangeWalletActivity extends BaseActivity {

    public static final String QR_MODE_PRIVATE_KEY = "QR_MODE_PRIVATE_KEY";

    @BindView(R.id.tv_cur_eth_address)
    TextView tvCurEthAddress;

    @BindView(R.id.tv_new_eth_address)
    TextView tvNewEthAddress;

    @BindView(R.id.tv_cur_private_key)
    TextView tvCurPrivateKey;

    @BindView(R.id.tv_new_private_key)
    TextView tvNewPrivateKey;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private String mNewPrivateKey;
    private static String mQrMode;
    private AccountService mAccountService;

    @Override
    public int getResourceId() {
        return R.layout.activity_change_wallet;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
        refreshUI();
    }

    private void initServices() {
        mAccountService = new AccountService(this);
    }

    private void refreshUI() {
        tvCurEthAddress.setText(StringHelper.getValueOrNotDefine(mAccountService.getEthAddress()));

        if (mNewPrivateKey == null) {
            tvNewEthAddress.setText(StringHelper.getValueOrNotDefine(mNewPrivateKey));
        } else {
            tvNewEthAddress.setText(StringHelper.getValueOrNotDefine(Credentials.create(mNewPrivateKey).getAddress()));
        }

        tvCurPrivateKey.setText(StringHelper.getValueOrNotDefine(mAccountService.getPrivateKey()));
        tvNewPrivateKey.setText(StringHelper.getValueOrNotDefine(mNewPrivateKey));
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @OnClick(R.id.btn_change_prv_key)
    void onBtnChangePrvClicked() {
        View view = getLayoutInflater().inflate(R.layout.dlg_change_text, null, false);

        final AlertDialog dialog = DialogHelper.createDialogFromView(this, view);

        TextView tvMessage = view.findViewById(R.id.tv_message);
        tvMessage.setText(R.string.private_key);

        final EditText edtValue = view.findViewById(R.id.edt_value);
        edtValue.setText(mNewPrivateKey);

        view.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = edtValue.getText().toString();
                mNewPrivateKey = val;
                dialog.dismiss();
                refreshUI();
            }
        });

        view.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @OnClick(R.id.btn_load_qr_private_key)
    void onBtnLoadQrCodePrivateClicked() {
        mQrMode = QR_MODE_PRIVATE_KEY;
        new IntentIntegrator(this).initiateScan();
    }

    @OnClick(R.id.btn_save)
    void onBtnSaveClicked() {

        if (mNewPrivateKey == null || mNewPrivateKey.isEmpty()) {
            Toast.makeText(this, R.string.eth_private_key_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        mAccountService.putPrivateKey(mNewPrivateKey);
        refreshUI();
    }

    @OnClick(R.id.btn_generate)
    void onBtnGenerateClicked() {
        mNewPrivateKey = ContractHelper.generatePrivateKey();
        refreshUI();
    }

    @OnClick(R.id.btn_discard)
    void onBtnDiscardClicked() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        operateQrCodeResult(requestCode, resultCode, data);
    }

    private void operateQrCodeResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (result.getContents() != null) {
            if (mQrMode.equals(QR_MODE_PRIVATE_KEY)) {
                String privateKey = result.getContents();

                try {
                    Credentials.create(privateKey);
                    mNewPrivateKey = privateKey;
                } catch (Exception ex) {
                    Toast.makeText(this, privateKey + " - is not valid private key", Toast.LENGTH_SHORT).show();
                }
            }

            refreshUI();
        }
    }
}
