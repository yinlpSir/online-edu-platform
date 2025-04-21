package com.jh.education.common.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
public class COSUtil {

    private COSUtil() {
    }

    private static final String BUCKET_NAME = "";
    private static final COSClient COS_CLIENT;

    static {
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials("accessKey", "secretKey");
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region("regionName"));
        // 生成cos客户端
        COS_CLIENT = new COSClient(cred, clientConfig);
    }

    public static void uploadFile(String key, MultipartFile file) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, file.getInputStream(), metadata);
            PutObjectResult putObjectResult = COS_CLIENT.putObject(putObjectRequest);
            log.info(putObjectResult.getRequestId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileUrl(String key) {
        return COS_CLIENT.getObjectUrl(BUCKET_NAME, key).toString();
    }

    public static void deleteFile(String key) {
        COS_CLIENT.deleteObject(BUCKET_NAME, key);
    }
}