package com.cmg.springs3service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.cmg.springs3service.S3ServiceException;
import com.cmg.springs3service.mapping.S3FolderMapper;
import com.cmg.springs3service.mapping.S3MediaFileMapper;
import com.cmg.springs3service.model.S3Folder;
import com.cmg.springs3service.model.S3MediaFile;
import com.cmg.springs3service.model.S3MediaResponse;
import com.cmg.springs3service.util.IOUtilsWrapper;
import com.cmg.springs3service.util.ListObjectsV2RequestBuilder;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class S3Service implements IS3Service {

    private final AmazonS3 s3;

    private final S3MediaFileMapper s3MediaFileMapper;

    private final S3FolderMapper s3FolderMapper;

    private final ListObjectsV2RequestBuilder requestBuilder;

    private final IOUtilsWrapper ioUtils;

    @Autowired
    public S3Service(AmazonS3 s3, S3MediaFileMapper s3MediaFileMapper, S3FolderMapper s3FolderMapper,
        ListObjectsV2RequestBuilder requestBuilder, IOUtilsWrapper ioUtils) {
        this.s3 = s3;
        this.s3MediaFileMapper = s3MediaFileMapper;
        this.s3FolderMapper = s3FolderMapper;
        this.requestBuilder = requestBuilder;
        this.ioUtils = ioUtils;
    }

    @Override
    public List<S3Folder> getRootAlbums(String bucketName) {
        ListObjectsV2Request request = requestBuilder.buildRequest(bucketName, "", "/", null);

        ListObjectsV2Result result = s3.listObjectsV2(request);
        return result.getCommonPrefixes().stream()
            .map(s3FolderMapper::map)
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public S3MediaResponse getMedia(String bucketName, String prefix, String continuationToken) {
        ListObjectsV2Request request = requestBuilder.buildRequest(bucketName, prefix + "/", null, continuationToken);

        ListObjectsV2Result result = s3.listObjectsV2(request);
        List<S3MediaFile> mediaFileList = result.getObjectSummaries().stream()
            .filter(this::isNotThumbnail)
            .map(s3MediaFileMapper::map)
            .sorted()
            .collect(Collectors.toList());

        return new S3MediaResponse(result.getNextContinuationToken(), mediaFileList);
    }

    @Override
    public byte[] downloadFile(String bucketName, String key) {
        try {
            S3Object object = s3.getObject(bucketName, key);
            return ioUtils.toByteArray(object.getObjectContent());
        } catch (IOException | AmazonS3Exception ae) {
            String message = String.format("Error downloading file: %1$s from bucket: %2$s", key, bucketName);
            throw new S3ServiceException(message, ae);
        }
    }

    @Override
    public Boolean copyFileToFavorites(String sourceBucketName, String destinationBucketName, String key) {
        CopyObjectRequest request = new CopyObjectRequest()
            .withSourceBucketName(sourceBucketName)
            .withSourceKey(key)
            .withDestinationBucketName(destinationBucketName)
            .withDestinationKey(key);
        try {
            s3.copyObject(request);
        } catch (AmazonS3Exception ae) {
            String message = String.format("Error copying file: %1$s from bucket: %2$s to %3$s",
                key, sourceBucketName, destinationBucketName);
            throw new S3ServiceException(message, ae);
        }
        return true;
    }

    @Override
    public Boolean delete(String bucketName, String key) {
        try {
            s3.deleteObject(bucketName, key);
        } catch (AmazonS3Exception ae) {
            String message = String.format("Error deleting file: %1$s from bucket: %2$s", key, bucketName);
            throw new S3ServiceException(message, ae);
        }
        return true;
    }

    boolean isNotThumbnail(S3ObjectSummary objectSummary) {
        return !objectSummary.getKey().contains("thumbnails");
    }

}
