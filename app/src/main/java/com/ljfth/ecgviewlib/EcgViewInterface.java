package com.ljfth.ecgviewlib;

public interface EcgViewInterface {
    void onError(EcgWaveView view, Exception var1);

    void onShowMessage(EcgWaveView view, String var1, int var2);
}