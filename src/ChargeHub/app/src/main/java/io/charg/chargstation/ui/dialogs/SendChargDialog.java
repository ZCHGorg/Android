package io.charg.chargstation.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.Locale;

import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.remote.contract.ChargCoinContract;
import io.charg.chargstation.services.local.SettingsProvider;

/**
 * Created by worker on 30.11.2017.
 */

public class SendChargDialog {

    private Context mContext;
    private AccountService mAccountService;
    private SettingsProvider mSettingsProvider;

    public SendChargDialog(Context context) {
        mContext = context;
        mAccountService = new AccountService(context);
        mSettingsProvider = new SettingsProvider(context);
    }

    public void sendCharg(String address, double amount) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dlg_sendcharg, null);

        final Dialog dlg = new AlertDialog.Builder(mContext)
                .setView(view)
                .setCancelable(false)
                .create();

        final EditText tvAmount = view.findViewById(R.id.tv_amount);
        final EditText tvAddressTo = view.findViewById(R.id.tv_address_to);
        final ProgressBar progressBar = view.findViewById(R.id.progress);
        final ViewGroup ltResult = view.findViewById(R.id.lt_result);
        Button btnSend = view.findViewById(R.id.btn_send);
        Button btnDismiss = view.findViewById(R.id.btn_discard);
        final TextView tvResult = view.findViewById(R.id.tv_result);
        final Button btnOpen = view.findViewById(R.id.btn_open);

        tvAmount.setText(String.format(Locale.ROOT, "%.6f", amount));
        tvAddressTo.setText(address);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {

            class SendChargAsyncTask extends AsyncTask<Object, Object, String> {

                final Web3j web3 = Web3jFactory.build(new HttpService(mSettingsProvider.getEthConnectionUrl()));

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressBar.setVisibility(View.VISIBLE);
                    ltResult.setVisibility(View.INVISIBLE);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    progressBar.setVisibility(View.INVISIBLE);
                    ltResult.setVisibility(View.VISIBLE);

                    tvResult.setText(s);
                }

                @Override
                protected String doInBackground(Object... objects) {
                    try {
                        ChargCoinContract contract = ChargCoinContract.load(CommonData.SMART_CONTRACT_ADDRESS, web3, Credentials.create(mAccountService.getPrivateKey()), mSettingsProvider.getGasPrice(), mSettingsProvider.getGasLimit());
                        String addressTo = tvAddressTo.getText().toString();
                        BigInteger amount = BigInteger.valueOf((long) (Double.valueOf(tvAmount.getText().toString()) * 1E18));
                        TransactionReceipt transactionReceipt = contract.transfer(addressTo, amount).sendAsync().get();
                        final String hash = transactionReceipt.getTransactionHash();
                        Log.v(CommonData.TAG, "transaction hash is:" + hash);

                        btnOpen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    String url = String.format("https://ethplorer.io/tx/%s", hash);
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    mContext.startActivity(i);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        return String.format("Hash: %s", hash);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return ex.getMessage();
                    }
                }
            }

            @Override
            public void onClick(View view) {
                new SendChargAsyncTask().execute();
            }
        });

        dlg.show();
    }

}
