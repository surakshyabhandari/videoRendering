package com.example.Video.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageToVideoService {

    ResponseEntity<byte[]> convertImagesToVideo(List<MultipartFile> imageFiles, Integer framesPerSecond);

}
