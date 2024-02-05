package com.example.backend.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class S3FileUploadService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client amazonS3Client;
    private final AmazonS3 s3Client;

    public String store(String artist, String fullPath, MultipartFile multipartFile, String username) {
        LocalDate localDate = LocalDate.now();
        String folderKey = "post/"+artist+"/"+username+"/"+localDate+"/";
        return saveImg(fullPath, multipartFile, folderKey);
    }

    public String saveSellerImg(String fullPath, MultipartFile multipartFile) {
        String folderKey = "/sellerProfile";
        return saveImg(fullPath, multipartFile, folderKey);
    }

    @NotNull
    private String saveImg(String fullPath, MultipartFile multipartFile, String folderKey) {
        String objectKey = folderKey + fullPath;

        File file = new File(MultipartUtil.getLocalHomeDirectory(), fullPath);

        try {
            multipartFile.transferTo(file);
            amazonS3Client.putObject(new PutObjectRequest(bucket, objectKey, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
        return objectKey;
    }

    public void deleteMissingImages(List<String> deleteList){
        for(String imgKey : deleteList){
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, imgKey);
            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}
