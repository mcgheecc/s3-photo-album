package com.cmg.springs3service.mapping;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.cmg.springs3service.model.S3MediaFile;
import org.junit.jupiter.api.Test;

class S3MediaFileMapperTest {

    private final S3MediaFileMapper mapper = new S3MediaFileMapper();
    private final static String TEST_KEY = "/2020/02/08/TEST.JPG";
    private final static long TEST_SIZE = 2560000L;
    private final static String EXPECTED_SIZE_IN_MB = "2 MB";

    @Test
    void map() {
        S3ObjectSummary objectSummary = new S3ObjectSummary();
        objectSummary.setKey(TEST_KEY);
        objectSummary.setSize(TEST_SIZE);
        S3MediaFile mediaFile = mapper.map(objectSummary);

        assertThat(mediaFile).isNotNull();
        assertThat(mediaFile.getPath()).isEqualTo(TEST_KEY);
        assertThat(mediaFile.getSizeInMb()).isEqualTo(EXPECTED_SIZE_IN_MB);
    }
}
