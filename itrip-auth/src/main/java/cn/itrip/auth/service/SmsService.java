package cn.itrip.auth.service;

/**
 * description:
 * Created by Ray on 2019-10-18
 */
public interface SmsService {
    void send(String to,String templateId,String[] datas) throws Exception;
}
