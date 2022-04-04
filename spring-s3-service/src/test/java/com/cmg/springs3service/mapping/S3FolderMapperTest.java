package com.cmg.springs3service.mapping;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.cmg.springs3service.model.S3Folder;
import org.junit.jupiter.api.Test;

class S3FolderMapperTest {

    private final static String EXPECTED_NAME = "TEST_FOLDER";
    private final S3FolderMapper mapper = new S3FolderMapper();

    @Test
    void map_givenFolderName_expectS3Folder() {
        S3Folder folder = mapper.map(EXPECTED_NAME);

        assertThat(folder).isNotNull();
        assertThat(folder.getName()).isEqualTo(EXPECTED_NAME);
    }

    @Test
    void map_givenFolderNameWithForwardSlash_expectS3FolderNameWithoutForwardSlash() {
        S3Folder folder = mapper.map("/" + EXPECTED_NAME + "/");

        assertThat(folder).isNotNull();
        assertThat(folder.getName()).isEqualTo(EXPECTED_NAME);
    }
}
