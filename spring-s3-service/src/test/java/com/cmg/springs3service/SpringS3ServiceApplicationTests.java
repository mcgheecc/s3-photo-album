package com.cmg.springs3service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.cmg.springs3service.web.S3Controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringS3ServiceApplicationTests {

    @Autowired
    private S3Controller s3Controller;

    @Test
    void contextLoads() {
        assertThat(s3Controller).isNotNull();
    }

}
