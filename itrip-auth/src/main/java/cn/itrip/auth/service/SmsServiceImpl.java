package cn.itrip.auth.service;

import cn.itrip.common.SystemConfig;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;

/**
 * description:
 * Created by Ray on 2019-10-18
 */
@Service
public class SmsServiceImpl implements SmsService {
    @Resource
    private SystemConfig systemConfig;

    /**
     * 调用荣联云SMS平台给指定手机发送验证码
     * @param to
     * @param templateId
     * @param datas
     * @throws Exception
     */
    @Override
    public void send(String to, String templateId, String[] datas) throws Exception {
        HashMap<String, Object> result = null;
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
        restAPI.init(systemConfig.getSmsServerIP(), systemConfig.getSmsServerPort());
        restAPI.setAccount(systemConfig.getSmsAccountSid(), systemConfig.getSmsAuthToken());
        restAPI.setAppId(systemConfig.getSmsAppID());
        //调用sms云平台发送短信
        result = restAPI.sendTemplateSMS(to,templateId ,datas);

        System.out.println("SDKTestGetSubAccounts result=" + result);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            System.out.println("发送成功！");
        }else{
//            //异常返回输出错误码和错误信息
//            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            throw  new Exception(result.get("statusCode").toString()+":"+result.get("statusMsg"));
        }
    }
}
