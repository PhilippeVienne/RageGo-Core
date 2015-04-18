package com.ragego.utils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Useful methods to read, write and manipulate files.
 */
public class FileUtils {

    /**
     * Buffer size for copying streams.
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Get a resource as a temp files.
     * This function copy a resource in a temp file to use it if it's not directly available in file system.
     * @param clazz The class which own this resource, this could be null.
     * @param resource The resource to find in the classpath
     * @return The {@link File} which represent the file.
     * @throws IOException Generaly means an error occurs to get or copy the resource.
     * @throws FileNotFoundException The resource is not in the classpath
     */
    public static File getResourceAsTMPFile(Class clazz, String resource) throws IOException{
        if(clazz == null){
            clazz = FileUtils.class;
        }
        final URL url = clazz.getResource(resource);
        if(url == null)
            throw new FileNotFoundException("The resource "+resource+(clazz == FileUtils.class?"":" from class "+clazz.getName())+" is not found");
        if(url.getProtocol().equals("file")){
            try {
                return new File(url.toURI());
            } catch (URISyntaxException e) {
                throw new IOException("Can not get the file",e);
            }
        }
        final File tmpFile = File.createTempFile("tmpFile", "");
        copyStreams(clazz.getResourceAsStream(resource),new FileOutputStream(tmpFile));
        return tmpFile;
    }

    /**
     * Copy stream into another.
     * Read fully the input stream to write in output stream
     * @param inputStream The input to read
     * @param outputStream The output to write
     * @throws IOException Error while copying streams. See error for more details.
     */
    public static void copyStreams(InputStream inputStream, OutputStream outputStream) throws IOException{
        byte[] buffer = new byte[FileUtils.BUFFER_SIZE];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
    }

}
