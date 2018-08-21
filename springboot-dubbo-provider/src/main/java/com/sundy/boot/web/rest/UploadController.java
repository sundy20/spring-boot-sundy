package com.sundy.boot.web.rest;

import com.sundy.share.dto.Result;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author plus.wang
 * @description 上传控制器
 * @date 2018/5/8
 */
@RestController
@Api(value = "UploadController", description = "文件上传控制器)")
public class UploadController {

    @RequestMapping(value = "/rest/file/upload", method = RequestMethod.POST)
    @ApiOperation(value = "文件上传", httpMethod = "POST", response = Result.class)
    @ApiResponses({
            @ApiResponse(code = 500, message = "系统服务异常")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uploadFile", value = "上传文件", required = true),
            @ApiImplicitParam(name = "fileName", value = "文件名", required = true)
    })
    public Result<String> uploadFile(HttpServletRequest request) {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        MultipartFile multipartFile = multipartRequest.getFile("uploadFile");

        String fileName = request.getParameter("fileName");

        return Result.success(null);
    }
}
