package com.cmg.springs3service.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.cmg.springs3service.model.S3Folder;
import com.cmg.springs3service.model.S3MediaFile;
import com.cmg.springs3service.model.S3MediaResponse;
import com.cmg.springs3service.service.S3Service;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class S3ControllerTest {

    private final static String BUCKET_NAME = "c-mcghee-photos";
    private final static String KEY = "2010/05/12/12_05_2010_07_43-001.jpg";
    private final static String CONTINUATION_TOKEN = "2312414414";
    private final static String[] ALBUM_ARRAY = {"2008","2010","2011","2012","2013",
        "2014","2016","2017","2018","2019","2020"};
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private S3Service service;

    @Test
    public void getRootAlbums() throws Exception {
        List<S3Folder> s3FoldersList = getS3FolderList();
        when(service.getRootAlbums(BUCKET_NAME)).thenReturn(s3FoldersList);

        this.mockMvc.perform(get("/albums/" + BUCKET_NAME))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name",is(ALBUM_ARRAY[0])));
    }

    private List<S3Folder> getS3FolderList() {
        return Arrays.stream(S3ControllerTest.ALBUM_ARRAY)
            .map(S3Folder::new)
            .collect(Collectors.toList());
    }

    @Test
    public void getMedia() throws Exception {
        S3MediaResponse response = getS3MediaResponse();
        when(service.getMedia(anyString(), anyString(), anyString())).thenReturn(response);
        this.mockMvc.perform(get("/media/all")
                .param("bucketName", BUCKET_NAME)
                .param("prefix", "/")
                .param("continuationToken", CONTINUATION_TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fileList[0].path",is(KEY)));
    }

    @Test
    public void download() throws Exception {
        String testStr = "test12345";
        when(service.downloadFile(BUCKET_NAME, KEY)).thenReturn(testStr.getBytes());
        this.mockMvc.perform(get("/media")
                .param("bucketName", BUCKET_NAME)
                .param("key", KEY ))
            .andExpect(status().isOk())
            .andExpect(content().string(testStr));
    }

    @Test
    public void addToFavorites() throws Exception {
        when(service.copyFileToFavorites(anyString(), anyString(),anyString())).thenReturn(true);
        this.mockMvc.perform(put("/favourite")
                .param("sourceBucketName", BUCKET_NAME)
                .param("targetBucketName", BUCKET_NAME + "2")
                .param("key", KEY))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    public void deleteObject() throws Exception {
        when(service.delete(BUCKET_NAME, KEY)).thenReturn(true);

        this.mockMvc.perform(delete("/media")
                .param("bucketName", BUCKET_NAME)
                .param("key", KEY ))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    private S3MediaResponse getS3MediaResponse() {
        S3MediaFile s3MediaFile = new S3MediaFile();
        s3MediaFile.setPath(KEY);
        s3MediaFile.setSizeInMb("1 MB");
        List<S3MediaFile> fileList = Collections.singletonList(s3MediaFile);
        return new S3MediaResponse(CONTINUATION_TOKEN, fileList);
    }

}
