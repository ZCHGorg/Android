package io.charg.chargstation.root;

/**
 * Created by worker on 02.11.2017.
 */

public interface IAsyncCommand<I, O> {

    I getInputData();

    void onPrepare();

    void onComplete(O result);

    void onError(String error);

}
