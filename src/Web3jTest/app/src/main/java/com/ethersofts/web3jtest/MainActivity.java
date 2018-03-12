package com.ethersofts.web3jtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Web3j web3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/Zp6evGImttk7WOe95WcW "));
                try {
                    final BigInteger balance = web3j.ethGetBalance("0x238D80385a7207Dd4f1BD53Ef59a9A7aCbf77246", DefaultBlockParameterName.LATEST).send().getBalance();
                    final String str = Convert.fromWei(balance.toString(), Convert.Unit.ETHER).toPlainString();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.v(getLocalClassName(), str);

                    final TransactionReceipt response = Transfer.sendFunds(web3j, Credentials.create("21bb1af0413f3730203b65a2fbd3a3fae5f486178181d99031c4bc254f3c818f"), "0x129D45d98Aec64A00d1dac99519cd110b2f052d2", BigDecimal.valueOf(0.01), Convert.Unit.ETHER).send();

                    final NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    Log.v(getLocalClassName(), response.getTransactionHash());

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notificationManager.notify(0, new Notification.Builder(getApplicationContext())
                                    .setContentText(response.getTransactionHash())
                                    .setContentTitle("Transaction executed")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .build());
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
