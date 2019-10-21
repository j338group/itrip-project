package cn.itrip.auth.controller;

import cn.itrip.auth.service.TokenService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class TokenController {
    @Resource
    private TokenService tokenService;
    @RequestMapping(value = "/reload",method = RequestMethod.POST,headers = "token")
    public Dto reloadToken(HttpServletRequest request){
        String token = request.getHeader("token");
        String agent = request.getHeader("user-agent");
        //验证旧token
        try {
            if (tokenService.validate(token, agent)) {
                //置换token
                String newToken=tokenService.reload(token,agent);
                //返回新token
                long expTime = tokenService.TOKEN_EXPIRE * 60 * 60 * 1000;
                long genTime = new Date().getTime();
                return DtoUtil.returnDataSuccess(new ItripTokenVO(newToken, expTime, genTime));
            }else{
                return DtoUtil.returnFail("token无效", ErrorCode.AUTH_TOKEN_INVALID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_REPLACEMENT_FAILED);
        }

    }
}
