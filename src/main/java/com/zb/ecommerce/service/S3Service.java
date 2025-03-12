package com.zb.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
  private final S3Client s3Client;
  private final String keyPrefix = "images/";

  @Value("${aws.bucket}")
  private String bucketName;

  public String uploadFile(InputStream inputStream, String fileName, String contentType) throws IOException {
    String newFileName = UUID.randomUUID() + "-" + fileName;

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(keyPrefix + newFileName)
            .contentType(contentType)
            .build();

    System.out.println(contentType);
    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
    return "https://d2p686p8ijr9ga.cloudfront.net/" + newFileName;
  }

  public void deleteFile(String fileName) {
    String newFileName = fileName.substring(38);
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(keyPrefix + newFileName)
            .build();
    s3Client.deleteObject(deleteObjectRequest);
  }

}
