package com.cmg.springs3service.mapping;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.cmg.springs3service.model.S3MediaFile;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class S3MediaFileMapper {

    public S3MediaFile map(S3ObjectSummary objectSummary) {
        S3MediaFile mediaFile = new S3MediaFile();
        mediaFile.setPath(objectSummary.getKey());
        mediaFile.setSizeInMb(FileUtils.byteCountToDisplaySize(objectSummary.getSize()));
        return mediaFile;
    }

}
