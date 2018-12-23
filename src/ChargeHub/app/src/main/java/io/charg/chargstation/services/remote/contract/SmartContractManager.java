package io.charg.chargstation.services.remote.contract;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple6;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.services.local.AccountService;
import io.charg.chargstation.services.local.SettingsProvider;

/**
 * Created by worker on 30.11.2017.
 */

public class SmartContractManager {

    private final Context mContext;

    private final Web3j mWeb3;

    private final AccountService mAccountService;

    private final ChargCoinContract mSmContract;

    private final SettingsProvider mSettingsProvider;

    private SmartContractManager(Context context) {
        mContext = context;
        mAccountService = new AccountService(context);
        mSettingsProvider = new SettingsProvider(context);
        mWeb3 = Web3jFactory.build(new HttpService(mSettingsProvider.getContractAddress()));
        Credentials credentials = Credentials.create(mAccountService.getPrivateKey());
        mSmContract = ChargCoinContract.load(CommonData.SMART_CONTRACT_ADDRESS, mWeb3, credentials, mSettingsProvider.getGasPrice(), mSettingsProvider.getGasLimit());
    }

    public static SmartContractManager getInstance(Context context) {
        try {
            return new SmartContractManager(context);
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public boolean isNowParking() {
        try {
            Tuple6 res = mSmContract.parkingSwitches(mAccountService.getEthAddress()).sendAsync().get();
            String nodeAddress = String.valueOf(res.getValue1());
            long startTime = Long.valueOf(res.getValue2().toString());
            long endTime = Long.valueOf(res.getValue3().toString());
            long rate = Long.valueOf(res.getValue4().toString());
            boolean init = Boolean.valueOf(res.getValue5().toString());
            long prefAmount = Long.valueOf(res.getValue6().toString());
            return init;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void parkingOnAsync(final String address, final BigInteger time) {
        new SmartContractAsyncTask(mContext) {
            @Override
            protected String onExecute() throws ExecutionException, InterruptedException {
                return mSmContract.parkingOn(address, time).sendAsync().get().getTransactionHash();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void parkingOffAsync(final String ethAddress) {
        new SmartContractAsyncTask(mContext) {
            @Override
            protected String onExecute() throws ExecutionException, InterruptedException {
                return mSmContract.parkingOff(ethAddress).sendAsync().get().getTransactionHash();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void chargeOnAsync(final String ethAddress, final BigInteger time) {
        new SmartContractAsyncTask(mContext) {
            @Override
            protected String onExecute() throws ExecutionException, InterruptedException {
                return mSmContract.chargeOn(ethAddress, time).sendAsync().get().getTransactionHash();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void chargeOffAsync(final String ethAddress) {
        new SmartContractAsyncTask(mContext) {
            @Override
            protected String onExecute() throws ExecutionException, InterruptedException {
                return mSmContract.chargeOff(ethAddress).sendAsync().get().getTransactionHash();
            }
        }.execute();
    }

    public boolean isNowCharging() {
        try {
            Tuple6 res = mSmContract.chargingSwitches(mAccountService.getEthAddress()).sendAsync().get();
            String nodeAddress = String.valueOf(res.getValue1());
            long startTime = Long.valueOf(res.getValue2().toString());
            long endTime = Long.valueOf(res.getValue3().toString());
            long rate = Long.valueOf(res.getValue4().toString());
            boolean init = Boolean.valueOf(res.getValue5().toString());
            long prefAmount = Long.valueOf(res.getValue6().toString());
            return init;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    abstract class SmartContractAsyncTask extends AsyncTask<Void, String, String> {

        private Context mContext;

        SmartContractAsyncTask(Context context) {
            mContext = context;
        }

        protected abstract String onExecute() throws ExecutionException, InterruptedException;

        @Override
        protected String doInBackground(Void... runnables) {
            try {
                return onExecute();
            } catch (Exception ex) {
                publishProgress(ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(mContext, Arrays.toString(values), Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null || s.isEmpty()) {
                return;
            }

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) {
                return;
            }

            Notification notification = new Notification.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_notify)
                    .setContentTitle(mContext.getString(R.string.execute_tansaction))
                    .setContentText(String.format("TxHash: %s...%s", s.substring(0, 8), s.substring(s.length() - 4)))
                    .setContentIntent(PendingIntent.getActivity(mContext, 0, new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("https://ethplorer.io/tx/%s", s))), 0))
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(0, notification);

        }
    }
}
