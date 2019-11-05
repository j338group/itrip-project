package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;
import com.alibaba.fastjson.JSON;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * description:
 * Created by Ray on 2019-10-21
 */
@Service
public class TokenServiceImpl implements TokenService {
    @Resource
    private RedisAPI redisAPI;
    /**
     * 生成token
     * @param agent
     * @param user
     * @return
     * @throws Exception
     */
    @Override
    public String generateToken(String agent, ItripUser user) throws Exception {
        //token:客户端标识-USERCODE-USERID-CREATIONDATE-RONDEM[6位]
        //token:PC-3066014fa0b10792e4a762-23-20170531133947-4f6496
        StringBuffer sb = new StringBuffer();
        sb.append(this.TOKEN_PREFIX);
        DeviceType deviceType = UserAgent.parseUserAgentString(agent).getOperatingSystem().getDeviceType();
        if(deviceType.equals(DeviceType.MOBILE)){
            sb.append("MOBILE");
        }else{
            sb.append("PC");
        }
        sb.append("-");
        sb.append(MD5.getMd5(user.getUserCode(), 32));
        sb.append("-");
        sb.append(user.getId());
        sb.append("-");
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        sb.append("-");
        sb.append(MD5.getMd5(agent, 6));
        return sb.toString();
    }

    /**
     * 保存token到redis
     * @param token
     * @param user
     * @throws Exception
     */
    @Override
    public void save(String token, ItripUser user) throws Exception {
        if (token.startsWith(this.TOKEN_PREFIX+"MOBILE")) {
            redisAPI.set(token, JSON.toJSONString(user));
        }else{
            redisAPI.set(token, this.TOKEN_EXPIRE * 60 * 60, JSON.toJSONString(user));
        }
    }

    /**
     * 验证token有效性
     * @param token
     * @param agent
     * @return
     * @throws Exception
     */
    @Override
    public Boolean validate(String token, String agent) throws Exception {
//        String userJson = redisAPI.get(token);
        //验证是否同一个客户端
        if (!MD5.getMd5(agent,6).equals(token.split("-")[4])) {
            throw new Exception("不是同一个客户端,未登录");
        }
        //验证是否token失效
        if (!redisAPI.exist(token)) {
            throw new Exception("token失效，未登录");
        }
        return true;
    }

    /**
     * 删除token
     * @param token
     * @throws Exception
     */
    @Override
    public void del(String token) throws Exception {
        redisAPI.delete(token);
    }

    /**
     * 置换token
     * @param token
     * @param agent
     * @return
     * @throws Exception
     */
    @Override
    public String reload(String token, String agent) throws Exception {
        //获取当前登录用户信息
        String userJson = redisAPI.get(token);
        ItripUser user = JSON.parseObject(userJson, ItripUser.class);
        //是否在保护期，在保护期不允许置换
        String genTime=token.split("-")[3];
//        LocalDateTime time = LocalDateTime.parse(genTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        time.
        long tokenGenTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(genTime).getTime();
        if((new Date().getTime())-tokenGenTime<this.TOKEN_PROTECTED_TIME*60*1000){
            throw new Exception("token保护期，不允许置换");
        }
        //生成新token
        String newToken = this.generateToken(agent, user);
        //旧token延时过期
        redisAPI.set(token, 2 * 60, userJson);
        //保存新token
        this.save(newToken, user);

        return newToken;
    }
}
