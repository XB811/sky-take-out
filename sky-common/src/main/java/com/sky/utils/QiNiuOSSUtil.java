package com.sky.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @FileName QiniuOSSUtil
 * @Description 七牛云OSS上传工具
 * @Author xb
 * @date 2024-09-11
 **/

@Data
@AllArgsConstructor
@Slf4j
@Builder
public class QiNiuOSSUtil {

    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String imageUrl;
    private String path;

    public String upload(byte[] bytes,String objectName){
        //构建一个uploadManager,
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        // 创建上传者
        Auth auth = Auth.create(accessKeyId, accessKeySecret);

        // 创建日期目录分隔
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String datePath = path+"/"+dateFormat.format(new Date());

        // 在文件名前添加路径
        String filename =  datePath+"/"+ objectName;//生成文件名

        //设置上传者的上传仓库
        String upToken = auth.uploadToken(bucketName);
        //上传文件
        try {
            uploadManager.put(bytes,filename,upToken);
        } catch (QiniuException e) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("Error Code: " + e.code());

        }
        log.info("文件上传到:{}",imageUrl+"/"+filename);
        return imageUrl+"/"+filename;
    }
}
