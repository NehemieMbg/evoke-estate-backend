package _6nehemie.com.evoke_estate.services;

import _6nehemie.com.evoke_estate.dto.S3UploadDto;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String s3BucketName;


    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public S3UploadDto uploadFile(MultipartFile file) {
        
        // ? Create a unique file key
        String fileKey = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        
        try {
            // ? Store the file in S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            s3Client.putObject(new PutObjectRequest(s3BucketName, fileKey, file.getInputStream(), metadata));
            
            // ? Get the file URL
            String fileUrl = s3Client.getUrl(s3BucketName, fileKey).toString();
            
            // ? Return the file URL and key
            return new S3UploadDto(fileUrl, fileKey);
            
        } catch (IOException e) {
            throw new RuntimeException("Error storing file to S3", e);
        }
    }
    
    public void deleteFile(String fileKey) {
        s3Client.deleteObject(s3BucketName, fileKey);
    }
}
