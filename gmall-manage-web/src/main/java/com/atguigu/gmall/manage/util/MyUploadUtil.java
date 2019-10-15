package com.atguigu.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MyUploadUtil {
    public static String upload_image(MultipartFile multipartFile) throws IOException, MyException {
        String trackerPath=MyUploadUtil.class.getClassLoader().getResource("tracker.conf").getPath();
        try {
            ClientGlobal.init(trackerPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient=new TrackerClient();
        TrackerServer trackerServer=null;
        try {
            trackerServer=trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageClient storageClient=new StorageClient(trackerServer,null);
        String url="http://120.78.175.32";
        String orginalFilename=multipartFile.getOriginalFilename();
        try {
            byte[] bytes=multipartFile.getBytes();
            int lastPoint=orginalFilename.lastIndexOf(".");
            String ext_name=orginalFilename.substring(lastPoint+1);
            String[] upload_file = storageClient.upload_file(bytes, ext_name, null);
            for (String s : upload_file) {
                url=url+"/"+s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }
}
