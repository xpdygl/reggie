package com.huixu.controller;

import com.huixu.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @author XuHui
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonFileUploadController {

    //文件上传保存目录
    @Value("${reggie.upload-path}")
    private String uploadPath;

    /**
     * 文件上传
     * @param file 用户上传的文件
     * @return
     */
    @PostMapping("/upload")
    public R upload(MultipartFile file) throws IOException {

        //1.获取原文件名称 得到其后缀
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        //设置上传文件的新文件吗， 防止和之前的文件名同名 导致被覆盖

        String newFileName = UUID.randomUUID().toString().replace("-", "");
        //2 拼接得到完整的文件名称 包含后缀
        newFileName = newFileName+suffix;

        //3 判断文件上传目录是否存在，如果不存在就创建一个
        File directory = new File(uploadPath);
        if(!directory.exists()){
            directory.mkdir();
        }

        //4.设置文件上传的保存位置
        File newFile = new File(uploadPath+newFileName);

        //5.将上传的文件保存到指定的位置
        file.transferTo(newFile);
        return R.success(newFileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response 响应对象  将要下载的文件写给客户端浏览器   response.getWriter|response.getOutputStream
     */

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //IO读写
        //1.创建字节输入输出流
        InputStream is = new FileInputStream(new File(uploadPath + name));
        ServletOutputStream os = response.getOutputStream();

        //2.IO复制读写
        //2.1：定义一个字节数组作为缓冲区
        byte[] b = new byte[1024];
        //2.2：循环读写
        int len = -1;
        while ((len = is.read(b))!=-1){
            os.write(b,0,len);
        }

        os.close();
        is.close();
    }
}
