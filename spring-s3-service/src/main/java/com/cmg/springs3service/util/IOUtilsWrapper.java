package com.cmg.springs3service.util;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class IOUtilsWrapper {

    public byte[] toByteArray(S3ObjectInputStream objectInputStream) throws IOException {
        return IOUtils.toByteArray(objectInputStream);
    }
}
