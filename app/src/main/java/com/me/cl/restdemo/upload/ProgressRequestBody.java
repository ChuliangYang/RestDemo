package com.me.cl.restdemo.upload;

import android.content.Context;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by CL on 8/1/18.
 */
public class ProgressRequestBody extends RequestBody {

    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int UPDATE_PERCENT_THRESHOLD = 1;

    private File file;
    private ProgressListener listener;
    private MediaType mediaType;

    public interface ProgressListener {
        void onUploadProgress(int progressInPercent, long uploadedBytes,long totalBytes);
    }

    public ProgressRequestBody(Context context, String path, ProgressListener listener) {
        this.file = new File(path);
        this.listener = listener;
        String type = getMimeType(file.getAbsolutePath());
        mediaType = MediaType.parse(type);
    }

    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSource source= Okio.buffer(Okio.source(file));
        long totalBytes = file.length();
        try {
            // init variables
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            long uploadedBytes = 0;
            int readBytes;
            int fileUploadedInPercent = 0;
            // go through the file and notify the UI
            while ((readBytes = source.read(buffer)) != -1) {
                // notify UI at max after every 1%
                int newfileUploadedInPercent = (int) ((uploadedBytes * 100) / totalBytes);
                if (fileUploadedInPercent + UPDATE_PERCENT_THRESHOLD <= newfileUploadedInPercent) {
                    fileUploadedInPercent = newfileUploadedInPercent;
                    listener.onUploadProgress(newfileUploadedInPercent,uploadedBytes,totalBytes);
                }
                uploadedBytes += readBytes;
                sink.write(buffer,0, readBytes);
            }
        }finally {
            source.close();
        }

        listener.onUploadProgress(100,totalBytes, totalBytes);
    }


    public String getMimeType(String url) {
        String type=null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}