package com.example.Video.controller;

import com.example.Video.service.ImageToVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
public class ImageToVideoController {

    private final ImageToVideoService imageToVideoService;

    public ImageToVideoController(ImageToVideoService imageToVideoService) {
        this.imageToVideoService = imageToVideoService;
    }

    @PostMapping(value = "/convert")
    public ResponseEntity<byte[]> convertImagesToVideo(@ModelAttribute List<MultipartFile> imageFiles, @RequestParam("fps") Integer framesPerSecond){
        log.info("Convert images to video :: [{}] [{}]", imageFiles, framesPerSecond);
        return imageToVideoService.convertImagesToVideo(imageFiles,framesPerSecond);
    }
}
