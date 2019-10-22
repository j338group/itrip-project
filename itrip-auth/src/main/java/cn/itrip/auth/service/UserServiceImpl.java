package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.Constants;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisAPI;
import cn.itrip.mapper.itripUser.ItripUserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:
 * Created by Ray on 2019-10-18
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private ItripUserMapper itripUserMapper;
    @Resource
    private SmsService smsService;
    @Resource
    private RedisAPI redisAPI;

    /**
     * 创建新用户
     * @param user
     * @throws Exception
     */
    @Override
    public void itriptxCreateUser(ItripUser user) throws Exception {
        //把user写入mysql数据库
        user.setActivated(0);
        user.setCreationDate(new Date());
        user.setUserType(0);
        user.setUserPassword(MD5.getMd5(user.getUserPassword(),32));
        itripUserMapper.insertItripUser(user);
        //掉SMS发送验证码
        int code = MD5.getRandomCode();
        //验证码有效期（分钟）
        int expire=1;
        smsService.send(user.getUserCode(),"1",new String[]{String.valueOf(code),String.valueOf(expire*60)});
        //缓存验证码
        redisAPI.set(Constants.POHONE_SMS_ACTIVE_PREFIX + user.getUserCode(), expire * 60, String.valueOf(code));
    }

    /**
     * 根据userCode查询ItripUser
     * @param userCode  phone or email
     * @return
     * @throws Exception
     */
    @Override
    public ItripUser getItripUserByUserCode(String userCode) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("userCode", userCode);
        List<ItripUser> users = itripUserMapper.getItripUserListByMap(param);

        if(users.size()==1&&users.get(0)!=null)
            return users.get(0);
        return null;
    }

    /**
     * 验证手机短信验证码，更新激活状态
     * @param userCode  手机号
     * @param smsCode   短信验证码
     * @return
     * @throws Exception
     */
    @Override
    public Boolean validatePhone(String userCode, String smsCode) throws Exception {
        ItripUser user = this.getItripUserByUserCode(userCode);
        //获取缓存的验证码
        String cacheCode = redisAPI.get(Constants.POHONE_SMS_ACTIVE_PREFIX + userCode);
        //比较验证码
        if(cacheCode!=null&&cacheCode.equals(smsCode)){
            //验证成功，修改激活状态
            user.setActivated(1);
            //平台id
            user.setFlatID(user.getId());
            //用户类型
//            user.setUserType(0);
//            user.setCreationDate(new Date());
            //更新用户
            itripUserMapper.updateItripUser(user);
            return true;
        }
        //激活失败，删除用户记录
        itripUserMapper.deleteItripUserById(user.getId());
        return false;
    }

    /**
     * 登录验证
     * @param userCode
     * @param userPassword
     * @return
     * @throws Exception
     */
    @Override
    public ItripUser login(String userCode, String userPassword) throws Exception {
        ItripUser user = this.getItripUserByUserCode(userCode);
        if (user != null && user.getUserPassword().equals(MD5.getMd5(userPassword, 32))) {
            if (user.getActivated() != 1) {
                throw new Exception("用户未激活");
            } else {
                return user;
            }

        }
        return null;
    }

    /**
     * 第三方登录创建用户
     * @param user
     * @throws Exception
     */
    @Override
    public void vendorCreateUser(ItripUser user) throws Exception {
        itripUserMapper.insertItripUser(user);
    }

    @Override
    public void updateUser(ItripUser user) throws Exception {
        itripUserMapper.updateItripUser(user);
    }
}
