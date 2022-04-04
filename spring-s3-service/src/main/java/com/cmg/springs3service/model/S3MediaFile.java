package com.cmg.springs3service.model;

import com.cmg.springs3service.S3ServiceException;
import lombok.Data;

@Data
public class S3MediaFile implements Comparable<S3MediaFile>{

    private static final int MIN_LENGTH = 5;
    private String path;
    private String thumbnailPath;
    private String sizeInMb;

    public void setPath(String path) {
        if (path == null || path.length() < MIN_LENGTH) {
            throw new S3ServiceException("Invalid Path: " + path);
        }
        this.path = path;
        int index = path.lastIndexOf("/");
        this.thumbnailPath = path.substring(0, index +1) + "thumbnails/tn_" + path.substring(index +1);
    }

    @Override
    public int compareTo(S3MediaFile o) {
        return this.path.compareTo(o.path);
    }
}
