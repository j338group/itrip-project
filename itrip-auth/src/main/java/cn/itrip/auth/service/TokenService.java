package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;

/**
 * description:
 * Created by Ray on 2019-10-21
 */
public interface TokenService {
    String TOKEN_PREFIX = "token:";
    Integer TOKEN_EXPIRE=2;//小时
    Integer TOKEN_PROTECTED_TIME=60;//分钟
    String generateToken(String agent, ItripUser user) throws Exception;

    void save(String token, ItripUser user)throws Exception;

    Boolean validate(String token, String agent) throws Exception;

    void del(String token)throws Exception;

    String reload(String token, String agent) throws Exception;
}
