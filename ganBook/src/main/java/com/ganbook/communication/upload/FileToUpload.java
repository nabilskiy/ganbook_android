package com.ganbook.communication.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.os.Parcelable;

public class FileToUpload { 

    private static final String NEW_LINE = "\r\n";

    private final String filePath;
    private final String fileName;
    private final String paramName;
    private final String contentType;    
    
    public final String origFilePath;
    
//    public final String tmb_file_path;
    
    
    public String getFileName() {
        return fileName;
    }

    public FileToUpload(final String path,
                        final String parameterName,
                        final String fileName,
                        final String contentType,
                        final String origFilePath) {
//                        final String tmb_file_path) {
        this.filePath = path;
        this.paramName = parameterName;
        this.contentType = contentType;
        this.origFilePath = origFilePath;
//        this.tmb_file_path = tmb_file_path;
        
        if (fileName == null || "".equals(fileName)) {
            this.fileName = new File(filePath).getName();
        } else {
            this.fileName = fileName;
        }
    }


    public final InputStream getStream() throws FileNotFoundException {
        return new FileInputStream(filePath);
    }

    public byte[] getMultipartHeader() throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();        	
        builder.append("Content-Disposition: form-data; name=\"")
               .append(paramName)
               .append("\"; filename=\"")
               .append(fileName)
               .append("\"")
               .append(NEW_LINE);
		
        if (contentType != null) {
            builder.append("Content-Type: ")
                   .append(contentType)
                   .append(NEW_LINE);
        }
		
        builder.append(NEW_LINE);
        
        return builder.toString().getBytes("UTF-8");
    }

    public long length() {
        return getFile().length();
    }

    public File getFile() {
        return new File(filePath);
    }    
}
