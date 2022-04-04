package com.cmg.springs3service.util;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ListObjectsV2RequestBuilder {

    @Value("${aws.maxKeys}")
    private Integer maxKeys;

    public ListObjectsV2Request buildRequest(String bucketName, String prefix, String delimiter, String continuationToken) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withMaxKeys(maxKeys)
            .withDelimiter(delimiter)
            .withPrefix(prefix);
        if (StringUtil.isNotNull(continuationToken)) {
            request.setContinuationToken(continuationToken);
        }
        return request;
    }
}
