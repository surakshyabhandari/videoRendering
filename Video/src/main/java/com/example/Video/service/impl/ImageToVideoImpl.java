package com.example.Video.service.impl;

import com.example.Video.service.ImageToVideoService;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageToVideoImpl implements ImageToVideoService {

    @Override
    public ResponseEntity<byte[]> convertImagesToVideo(List<MultipartFile> imageFiles, Integer framesPerSecond) {

        try{
            String tempDir = UUID.randomUUID().toString();
            File dir = new File(tempDir);
            dir.mkdir();

            // Save the image files to the temporary directory
            List<String> imagePaths = new ArrayList<>();
            for (MultipartFile imageFile : imageFiles) {
                String imagePath = tempDir + "/" + imageFile.getOriginalFilename();
                imagePaths.add(imagePath);
                File file = new File(imagePath);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(imageFile.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Generate the output video file name
            String outputVideo = UUID.randomUUID().toString() + ".mp4";

            // Set up the FFmpegFrameRecorder for video output
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputVideo, framesPerSecond);
            recorder.setVideoCodecName("libx264");
            recorder.setVideoQuality(0);
            recorder.setFrameRate(framesPerSecond);

            // Start the recording process
            recorder.start();

            // Convert and write each image frame to the video
            Java2DFrameConverter converter = new Java2DFrameConverter();
            for (String imagePath : imagePaths) {
                BufferedImage image = ImageIO.read(new File(imagePath));
                recorder.record(converter.convert(image));
            }

            // Stop the recording process and release resources
            recorder.stop();
            recorder.release();

            // Read the output video file and return it as a response
            File videoFile = new File(outputVideo);
            byte[] videoData = Files.readAllBytes(videoFile.toPath());
            videoFile.delete();

            // Delete the temporary image files and directory
            for (String imagePath : imagePaths) {
                File imageFile = new File(imagePath);
                imageFile.delete();
            }
            dir.delete();

            return ResponseEntity.ok()
                    .contentLength(videoData.length)
                    .header("Content-Disposition", "attachment; filename=" + outputVideo)
                    .body(videoData);


        }
        catch (FFmpegFrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
