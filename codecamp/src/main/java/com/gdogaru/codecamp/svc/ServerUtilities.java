package com.gdogaru.codecamp.svc;

import java.util.Random;

/**
 */
public class ServerUtilities {

    public static final String ZUMO_APP_ID = "rvFDWxpvbJyobzmGuNFmUghdIxSqwQ70";
    public static final String ZUMO_HEADER = "X-ZUMO-APPLICATION";
    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    static final String SERVER_URL = "https://codecampevents.azure-mobile.net/tables/Devices";
    /**
     * Google API project id registered to use GCM.
     */
    static final String SENDER_ID = "771082214216";
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final String PREFS_NAME = "GCM_PREFS";
    private static final String SERVER_ID = "SERVER_ID";

}
