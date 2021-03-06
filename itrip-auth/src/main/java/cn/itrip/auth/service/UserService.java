package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;

/**
 * description:
 * Created by Ray on 2019-10-18
 */
public interface UserService {
    void itriptxCreateUser(ItripUser user)throws Exception;

    ItripUser getItripUserByUserCode(String userCode) throws Exception;

    Boolean validatePhone(String userCode, String smsCode) throws Exception;

    ItripUser login(String userCode, String userPassword)throws Exception;

    void vendorCreateUser(ItripUser user) throws Exception;

    void updateUser(ItripUser user) throws Exception;
}
