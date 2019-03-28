package io.charg.chargstation.root;

import android.app.Application;

import io.charg.chargstation.services.remote.api.socketio.SocketIOProvider;

public class ChargeApp extends Application {

    private SocketIOProvider mSocketIOProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        initSocketIO();
    }

    private void initSocketIO() {
        mSocketIOProvider = SocketIOProvider.getInstance();
        mSocketIOProvider.init();
        mSocketIOProvider.connect();
    }

}
