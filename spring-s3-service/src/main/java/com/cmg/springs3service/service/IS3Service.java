package com.cmg.springs3service.service;

import com.cmg.springs3service.model.S3Folder;
import com.cmg.springs3service.model.S3MediaResponse;
import java.util.List;

public interface IS3Service {

    List<S3Folder> getRootAlbums(String bucketName);

    S3MediaResponse getMedia(String bucketName, String prefix, String continuationToken);

    byte[] downloadFile(String bucketName, String key);

    Boolean copyFileToFavorites(String sourceBucketName, String destinationBucketName, String key);

    Boolean delete(String bucketName, String key);
}
