package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @FileName QiNiuOssProperties
 * @Description
 * @Author xb
 * @date 2024-09-11
 **/
@Component
@ConfigurationProperties(prefix = "sky.qiniuoss")
@Data
public class QiNiuOssProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String imageUrl;
    private String path;
}
