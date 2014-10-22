package com.gdogaru.codecamp.gcm;

public class RegisterOperation {

    private int ClientPlatform = 2;
    private String DeviceToken;

    public RegisterOperation() {
    }

    public RegisterOperation(String deviceToken) {
        DeviceToken = deviceToken;
    }

    public void setClientPlatform(int clientPlatform) {
        ClientPlatform = clientPlatform;
    }

    public void setDeviceToken(String deviceToken) {
        DeviceToken = deviceToken;
    }

    public int getClientPlatform() {
        return ClientPlatform;
    }

    public String getDeviceToken() {
        return DeviceToken;
    }
}
