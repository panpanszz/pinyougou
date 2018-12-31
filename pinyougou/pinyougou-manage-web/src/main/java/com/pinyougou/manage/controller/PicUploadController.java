package com.pinyougou.manage.controller;

import com.pinyougou.common.util.FastDFSClient;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/upload")
@RestController
public class PicUploadController {

    /**
     * 接收图片文件保存到FastDFS并返回图片地址
     * @param file 图片文件
     * @return 操作结果
     */
    @PostMapping
    public Result upload(MultipartFile file){
        Result result = Result.fail("上传图片失败");

        try {
            //上传文件
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");

            //文件的扩展名（后缀）
            String fileExtName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            //参数1：上传的文件的字节数组；参数2：文件扩展名
            String url = fastDFSClient.uploadFile(file.getBytes(), fileExtName);

            result = Result.ok(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
