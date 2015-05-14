package com.ragego.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Check if we are connected to the Internet.
 */
public class InternetCheckThread extends Thread {

    // Times are in Milliseconds
    protected static final int WAIT_TIME_BEFORE_RECHECK_IF_ARE_ALREADY_CONNECTED = 60000;
    protected static final int WAIT_TIME_BEFORE_RECHECK_IF_NOT_CONNECTED = 500;
    protected static final String URL_TO_CHECK = "http://www.google.com";
    protected static final int TIMEOUT = 2000;
    protected static final String THREAD_DEFAULT_NAME = "InternetCheckThread";
    private boolean connected;

    public InternetCheckThread() {
        super(THREAD_DEFAULT_NAME);
    }

    public void run() {
        while (this.isAlive()) {
            try {
                connected = isInternetReachable();
                Thread.sleep(
                        connected ?
                                WAIT_TIME_BEFORE_RECHECK_IF_ARE_ALREADY_CONNECTED :
                                WAIT_TIME_BEFORE_RECHECK_IF_NOT_CONNECTED);
            } catch (InterruptedException e) {
                this.start();
            }
        }
    }

    public boolean isInternetReachable() {
        try {
            //make a URL to a known source
            URL url = new URL(URL_TO_CHECK);

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.setConnectTimeout(TIMEOUT);

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            int read = urlConnect.getInputStream().read();
            return read != -1;
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
