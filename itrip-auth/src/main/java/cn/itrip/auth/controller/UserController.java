package cn.itrip.auth.controller;

import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * description:
 * Created by Ray on 2019-10-18
 */
@RestController
@RequestMapping("/api")
public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping(value = "/registerbyphone",method = RequestMethod.POST)
    public Dto registerByPhone(@RequestBody ItripUserVO userVO) {
        //验证手机号
        if (!validPhone(userVO.getUserCode())) {
            return DtoUtil.returnFail("请输入正确手机号", ErrorCode.AUTH_ILLEGAL_USERCODE);
        }
        //转换userVo 为ItripUser
        ItripUser itripUser = new ItripUser();
        BeanUtils.copyProperties(userVO,itripUser);
        //调用service 注册用户
        try {
            //验证用户是否已经存在
            ItripUser user=userService.getItripUserByUserCode(userVO.getUserCode());
            if(EmptyUtils.isNotEmpty(user)){
                return DtoUtil.returnFail("用户已经存在", ErrorCode.AUTH_USER_ALREADY_EXISTS);
            }
            //添加用户
            userService.itriptxCreateUser(itripUser);
            return DtoUtil.returnSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }

    }

    /**
     * 验证是否合法的手机号
     * @param phone
     * @return
     */
    private boolean validPhone(String phone) {
        String regex="^1[3578]{1}\\d{9}$";
        return Pattern.compile(regex).matcher(phone).find();
    }

    /**
     * 验证短信验证码
     * @param userCode  手机号
     * @param smsCode   短信验证码
     * @return
     */
    @RequestMapping(value = "/validatephone",method = RequestMethod.PUT)
    public Dto validatephone(@RequestParam("userCode") String userCode, @RequestParam("code") String smsCode) {
        Boolean b= null;
        try {
            b = userService.validatePhone(userCode, smsCode);
            if (b){
                return DtoUtil.returnSuccess("验证成功！");
            }else{
                return DtoUtil.returnSuccess("验证失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_ACTIVATE_FAILED);
        }



    }
}
