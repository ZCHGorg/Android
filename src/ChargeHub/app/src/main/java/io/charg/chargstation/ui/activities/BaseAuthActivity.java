package io.charg.chargstation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import io.charg.chargstation.R;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.helpers.DialogHelper;

/**
 * Created by worker on 23.11.2017.
 */

public abstract class BaseAuthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkWalletWithDialog();
    }

    protected boolean checkWalletWithDialog() {
        AccountService accountService = new AccountService(this);

        String ethAddress = accountService.getEthAddress();
        String privateKey = accountService.getPrivateKey();
        if (TextUtils.isEmpty(ethAddress) || TextUtils.isEmpty(privateKey)) {
            DialogHelper.showQuestion(this, getString(R.string.ask_login), new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(BaseAuthActivity.this, ChangeWalletActivity.class));
                }
            });
            return false;
        } else {
            return true;
        }
    }
}
