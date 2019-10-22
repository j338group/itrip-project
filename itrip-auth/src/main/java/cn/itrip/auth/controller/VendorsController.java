package cn.itrip.auth.controller;

import cn.itrip.auth.service.TokenService;
import cn.itrip.auth.service.UserService;
import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripWechatTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.common.UrlUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * description:
 * Created by Ray on 2019-10-22
 */
@RestController
@RequestMapping("/vendors")
public class VendorsController {
    @Resource
    private UserService userService;

    @Resource
    private TokenService tokenService;

    @RequestMapping("/wechat/login")
    public void wechatLogin(HttpServletResponse response) throws IOException {
        String appId = "wx9168f76f000a0d4c";
        String redirectUri = "http://localhost:8080/auth/vendors/wechat/callback";
        String url="https://open.weixin.qq.com/connect/qrconnect?" +
                "appid=" +appId+
                "&redirect_uri=" + URLEncoder.encode(redirectUri,"UTF-8")+
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=STATE#wechat_redirect";
        response.sendRedirect(url);
    }
    @RequestMapping("/wechat/callback")
    public Dto callback(String code, String state, HttpServletRequest request){
        //code有效期10分钟，用于换取access_token
        String appId = "wx9168f76f000a0d4c";
        String secret="8ba69d5639242c3bd3a69dffe84336c1";
        System.out.println("code===="+code);
        String url="https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" +appId+
                "&secret=" +secret+
                "&code=" +code+
                "&grant_type=authorization_code";
        //请求微信平台换取access_token
        String jsonStr = UrlUtils.loadURL(url);
        //判断是否得到用户授权
        Map<String,String> map = JSON.parseObject(jsonStr, Map.class);
        if (!map.containsKey("access_token")) {
            return DtoUtil.returnFail("用户未授权", ErrorCode.AUTH_AUTHENTICATION_FAILED);
        }
        //创建用户到数据库
        try {
            //判断是否第一次登陆
            String openid = map.get("openid");
            ItripUser user=userService.getItripUserByUserCode(openid);
            if(user==null){
                //创建新用户
                user = new ItripUser();
                user.setUserName(openid);
                user.setUserCode(openid);
                user.setActivated(1);
                user.setCreationDate(new Date());
                user.setUserType(1);
                userService.vendorCreateUser(user);
            }
            //生成本地token
            String token = tokenService.generateToken(request.getHeader("user-agent"), user);
            //保存本地token
            tokenService.save(token, user);
            //返回本地token及微信授权token到客户端
            long expTime=tokenService.TOKEN_EXPIRE*3600*1000;
            long genTime = System.currentTimeMillis();
            ItripWechatTokenVO wechatTokenVO = new ItripWechatTokenVO(token,expTime,genTime);
            wechatTokenVO.setAccessToken(map.get("access_token"));
            String expires_in = String.valueOf(map.get("expires_in"));
            wechatTokenVO.setExpiresIn(expires_in);
            wechatTokenVO.setOpenid(map.get("openid"));
            wechatTokenVO.setRefreshToken(map.get("refresh_token"));

            return DtoUtil.returnDataSuccess(wechatTokenVO);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("获取用户授权失败", ErrorCode.AUTH_UNKNOWN);
        }

    }
    @RequestMapping("/wechat/user/info")
    public Dto getWechatUserInfo(HttpServletRequest request){
        //获取accessToken和openId
        String accessToken = request.getHeader("accessToken");
        String openId = request.getHeader("openId");
        //请求微信平台获取用户信息的接口
        String url="https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" +accessToken+
                "&openid="+openId;
        String jsonStr = UrlUtils.loadURL(url);
        Map<String,String> map = JSON.parseObject(jsonStr, Map.class);
        if(!map.containsKey("openid")){
            return DtoUtil.returnFail("获取微信用户信息失败", ErrorCode.AUTH_AUTHENTICATION_FAILED);
        }
        //修改用户信息
        try {
            ItripUser user = userService.getItripUserByUserCode(map.get("openid"));
            String nickname = map.get("nickname");
            user.setUserName(nickname);
            userService.updateUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回用信息给前端
        return DtoUtil.returnDataSuccess(map);

    }
}
