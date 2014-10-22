package com.gdogaru.codecamp.gcm;

public class RegisterResponse {
    private int ClientPlatform = 2;
    private String DeviceToken;
    private Integer id;

    public int getClientPlatform() {
        return ClientPlatform;
    }

    public void setClientPlatform(int clientPlatform) {
        ClientPlatform = clientPlatform;
    }

    public String getDeviceToken() {
        return DeviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        DeviceToken = deviceToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
