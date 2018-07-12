package io.charg.chargstation.ui.fragments;

public abstract class BaseNavFragment extends BaseFragment {

    public abstract boolean canBack();

    public abstract boolean canNext();

    public abstract boolean isValid();

    public abstract void invalidate();
}
