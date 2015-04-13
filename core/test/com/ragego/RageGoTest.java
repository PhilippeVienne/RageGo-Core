package com.ragego;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Define a set of functions for unit testing.
 */
public class RageGoTest {
    public static File getResourceAsFile(URL url, String filename, String extension) throws IOException {
        InputStream inputStream = url.openStream();
        File tempFile = File.createTempFile(filename, extension);
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        while (inputStream.available() != 0) {
            outputStream.write(inputStream.read());
        }
        inputStream.close();
        outputStream.close();
        return tempFile;
    }

    public static File writeTempFile(String filename, String extension, String data) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(filename, extension);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            try {
                outputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }
}
