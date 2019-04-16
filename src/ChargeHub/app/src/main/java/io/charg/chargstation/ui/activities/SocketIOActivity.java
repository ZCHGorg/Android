package io.charg.chargstation.ui.activities;

import android.util.Log;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;

public class SocketIOActivity extends BaseActivity {

    private Socket mSocket;

    @BindView(R.id.tv_status)
    TextView mTvStatus;

    @Override
    public int getResourceId() {
        return R.layout.activity_socketio;
    }

    @Override
    public void onActivate() {
        try {
            mSocket = IO.socket("https://dhanyainnovation.com:3002");
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(CommonData.TAG, "Connected. " + Arrays.toString(args));
                }
            });
            /*mSocket.on("getBitcoinAddress", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(CommonData.TAG, "getFees. " + Arrays.toString(args));
                }
            });*/
            mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i(CommonData.TAG, "Disconnected. " + Arrays.toString(args));
                }
            });
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        refresh();
    }

    @OnClick(R.id.btn_connect)
    void onBtnConnectClicked() {

        if (!mSocket.connected()) {
            mSocket.connect();
        }

        mSocket.emit("getBraintreeToken", null, new Ack() {
            @Override
            public void call(Object... args) {
                Log.i(CommonData.TAG, "result. " + Arrays.toString(args));
            }
        });

        refresh();

    }

    @OnClick(R.id.btn_disconnect)
    void onBtnDisconnectClicked() {
        mSocket.disconnect();
        refresh();
    }

    void refresh() {
        if (mSocket != null) {
            mTvStatus.setText(String.valueOf(mSocket.connected()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off();
    }
}
