package com.cmg.springs3service.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import org.junit.jupiter.api.Test;

class ListObjectsV2RequestBuilderTest {

    private static final String BUCKET_NAME = "bucket1";
    private static final String PREFIX = "/2010";
    private static final String DELIMITER = "/";

    private final ListObjectsV2RequestBuilder builder = new ListObjectsV2RequestBuilder();

    @Test
    void buildRequestWithNoContinuationToken() {
        ListObjectsV2Request request = builder.buildRequest(BUCKET_NAME, PREFIX, DELIMITER, null);

        assertThat(request.getBucketName()).isEqualTo(BUCKET_NAME);
        assertThat(request.getPrefix()).isEqualTo(PREFIX);
        assertThat(request.getDelimiter()).isEqualTo(DELIMITER);
        assertThat(request.getContinuationToken()).isNull();
    }

    @Test
    void buildRequestWithContinuationToken() {
        String token = "34223r2f2333";
        ListObjectsV2Request request = builder.buildRequest(BUCKET_NAME, PREFIX, DELIMITER, token);

        assertThat(request.getBucketName()).isEqualTo(BUCKET_NAME);
        assertThat(request.getPrefix()).isEqualTo(PREFIX);
        assertThat(request.getDelimiter()).isEqualTo(DELIMITER);
        assertThat(request.getContinuationToken()).isEqualTo(token);
    }
}
