package com.ljfth.ecgviewlib.model;

public class DevicesModel {

    private String macStr;
    private String signalStr;
    private String deviceType;
    private String connectStr;
    private byte[] devicesData;
    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public byte[] getDevicesData() {
        return devicesData;
    }

    public void setDevicesData(byte[] devicesData) {
        this.devicesData = devicesData;
    }

    public String getMacStr() {
        return macStr;
    }

    public void setMacStr(String macStr) {
        this.macStr = macStr;
    }

    public String getSignalStr() {
        return signalStr;
    }

    public void setSignalStr(String signalStr) {
        this.signalStr = signalStr;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getConnectStr() {
        return connectStr;
    }

    public void setConnectStr(String connectStr) {
        this.connectStr = connectStr;
    }
}
