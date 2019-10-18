package cn.itrip.auth.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.common.DtoUtil;
import io.swagger.annotations.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * description:
 * Created by Ray on 2019-10-17
 */
@Api
@Controller
@RequestMapping("/api")
public class TestController {
    @ApiOperation(value ="登录验证",notes = "成功后返回success，失败返回fail",produces = "application/json",httpMethod = "POST")
//    @ApiResponse(code = 200,message = "成功执行")
    @ApiResponses(
            { @ApiResponse(code = 200,message = "成功执行"),
            @ApiResponse(code = 500,message = "服务器异常")}
    )
    @RequestMapping(value = "/user/login",method = RequestMethod.POST)
    @ResponseBody
    public Dto testLogin(
            @ApiParam(name = "uname",value = "用户名",defaultValue = "tom",required = true)
            @RequestParam(value = "uname",required = true) String uname,
            @ApiParam(name = "password",value = "用户密码",defaultValue = "123456",required = true)
            @RequestParam(value = "password",required = true)String password) {

        if("tom".equals(uname))
            return DtoUtil.returnSuccess();
        return DtoUtil.returnFail("用户名或密码错误","123456");

    }
}
