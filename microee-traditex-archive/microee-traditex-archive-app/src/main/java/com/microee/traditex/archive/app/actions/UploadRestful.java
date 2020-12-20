package com.microee.traditex.archive.app.actions;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.microee.plugin.response.R;
import com.microee.traditex.archive.app.service.UploadService;

@RestController
@RequestMapping("/s3")
public class UploadRestful {

    @Autowired
    private UploadService uploadService;
    
    // 上传文件
    @RequestMapping(value = "/upload", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> upload(
            @RequestParam("uploadfile") MultipartFile uploadfile) throws IOException {
        try (InputStream input = new BufferedInputStream(uploadfile.getInputStream())) {
            return R.ok(uploadService.uploadToS3(uploadfile.getContentType(), uploadfile.getOriginalFilename(), input, uploadfile.getSize()));
        } 
    }

    // 读取临时文件url
    @RequestMapping(value = "/getS3Url", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> getS3Url(@RequestParam("fileId") String fileId) {
        return R.ok(uploadService.getS3Url(fileId, true).toString());
    }

}
