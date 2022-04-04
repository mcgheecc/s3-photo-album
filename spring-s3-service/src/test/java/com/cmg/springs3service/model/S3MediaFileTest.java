package com.cmg.springs3service.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.cmg.springs3service.S3ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class S3MediaFileTest {

    private final String PATH = "2020/01/07/test.jpg";

    @Test
    void setPath_expectThumbnailSet() {
        S3MediaFile mediaFile = new S3MediaFile();
        mediaFile.setPath(PATH);

        String thumbnailPath = "2020/01/07/thumbnails/tn_test.jpg";
        assertThat(mediaFile.getThumbnailPath()).isEqualTo(thumbnailPath);
    }

    @Test
    void setPath_givenNull_expectException() {
        S3ServiceException thrown = Assertions.assertThrows(S3ServiceException.class, () -> {
            S3MediaFile mediaFile = new S3MediaFile();
            mediaFile.setPath(null);
        });
        assertThat(thrown).hasMessageContaining("Invalid");
    }

    @Test
    void setPath_givenTooShort_expectException() {
        S3ServiceException thrown = Assertions.assertThrows(S3ServiceException.class, () -> {
            S3MediaFile mediaFile = new S3MediaFile();
            mediaFile.setPath("inva");
        });
        assertThat(thrown).hasMessageContaining("Invalid");
    }

    @Test
    void compareTo_expectEqual() {
        S3MediaFile mediaFileA = new S3MediaFile();
        mediaFileA.setPath(PATH);
        S3MediaFile mediaFileB = new S3MediaFile();
        mediaFileB.setPath(PATH);

        assertThat(mediaFileA.compareTo(mediaFileB)).isZero();
    }

    @Test
    void compareTo_expectLessThan() {
        S3MediaFile mediaFileA = new S3MediaFile();
        mediaFileA.setPath(PATH);
        S3MediaFile mediaFileB = new S3MediaFile();
        mediaFileB.setPath(PATH + "B");

        assertThat(mediaFileA.compareTo(mediaFileB)).isEqualTo(-1);
    }

    @Test
    void compareTo_expectMoreThan() {
        S3MediaFile mediaFileA = new S3MediaFile();
        mediaFileA.setPath(PATH + "B");
        S3MediaFile mediaFileB = new S3MediaFile();
        mediaFileB.setPath(PATH );

        assertThat(mediaFileA.compareTo(mediaFileB)).isEqualTo(1);
    }
}
