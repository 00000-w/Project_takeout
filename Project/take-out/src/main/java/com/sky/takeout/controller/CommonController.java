package com.sky.takeout.controller;

import com.sky.takeout.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.font.MultipleMaster;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 公共接口：图片上传等
 */

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {
    //从yaml读取上传路径
    @Value("${sky.upload.path}")
    private String uploadPath;

    /**
     * 图片上传
     * 前端传来的文件叫"file"
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        //校验文件是否为空
        if (file.isEmpty())
            return Result.error("上传文件不能为空");

        //提取原始文件名
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null)
            return Result.error("文件名不能为空");
        //文件后缀
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));

        //使用UUID生成唯一文件名，防止文件名冲突
        String newFileName = UUID.randomUUID().toString() + suffix;

        //创建上传目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //保存文件到磁盘
        try {
            file.transferTo(new File(uploadPath + newFileName));
            log.info("文件上传成功：{}", newFileName);
            return Result.success("/uploads/" + newFileName);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
            return Result.error("文件上传失败，请重试");
        }
    }
}
