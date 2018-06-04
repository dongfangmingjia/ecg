package com.algorithm4.library.algorithm4library;

public class Algorithm4Library {
    static {
        System.loadLibrary("algorithm4library");
    }

    public native static boolean InitSingleInstance();

    public native static int addData(byte data[][], int nProtocolLen, int nArrayLen);

    public native static void SetTargetSampling(int SampleRate[]);

    public native static void getValue(double Data[]);

    public native static void getSampledData(double SampledData[][]);
}  