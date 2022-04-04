package com.cmg.springs3service.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.cmg.springs3service.S3ServiceException;
import com.cmg.springs3service.mapping.S3FolderMapper;
import com.cmg.springs3service.mapping.S3MediaFileMapper;
import com.cmg.springs3service.model.S3Folder;
import com.cmg.springs3service.model.S3MediaFile;
import com.cmg.springs3service.model.S3MediaResponse;
import com.cmg.springs3service.util.IOUtilsWrapper;
import com.cmg.springs3service.util.ListObjectsV2RequestBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private S3FolderMapper folderMapper;
    @Mock
    private AmazonS3 s3;

    @Mock
    private ListObjectsV2Result result;

    @Mock
    private ListObjectsV2RequestBuilder requestBuilder;

    @Mock
    private S3MediaFileMapper mediaFileMapper;

    @Mock
    private S3Object s3Object;

    @Mock
    private HttpRequestBase httpRequestBase;

    @Mock
    private IOUtilsWrapper ioUtilsWrapper;

    @InjectMocks
    private S3Service s3Service;

    private static final String BUCKET_NAME = "mrs_bucket";
    private static final String KEY = "/2008/01/01/image.jpg";
    private static final String FILE_SIZE_MB = "1 MB";
    private static final String TEST_STRING = "12345 TEST";

    @Test
    public void getRootAlbums() {
        when(requestBuilder.buildRequest(anyString(), anyString(), anyString(),isNull())).thenCallRealMethod();
        when(folderMapper.map(anyString())).thenCallRealMethod();
        List<String> input = List.of("2005/", "2006/", "2007/");
        when(result.getCommonPrefixes()).thenReturn(input);
        when(s3.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(result);

        List<S3Folder> expected = input.stream().map(folderMapper::map).collect(Collectors.toList());

        List<S3Folder> actual = s3Service.getRootAlbums(BUCKET_NAME);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getMedia() {
        when(requestBuilder.buildRequest(anyString(), anyString(), isNull(),isNull())).thenCallRealMethod();
        when(s3.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(result);
        when(result.getObjectSummaries()).thenReturn(getObjectSummaryList());
        when(mediaFileMapper.map(any(S3ObjectSummary.class))).thenCallRealMethod();
        S3MediaResponse actual = s3Service.getMedia(BUCKET_NAME, "/", null);

        assertThat(actual).isNotNull();
        assertThat(actual.getFileList()).isNotNull();
        S3MediaFile mediaFile = actual.getFileList().get(0);
        assertThat(mediaFile.getPath()).isEqualTo(KEY);
        assertThat(mediaFile.getSizeInMb()).isEqualTo(FILE_SIZE_MB);
    }

    @Test
    public void downloadFile_expectSuccess() throws Exception {
        when(s3.getObject(BUCKET_NAME, KEY)).thenReturn(s3Object);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(TEST_STRING.getBytes());
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(byteArrayInputStream, httpRequestBase);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(ioUtilsWrapper.toByteArray(any(S3ObjectInputStream.class))).thenCallRealMethod();

        byte[] actual = s3Service.downloadFile(BUCKET_NAME, KEY);
        assertThat(actual.length).isPositive();
        String actualStr = new String(actual);
        assertThat(actualStr).isEqualTo(TEST_STRING);
    }

    @Test
    public void downloadFile_expectS3Exception() {
        S3ServiceException thrown = Assertions.assertThrows(S3ServiceException.class, () -> {
            when(s3.getObject(BUCKET_NAME, KEY)).thenThrow(new AmazonS3Exception("test"));
            s3Service.downloadFile(BUCKET_NAME, KEY);
        });

        assertThat(thrown).hasMessageContaining("Error downloading file");
    }

    @Test
    public void downloadFile_expectIOException() {
        S3ServiceException thrown = Assertions.assertThrows(S3ServiceException.class, () -> {
            when(s3.getObject(BUCKET_NAME, KEY)).thenReturn(s3Object);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(TEST_STRING.getBytes());
            S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(byteArrayInputStream, httpRequestBase);
            when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
            when(ioUtilsWrapper.toByteArray(s3ObjectInputStream)).thenThrow(new IOException());
            s3Service.downloadFile(BUCKET_NAME, KEY);
        });

        assertThat(thrown).hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void copyFileToFavorites_expectTrue() {
        Boolean actual = s3Service.copyFileToFavorites(BUCKET_NAME, BUCKET_NAME + "2", KEY);
        assertThat(actual).isTrue();
        verify(s3).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    public void copyFileToFavorites_expectException() {
        S3ServiceException thrown = Assertions.assertThrows(S3ServiceException.class, () -> {
            when(s3.copyObject(any(CopyObjectRequest.class))).thenThrow(new AmazonS3Exception("test"));
            s3Service.copyFileToFavorites(BUCKET_NAME, BUCKET_NAME + "2", KEY);
        });
        assertThat(thrown).hasMessageContaining("Error copying file");
    }

    @Test
    public void delete_expectTrue() {
        Boolean actual = s3Service.delete(BUCKET_NAME, KEY);
        assertThat(actual).isTrue();
        verify(s3).deleteObject(BUCKET_NAME, KEY);
    }

    @Test
    public void delete_expectException() {
        S3ServiceException thrown = Assertions.assertThrows(S3ServiceException.class, () -> {
            doThrow(new AmazonS3Exception("test")).when(s3).deleteObject(BUCKET_NAME, KEY);
            s3Service.delete(BUCKET_NAME, KEY);
        });
        assertThat(thrown).hasMessageContaining("Error deleting file");
    }

    @Test
    void isNotThumbnail_expectTrue() {
        S3ObjectSummary objectSummary = new S3ObjectSummary();
        objectSummary.setKey("/123/test/test.jpg");

        boolean actual = s3Service.isNotThumbnail(objectSummary);
        assertThat(actual).isTrue();
    }

    @Test
    void isNotThumbnail_expectFalse() {
        S3ObjectSummary objectSummary = new S3ObjectSummary();
        objectSummary.setKey("/123/test/thumbnails/tn_test.jpg");

        boolean actual = s3Service.isNotThumbnail(objectSummary);
        assertThat(actual).isFalse();
    }

    private List<S3ObjectSummary> getObjectSummaryList() {
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setKey(KEY);
        summary.setSize(1200000);
        return Collections.singletonList(summary);
    }
}
