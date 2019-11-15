package cn.itrip.auth.controller;

import cn.itrip.auth.service.TokenService;
import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * description:
 * Created by Ray on 2019-10-21
 */
@RestController
@RequestMapping("/api")
public class LoginController {
    @Resource
    private UserService userService;
    @Resource
    private TokenService tokenService;

    @RequestMapping(value = "/dologin", method = RequestMethod.POST)
    public Dto doLogin(@RequestParam(value = "name", required = true) String userCode,
                       @RequestParam(value = "password", required = true) String userPassword, HttpServletRequest request) {
        //获取useragent信息
        String agent = request.getHeader("User-Agent");

        //验证用户名密码
        if (userCode == null || userPassword == null) {
            return DtoUtil.returnFail("用户名密码不能为空", ErrorCode.AUTH_PARAMETER_ERROR);
        }
        try {
            //验证登录
            ItripUser user = userService.login(userCode, userPassword);
            if (EmptyUtils.isNotEmpty(user)) {
                //生成token
                String token = tokenService.generateToken(agent, user);
                //缓存token
                tokenService.save(token, user);
                //发送token到前端
                long expTime = new Date().getTime()+tokenService.TOKEN_EXPIRE * 60 * 60 * 1000;
                long genTime = new Date().getTime();
                return DtoUtil.returnDataSuccess(new ItripTokenVO(token, expTime, genTime));
            } else {
                return DtoUtil.returnFail("用户名或密码错误！", ErrorCode.AUTH_PARAMETER_ERROR);
            }
        } catch (Exception e) {
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_AUTHENTICATION_FAILED);
        }

    }

    /**
     * 注销功能
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET, headers = "token")
    public Dto doLogout(HttpServletRequest request) {
        String token = request.getHeader("token");
        String agent = request.getHeader("user-agent");
        if (token == null || agent == null) {
            return DtoUtil.returnFail("无token信息", ErrorCode.AUTH_PARAMETER_ERROR);
        }
        //验证token有效性
        try {
            Boolean isOk = tokenService.validate(token, agent);
            if (isOk) {
                //退删除缓存中的token信息
                tokenService.del(token);
                return DtoUtil.returnSuccess("退出成功！");
            } else {
                return DtoUtil.returnSuccess("token无效");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }


    }
}
