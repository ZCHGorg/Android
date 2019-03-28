package io.charg.chargstation.services.remote.api.socketio;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.Arrays;

import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.ICallbackOnComplete;

public class SocketIOProvider {

    private static final String SOCKET_URL = "https://dhanyainnovation.com:3002";
    private static final String METHOD_GET_BRAINTREE_TOKEN = "getBraintreeToken";
    private static final String METHOD_PAY_BRAINTREE = "payBraintree";
    private static final String METHOD_GET_BEST_SELL_ORDER = "getBestSellOrder";

    private Socket mSocket;

    private static SocketIOProvider sInstance;

    private SocketIOProvider() {
        try {
            mSocket = IO.socket(SOCKET_URL);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static SocketIOProvider getInstance() {
        if (sInstance == null) {
            sInstance = new SocketIOProvider();
        }

        return sInstance;
    }

    public void connect() {
        mSocket.connect();
    }

    public void init() {
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(CommonData.TAG, getClass().getSimpleName() + ". Connected. " + Arrays.toString(args));
            }
        });
        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(CommonData.TAG, getClass().getSimpleName() + ". Disconnected. " + Arrays.toString(args));
            }
        });
    }

    public void disconnect() {
        mSocket.off();
        mSocket.disconnect();
    }

    public void getBraintreeTokenAsync(final ICallbackOnComplete<BraintreeTokenDto> onComplete) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        mSocket.emit(METHOD_GET_BRAINTREE_TOKEN, null, new Ack() {
            @Override
            public void call(Object... args) {
                Log.i(CommonData.TAG, Arrays.toString(args));

                BraintreeTokenDto braintreeTokenDto = null;

                try {
                    braintreeTokenDto = new Gson().fromJson(args[0].toString(), BraintreeTokenDto.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (onComplete != null) {
                    onComplete.onComplete(braintreeTokenDto);
                }
            }
        });
    }

    public void payBraintreeAsync(PaymentDataRequestDto paymentData) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        Log.i(CommonData.TAG, new Gson().toJson(paymentData));

        mSocket.emit(METHOD_PAY_BRAINTREE, paymentData, new Ack() {
            @Override
            public void call(Object... args) {
                Log.i(CommonData.TAG, "Pay result: " + Arrays.toString(args));
            }
        });
    }

    public void getBestSellOrderAsync(final ICallbackOnComplete<SellOrderResponseDto> onComplete) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }

        mSocket.emit(METHOD_GET_BEST_SELL_ORDER, new SellOrderRequestDto(1000f), new Ack() {
            @Override
            public void call(Object... args) {
                Log.i(CommonData.TAG, Arrays.toString(args));

                SellOrderResponseDto sellOrder = null;

                try {
                    sellOrder = new Gson().fromJson(args[0].toString(), SellOrderResponseDto.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (onComplete != null) {
                    onComplete.onComplete(sellOrder);
                }

            }
        });
    }

}
