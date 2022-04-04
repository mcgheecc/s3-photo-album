package com.cmg.springs3service.mapping;

import com.cmg.springs3service.model.S3Folder;
import org.springframework.stereotype.Component;

@Component
public class S3FolderMapper {

    public S3Folder map(String folderName) {
        return new S3Folder(folderName.replace("/", ""));
    }

}
