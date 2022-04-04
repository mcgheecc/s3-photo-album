package com.cmg.springs3service.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class S3MediaResponse {

    private final String continuationToken;
    private final List<S3MediaFile> fileList;
}
