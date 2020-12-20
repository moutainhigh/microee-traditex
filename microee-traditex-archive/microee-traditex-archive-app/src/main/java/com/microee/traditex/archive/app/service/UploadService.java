package com.microee.traditex.archive.app.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.apache.tika.Tika;
import org.apache.tika.parser.txt.CharsetDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.microee.plugin.commons.UUIDObject;

@Component
public class UploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);
    
    /**
     * 亚马逊云 AccessKeyId
     */
    @Value("${aws.accessKey:AKIAJD6ISRKW4WZ62CXA}")
    private String accessKeyId;

    /**
     * 亚马逊云 SecretAccessKey
     */
    @Value("${aws.secretKey:zvWw7fVLsAgDnc5msLhyQViBDevIFjhGAIaWbzCW}")
    private String secretAccessKey;

    /**
     * 亚马逊云所属地区
     */
    @Value("${aws.region:ap-northeast-2}")
    private String region;

    /**
     * 亚马逊云BucketName
     */
    @Value("${aws.bucketName:chunhui-s3bucket}")
    private String bucketName;
    
    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region)
                .withClientConfiguration(new ClientConfiguration().withConnectionTimeout(120 * 1000)
                        .withSocketTimeout(120 * 1000))
                .build();
    }

    // 上传文件
    public String uploadToS3(String contentType, String fileName, InputStream input, long fileSize) throws IOException {
        ObjectMetadata omd = new ObjectMetadata();
        omd.setContentType(readContentType(input, contentType));
        omd.setContentLength(fileSize);
        omd.setHeader("filename", fileName);
        String objectId = UUIDObject.get().toString();
        s3Client.putObject(new PutObjectRequest(bucketName, objectId, input, omd));
        LOGGER.info("上传了一个文件至S3: region={}, bucketName={}", region, bucketName);
        return objectId;
    }
    
    public URL getS3Url(String objectId, boolean temp) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectId)
                .withMethod(HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + (3600 * 24 * 7) * 1000L));
        URL s3Url = null;
        if (temp) {
            s3Url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        } else {
            s3Url = s3Client.getUrl(bucketName, objectId);
        }
        return s3Url;
    }

    public String readContentType(InputStream input, String contentType) throws IOException {
        CharsetDetector detector = new CharsetDetector();
        detector.setText(input);
        if (detector.detect() != null) {
            try {
                return new Tika().detect(input) + ";charset=" + detector.detect().getName();
            } catch (NoSuchMethodError e) {
                return contentType;
            }
        } else {
            return new Tika().detect(input);
        }
    }
    
}
