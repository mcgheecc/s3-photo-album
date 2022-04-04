package com.cmg.springs3service.web;

import com.cmg.springs3service.model.S3Folder;
import com.cmg.springs3service.model.S3MediaResponse;
import com.cmg.springs3service.service.S3Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "${cors.sites}")
public class S3Controller {

    private final S3Service service;

    @Autowired
    public S3Controller(S3Service service) {
        this.service = service;
    }

    @GetMapping("/albums/{bucketName}")
    public List<S3Folder> getRootAlbums(@PathVariable String bucketName) {
        return service.getRootAlbums(bucketName);
    }

    @GetMapping("/media/all")
    public S3MediaResponse getMedia(@RequestParam String bucketName, @RequestParam String prefix, @RequestParam String continuationToken) {
        return service.getMedia(bucketName, prefix, continuationToken);
    }

    @GetMapping("/media")
    public byte[] download(@RequestParam String bucketName, @RequestParam String key) {
        return service.downloadFile(bucketName, key);
    }

    @PutMapping("/favourite")
    public Boolean addToFavorites(@RequestParam String sourceBucketName, @RequestParam String targetBucketName, @RequestParam String key) {
        return service.copyFileToFavorites(sourceBucketName, targetBucketName, key);
    }

    @DeleteMapping("/media")
    public Boolean delete(@RequestParam String bucketName, @RequestParam String key) {
        return service.delete(bucketName, key);
    }

}
