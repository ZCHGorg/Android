package io.charg.chargstation.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import io.charg.chargstation.R;
import io.charg.chargstation.services.AccountService;
import io.charg.chargstation.services.DialogHelper;

/**
 * Created by worker on 23.11.2017.
 */

public abstract class BaseAuthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkWallet();
    }

    protected boolean checkWallet() {
        AccountService accountService = new AccountService(this);

        String ethAddress = accountService.getEthAddress();
        String privateKey = accountService.getPrivateKey();
        if (ethAddress == null || ethAddress.isEmpty() || privateKey == null || privateKey.isEmpty()) {
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
