package com.sky.config;

import com.sky.properties.QiNiuOssProperties;
import com.sky.utils.QiNiuOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @FileName OssConfiguration
 * @Description 用于创建QiNiuOss对象
 * @Author xb
 * @date 2024-09-11
 **/
@Configuration
@Slf4j
public class OssConfiguration {
    /**
    * 自动执行，用QiNiuOssProperties参数生成一个QiNiuOSSUtil的bean，并交给IOC容器管理
    * @param qiNiuOssProperties
    * @return QiNiuOSSUtil
    * @Date 2024/9/11 16:59
    */
    @Bean
    @ConditionalOnMissingBean //只有一个QiNiuOSSUtil对象
    public QiNiuOSSUtil qiNiuOSSUtil(QiNiuOssProperties qiNiuOssProperties) {
        //log.info("开始创建阿里云文件上床工具类对象:{}", qiNiuOssProperties);
        QiNiuOSSUtil qiNiuOSSUtil = QiNiuOSSUtil.builder()
                .accessKeyId(qiNiuOssProperties.getAccessKeyId())
                .accessKeySecret(qiNiuOssProperties.getAccessKeySecret())
                .imageUrl(qiNiuOssProperties.getImageUrl())
                .path(qiNiuOssProperties.getPath())
                .bucketName(qiNiuOssProperties.getBucketName())
                .build();
        return qiNiuOSSUtil;
    }
}
