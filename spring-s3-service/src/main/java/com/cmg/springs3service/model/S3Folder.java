package com.cmg.springs3service.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class S3Folder implements Comparable<S3Folder> {
    private final String name;

    @Override
    public int compareTo(S3Folder o) {
        return this.getName().compareTo(o.getName());
    }
}
