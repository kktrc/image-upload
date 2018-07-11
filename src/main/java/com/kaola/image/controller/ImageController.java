package com.kaola.image.controller;

import com.kaola.image.bean.Image;
import com.kaola.image.bean.Response;
import com.kaola.image.util.DateUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;

@Controller
@RequestMapping(value = "/image")
public class ImageController {


    @Value("${image.path}")
    private String basePath;


    /**
     *
     * 读取图片
     * @param path
     * @param filePath
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/{path}/{file}")
    public void imagePath(@PathVariable(value = "path") String path,
                          @PathVariable(value = "file") String filePath, HttpServletResponse response) throws Exception {


        String year = path.substring(0, 4);
        String month = path.substring(4, 6);
        String day = path.substring(6);

        File file = new File(basePath
                + year
                + File.separator
                + month
                + File.separator
                + day
                + File.separator
                + filePath
        );

        OutputStream outputStream = response.getOutputStream();

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        Files.copy(file.toPath(), outputStream);
    }


    /**
     *
     * 上传文件
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Object upload(@RequestParam("file")MultipartFile file) throws Exception {

        Date now = new Date();

        String year = DateUtil.format(now, "yyyy");
        String month = DateUtil.format(now, "MM");
        String day = DateUtil.format(now, "dd");

        //先判断年的文件夹是否存在
        File yearDir = new File(basePath + year);
        if (!yearDir.exists()) {
            yearDir.mkdir();
        }

        //判断月份的文件夹是否存在
        File monthDir = new File(basePath
                + File.separator
                + year
                + File.separator
                + month
        );
        if (!monthDir.exists()) {
            monthDir.mkdir();
        }

        //判断天的文件夹是否存在
        File dayDir = new File(basePath
                + File.separator
                + year
                + File.separator
                + month
                + File.separator
                + day
        );
        if (!dayDir.exists()) {
            dayDir.mkdir();
        }

        String timestamp = String.valueOf(System.currentTimeMillis());

        String origin = file.getOriginalFilename();
        String ext = origin.substring(origin.indexOf("."));

        File outputFile = new File(dayDir.getAbsolutePath() + File.separator + timestamp + ext);

        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            IOUtils.copy(file.getInputStream(), outputStream);
        }

        Image image = new Image();
        image.setPath("/image/" + DateUtil.format(now, "yyyyMMdd") + "/" + timestamp + ext);

        Response<Image> resp = new Response<>();
        resp.setData(image);
        return resp;
    }
}
